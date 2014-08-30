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

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents an operations recorder that holds data in basic concurrent maps structures
 *
 * @author franky
 */
public class BasicInMemoryRecorder implements OperationRecorder {

    private static final int DEFAULT_LIMIT = 100000;
    private final int maximunRecords;
    private volatile int currentRecords;

    private final ConcurrentLinkedQueue<Record> queue;

    /**
     * Creates an new instance with a default limit of 100K records.
     */
    public BasicInMemoryRecorder() {
        this(BasicInMemoryRecorder.DEFAULT_LIMIT);
    }

    /**
     * Creates an new instance with a given limit of records to hold.
     * @param maximunRecords An integer value with the maximum number of records
     */
    public BasicInMemoryRecorder(final int maximunRecords) {
        this.maximunRecords = maximunRecords;
        this.queue = new ConcurrentLinkedQueue();
        this.currentRecords = 0;
    }

    @Override
    public void record(final String caller, final MeasuredOperations operation, final long value) {
        // check limit
        if (currentRecords >= maximunRecords) {
            return;
        }
        // record
        this.queue.add(new OperationRecord(caller, operation, value));
        // increase current records state
        currentRecords++;

    }

    @Override
    public Iterable<Record> records() {
        return this.queue;
    }

    @Override
    public void reset() {
        this.queue.clear();
        this.currentRecords = 0;
    }

}
