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
 * Represents a simple operation record. This Class intents to be immutable.
 *
 * @author franky
 */
public class OperationRecord implements Record {

    private final String caller;
    private final int operation;
    private final long value;

    /**
     * Hide empty constructor
     */
    private OperationRecord() {
        this.caller = null;
        this.operation = 0;
        this.value = 0;
    }


    /**
     * Creates an instance of the record
     * @param caller
     * @param operation
     * @param value
     */
    public OperationRecord(final String caller, final MeasuredOperations operation, long value) {
        this.caller = caller;
        this.operation = operation.ordinal();
        this.value = value;
    }

    @Override
    public String caller() {
        return caller;
    }

    @Override
    public MeasuredOperations operation() {
        return MeasuredOperations.values()[operation];
    }

    @Override
    public long value() {
        return value;
    }

}
