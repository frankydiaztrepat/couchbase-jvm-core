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
 * Represents the basic contract to publish Measurements
 * @author franky
 */
public interface Publisher {

    /**
     * Publishes a range of records given to the publisher.
     * @param records The records to be published.
     */
    public void publishRecords(final Iterable<Record> records);
    
    /**
     * Publishes the given map considering the key as the X (reason/object/measure) for which the corresponding Histogram was assigned.
     * @param histograms For now an org.HdrHistogram.Histogram instance. NOTE: This should and will be a interface.
     */
    public void publishHistograms(final Map<String, Histogram> histograms);
}
