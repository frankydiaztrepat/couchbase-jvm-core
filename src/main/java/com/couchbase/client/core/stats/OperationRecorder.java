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
 * Represents a basic operation recording functionality. It records MeasurementOperations with a timestamp.
 * @author franky
 */
public interface OperationRecorder {

    /**
     * Records the given operation
     * @param caller the caller who originated the record, or for which the value is being recorded.
     * @param operation the MeasureOperation for which the recorded value is actually recorded for.
     * @param value A long containing the value of the record.
     */
    public void record(final String caller, final MeasuredOperations operation, final long value);

    /**
     * Returns an iterable object that can be use to iterate through the OperationRecord instances saved.
     * @return a Record implementing instance.
     */
    public Iterable<Record> records();
    
    /**
     * Resets the recorder to a cleared state, ready to begin recording again. 
     */
    public void reset();
}
