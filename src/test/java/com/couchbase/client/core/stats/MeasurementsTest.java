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

import com.couchbase.client.core.message.CouchbaseRequest;
import com.couchbase.client.core.message.CouchbaseResponse;
import com.couchbase.client.core.message.ResponseStatus;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import rx.subjects.Subject;

/**
 * Checks functionality of {@link Measurements}.
 *
 * @author franky
 */
public class MeasurementsTest {

    static class MeasurementRequest implements CouchbaseRequest {

        private long createdOn;
        private int instanceNumber;

        public MeasurementRequest(int index) {
            createdOn = System.currentTimeMillis();
            instanceNumber = index;
            Measurements.timeline().record(getClass().getName(), MeasuredOperations.CREATED, createdOn);
        }

        @Override
        public Subject<CouchbaseResponse, CouchbaseResponse> observable() {
            return null;
        }

        @Override
        public String bucket() {
            return "Request-Bucket-" + instanceNumber;
        }

        @Override
        public String password() {
            return "Request-Password-" + instanceNumber;
        }

        @Override
        public long createdOn() {
            return createdOn;
        }
    }

    static class MeasurementResponse implements CouchbaseResponse {

        private long createdOn;
        private int instanceNumber;
        private CouchbaseRequest request;

        public MeasurementResponse(int index, CouchbaseRequest request) {
            createdOn = System.currentTimeMillis();
            instanceNumber = index;
            this.request = request;
            Measurements.timeline().record(getClass().getName(), MeasuredOperations.CREATED, createdOn);
            Measurements.latency().record(getClass().getName(), MeasuredOperations.REQUEST_RESPONSE_CREATION_CYCLE, (createdOn-this.request.createdOn()) * 1000000);
        }

        @Override
        public ResponseStatus status() {
            return ResponseStatus.SUCCESS;
        }

        @Override
        public CouchbaseRequest request() {
            return request;
        }

    }

    public MeasurementsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        Random generator = new Random();

        for (int i = 0; i < 5 ;i++) {
            MeasurementRequest request = new MeasurementRequest(i);
            int millis = generator.nextInt(10000) + 1;
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ex) {
                Logger.getLogger(MeasurementsTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            MeasurementResponse response = new MeasurementResponse(i, request);
        }
    }

    @After
    public void tearDown() {
        Measurements.latency().reset();
        Measurements.timeline().reset();
        Measurements.volume().reset();
    }

    /**
     * Test of latency method, of class Measurements.
     */
    @Test
    public void testLatency() {
        System.out.println("latency");
        Publisher publisher = new ConsolePublisher();
        Measurements.latency().publish(publisher);
    }

    /**
     * Test of timeline method, of class Measurements.
     */
    @Test
    public void testTimeline() {
        System.out.println("timeline");
        Publisher publisher = new ConsolePublisher();
        Measurements.timeline().publish(publisher);
    }

    /**
     * Test of volume method, of class Measurements.
     */
    @Test
    @Ignore
    public void testVolume() {
        System.out.println("volume");
        OperationRecorder expResult = null;
        OperationRecorder result = Measurements.volume();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
