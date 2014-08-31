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

/**
 * Manages the recording, processing, and publishing of different metrics and measurements
 *
 * @author franky
 */
public class Measurements {
    private static final Measurements INSTANCE;
    private final OperationRecorder timelineRecorder;
    private final OperationRecorder latencyRecorder;
    private final OperationRecorder volumeRecorder;
    
    static {
        INSTANCE = new Measurements();
    }

    /**
     * Returns the latency recorder to trace operation latency. 
     * Values given to the recorder must reflect time span measurements.
     * @return An OperationRecorder implementing instance that records latency operations.
     */
    public static OperationRecorder latency() {
        return INSTANCE.latencyRecorder;
    }
    
    /**
     * Returns the time-line recorder to trace operations as they occur. 
     * Values given to the recorder must reflect timestamp measurements.
     * @return An OperationRecorder implementing instance that records operations as they happen on an execution time-line.
     */
    public static OperationRecorder timeline() {
        return INSTANCE.timelineRecorder;
    }
    
    /**
     * Returns the volume recorder to trace operation volume (request/response packet size, queue sizes, etc) as they increase and decrease. 
     * Values given to the recorder must reflect size measurements.
     * @return An OperationRecorder implementing instance that records volume for operations as they increase or decrease.
     */
    public static OperationRecorder volume() {
        return INSTANCE.volumeRecorder;
    }
    
    private Measurements() {
        this.timelineRecorder = new BasicInMemoryRecorder();
        this.latencyRecorder = new HdrHistogramLatencyRecorder();
        this.volumeRecorder = new HdrHistogramLatencyRecorder();
    }
}
