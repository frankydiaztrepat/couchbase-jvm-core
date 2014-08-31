/**
 * Copyright (C) 2014 Couchbase, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING IN THE SOFTWARE.
 */
package com.couchbase.client.core.stats;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramIterationValue;

/**
 * Represents a latency focused recorder that uses Gil Tene's High Dynamic Range Histogram to eliminate coordinated omissions.
 *
 * @author franky
 */
public class HdrHistogramLatencyRecorder implements OperationRecorder {

    private static final String CALLER_OPERATION_SEPARATOR = "#";
    private static final long ONE_MINUTE_IN_NANOSECONDS = 60000000000L;
    private static final int DEFAULT_LIMIT = 2000000;
    private final int maximunRecords;
    private volatile int currentRecords;
    private final ConcurrentHashMap<String, Histogram> histograms;

    /**
     * Creates an new instance with a default limit of 2 million records.
     */
    public HdrHistogramLatencyRecorder() {
        this(HdrHistogramLatencyRecorder.DEFAULT_LIMIT);
    }

    /**
     * Creates an new instance with a given limit of records to hold.
     *
     * @param maximunRecords An integer value with the maximum number of records
     */
    public HdrHistogramLatencyRecorder(final int maximunRecords) {
        this.histograms = new ConcurrentHashMap<String, Histogram>();
        this.maximunRecords = maximunRecords;
    }

    @Override
    public void record(final String caller, final MeasuredOperations operation, final long value) {

        // check limit
        if (currentRecords >= maximunRecords) {
            return;
        }

        if (this.histograms.get(caller + CALLER_OPERATION_SEPARATOR + operation.name()) == null) {
            this.histograms.put(caller + CALLER_OPERATION_SEPARATOR + operation.name(), new Histogram(ONE_MINUTE_IN_NANOSECONDS, 3));
        }

        this.histograms.get(caller + CALLER_OPERATION_SEPARATOR + operation.name()).recordValue(value);
        currentRecords++;
    }

    @Override
    public Iterable<Record> records() {
        final LinkedList<Record> allRecords = new LinkedList<Record>();
        for (String histogramKey : this.histograms.keySet()) {
            for (HistogramIterationValue recordedValue : this.histograms.get(histogramKey).recordedValues()) {
                String[] callerAndOperation = histogramKey.split(Pattern.quote(CALLER_OPERATION_SEPARATOR));
                allRecords.add(new OperationRecord(callerAndOperation[0], MeasuredOperations.valueOf(callerAndOperation[1]), recordedValue.getTotalValueToThisValue()));
            }
        }
        return allRecords;
    }

    @Override
    public void reset() {
        this.histograms.clear();
    }

    @Override
    public void publish(Publisher publisher) {
        publisher.publishHistograms(this.histograms);
    }

}
