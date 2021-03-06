/**
 * Copyright (C) 2014 Couchbase, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING
 * IN THE SOFTWARE.
 */
package com.couchbase.client.core.env;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DefaultCoreEnvironmentTest {

    @Test
    public void shouldInitAndShutdownCoreEnvironment() throws Exception {
        CoreEnvironment env = DefaultCoreEnvironment.create();
        assertNotNull(env.ioPool());
        assertNotNull(env.scheduler());

        assertEquals(DefaultCoreEnvironment.BINARY_ENDPOINTS, env.binaryEndpoints());
        assertTrue(env.shutdown().toBlocking().single());
    }

    @Test
    public void shouldOverrideDefaults() throws Exception {
        CoreEnvironment env = DefaultCoreEnvironment
            .builder()
            .binaryEndpoints(3)
            .build();
        assertNotNull(env.ioPool());
        assertNotNull(env.scheduler());

        assertEquals(3, env.binaryEndpoints());
        assertTrue(env.shutdown().toBlocking().single());
    }

    @Test
    public void sysPropertyShouldTakePrecedence() throws Exception {

        System.setProperty("com.couchbase.binaryEndpoints", "10");

        CoreEnvironment env = DefaultCoreEnvironment
            .builder()
            .binaryEndpoints(3)
            .build();
        assertNotNull(env.ioPool());
        assertNotNull(env.scheduler());

        assertEquals(10, env.binaryEndpoints());
        assertTrue(env.shutdown().toBlocking().single());

        System.clearProperty("com.couchbase.binaryEndpoints");
    }

}
