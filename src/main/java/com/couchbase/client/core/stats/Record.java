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
 * Represents the basic contract of a record of measurement. Caller, Operation, value.
 *
 * @author franky
 */
public interface Record {

    /**
     * Returns the caller who originated the record, usually the name of the object owning the value returned by #operation()
     *
     * @return A String with the value of the name of the caller/owner/origin of the operation
     */
    public String caller();

    /**
     * Returns the MeasureOperation for which the recorded value is actually recorded for.
     *
     * @return A MeasuredOperations Enum instance.
     */
    public MeasuredOperations operation();

    /**
     * The recorded value for the corresponding caller and operation.
     *
     * @return A long containing the value of the record
     */
    public long value();
}
