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

import java.util.Map;
import org.HdrHistogram.Histogram;

/**
 * Represents a Publisher implementation to write recorded measurements to the standard out stream.
 * @author franky
 */
public class ConsolePublisher implements Publisher {
    private int percentileTicksPerHalfDistance;
    private Double outputValueUnitScalingRatio;

    public ConsolePublisher() {
        this.outputValueUnitScalingRatio = 1000.0;
    }

    @Override
    public void publishRecords(Iterable<Record> records) {
        for(Record record:records)
            System.out.println(String.format("record for %s and operation %s with value %d ", record.caller(), record.operation().name(), record.value()));
    }

    @Override
    public void publishHistograms(Map<String, Histogram> histograms) {
        for (String histogramKey : histograms.keySet()) {
            System.out.println(String.format("Histogram for: %s", histogramKey));
            histograms.get(histogramKey).outputPercentileDistribution(System.out, getOutputValueUnitScalingRatio());
            System.out.println(String.format("Ended Output for: %s", histogramKey));
        }
    }

    /**
     * @return the percentileTicksPerHalfDistance
     */
    public int getPercentileTicksPerHalfDistance() {
        return percentileTicksPerHalfDistance;
    }

    /**
     * @param percentileTicksPerHalfDistance the percentileTicksPerHalfDistance to set
     */
    public void setPercentileTicksPerHalfDistance(int percentileTicksPerHalfDistance) {
        this.percentileTicksPerHalfDistance = percentileTicksPerHalfDistance;
    }

    /**
     * @return the outputValueUnitScalingRatio
     */
    public Double getOutputValueUnitScalingRatio() {
        return outputValueUnitScalingRatio;
    }

    /**
     * @param outputValueUnitScalingRatio the outputValueUnitScalingRatio to set
     */
    public void setOutputValueUnitScalingRatio(Double outputValueUnitScalingRatio) {
        this.outputValueUnitScalingRatio = outputValueUnitScalingRatio;
    }   
}
