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
public class Measurements implements OperationRecorder {
    private static final Measurements INSTANCE;
    private OperationRecorder operationsRecorder;
    
    static {
        INSTANCE = new Measurements();
    }

    /**
     * @return the INSTANCE
     */
    public static OperationRecorder recorder() {
        return INSTANCE;
    }
    
    private Measurements() {
        this.operationsRecorder = new BasicInMemoryRecorder();
    }
    
    @Override
    public void record(final String caller, final MeasuredOperations operation, final long value) {
        this.operationsRecorder.record(caller, operation, value);
    }

    @Override
    public Iterable<Record> records() {
        return this.operationsRecorder.records();
    }

    @Override
    public void reset() {
        this.operationsRecorder.reset();
    }

}
