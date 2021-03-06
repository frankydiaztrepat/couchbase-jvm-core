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
package com.couchbase.client.core.cluster;

import com.couchbase.client.core.message.ResponseStatus;
import com.couchbase.client.core.message.binary.CounterRequest;
import com.couchbase.client.core.message.binary.CounterResponse;
import com.couchbase.client.core.message.binary.GetRequest;
import com.couchbase.client.core.message.binary.GetResponse;
import com.couchbase.client.core.message.binary.InsertRequest;
import com.couchbase.client.core.message.binary.InsertResponse;
import com.couchbase.client.core.message.binary.RemoveRequest;
import com.couchbase.client.core.message.binary.RemoveResponse;
import com.couchbase.client.core.message.binary.ReplaceRequest;
import com.couchbase.client.core.message.binary.ReplaceResponse;
import com.couchbase.client.core.message.binary.TouchRequest;
import com.couchbase.client.core.message.binary.TouchResponse;
import com.couchbase.client.core.message.binary.UnlockRequest;
import com.couchbase.client.core.message.binary.UnlockResponse;
import com.couchbase.client.core.message.binary.UpsertRequest;
import com.couchbase.client.core.message.binary.UpsertResponse;
import com.couchbase.client.core.message.query.GenericQueryRequest;
import com.couchbase.client.core.message.query.GenericQueryResponse;
import com.couchbase.client.core.util.ClusterDependentTest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies basic functionality of binary operations.
 *
 * @author Michael Nitschinger
 * @since 1.0
 */
public class BinaryMessageTest extends ClusterDependentTest {

    @Test
    public void shouldUpsertAndGetDocument() throws Exception {
        String key = "upsert-key";
        String content = "Hello World!";
        UpsertRequest upsert = new UpsertRequest(key, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8), bucket());
        cluster().<UpsertResponse>send(upsert).toBlocking().single();

        GetRequest request = new GetRequest(key, bucket());

        assertEquals(content, cluster(). <GetResponse>send(request).toBlocking().single().content()
            .toString(CharsetUtil.UTF_8));
    }

    @Test
    public void shouldUpsertWithExpiration() throws Exception {
        String key = "upsert-key-vanish";
        String content = "Hello World!";
        UpsertRequest upsert = new UpsertRequest(key, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8), 1, 0, bucket());
        cluster().<UpsertResponse>send(upsert).toBlocking().single();

        Thread.sleep(2000);

        GetRequest request = new GetRequest(key, bucket());
        assertEquals(ResponseStatus.NOT_EXISTS, cluster().<GetResponse>send(request).toBlocking().single().status());
    }

    @Test
    public void shouldHandleDoubleInsert() {
        String key = "insert-key";
        String content = "Hello World!";
        InsertRequest insert = new InsertRequest(key, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8), bucket());
        assertEquals(ResponseStatus.SUCCESS, cluster().<InsertResponse>send(insert).toBlocking().single().status());

        insert = new InsertRequest(key, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8), bucket());
        assertEquals(ResponseStatus.EXISTS, cluster().<InsertResponse>send(insert).toBlocking().single().status());
    }

    @Test
    public void shouldReplaceWithoutCAS() {
        final String key = "replace-key";
        final String content = "replace content";

        ReplaceRequest insert = new ReplaceRequest(key, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8), bucket());
        assertEquals(ResponseStatus.NOT_EXISTS, cluster().<ReplaceResponse>send(insert).toBlocking().single().status());

        UpsertRequest upsert = new UpsertRequest(key, Unpooled.copiedBuffer("insert content", CharsetUtil.UTF_8), bucket());
        ReplaceResponse response = cluster().<UpsertResponse>send(upsert)
            .flatMap(new Func1<UpsertResponse, Observable<ReplaceResponse>>() {
                @Override
                public Observable<ReplaceResponse> call(UpsertResponse response) {
                    return cluster().send(new ReplaceRequest(key, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8), bucket()));
                }
            }
        ).toBlocking().single();

        assertEquals(ResponseStatus.SUCCESS, response.status());
    }

    @Test
    public void shouldReplaceWithFailingCAS() {
        final String key = "replace-key-cas-fail";
        final String content = "replace content";

        ReplaceRequest insert = new ReplaceRequest(key, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8), bucket());
        assertEquals(ResponseStatus.NOT_EXISTS, cluster().<ReplaceResponse>send(insert).toBlocking().single().status());

        UpsertRequest upsert = new UpsertRequest(key, Unpooled.copiedBuffer("insert content", CharsetUtil.UTF_8), bucket());
        ReplaceResponse response = cluster().<UpsertResponse>send(upsert)
            .flatMap(new Func1<UpsertResponse, Observable<ReplaceResponse>>() {
                @Override
                public Observable<ReplaceResponse> call(UpsertResponse response) {
                 return cluster().send(new ReplaceRequest(key, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8), 24234234L, bucket()));
                }
            }).toBlocking().single();

        assertEquals(ResponseStatus.EXISTS, response.status());
    }

    @Test
    public void shouldReplaceWithMatchingCAS() {
        final String key = "replace-key-cas-match";
        final String content = "replace content";

        ReplaceRequest insert = new ReplaceRequest(key, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8), bucket());
        assertEquals(ResponseStatus.NOT_EXISTS, cluster().<ReplaceResponse>send(insert).toBlocking().single().status());

        UpsertRequest upsert = new UpsertRequest(key, Unpooled.copiedBuffer("insert content", CharsetUtil.UTF_8), bucket());
        ReplaceResponse response = cluster().<UpsertResponse>send(upsert)
            .flatMap(new Func1<UpsertResponse, Observable<ReplaceResponse>>() {
                @Override
                public Observable<ReplaceResponse> call(UpsertResponse response) {
                    return cluster().send(new ReplaceRequest(key, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8), response.cas(), bucket()));
                }
            }).toBlocking().single();

        assertEquals(ResponseStatus.SUCCESS, response.status());
    }

    @Test
    public void shouldRemoveDocumentWithoutCAS() {
        String key = "remove-key";
        String content = "Hello World!";
        UpsertRequest upsert = new UpsertRequest(key, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8), bucket());
        assertEquals(ResponseStatus.SUCCESS, cluster().<UpsertResponse>send(upsert).toBlocking().single().status());

        RemoveRequest remove = new RemoveRequest(key, bucket());
        assertEquals(ResponseStatus.SUCCESS, cluster().<RemoveResponse>send(remove).toBlocking().single().status());
        GetRequest get = new GetRequest(key, bucket());
        assertEquals(ResponseStatus.NOT_EXISTS, cluster().<GetResponse>send(get).toBlocking().single().status());
    }

    @Test
    public void shouldRemoveDocumentWithCAS() {
        String key = "remove-key-cas";
        String content = "Hello World!";
        UpsertRequest upsert = new UpsertRequest(key, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8), bucket());
        UpsertResponse upsertResponse = cluster().<UpsertResponse>send(upsert).toBlocking().single();
        assertEquals(ResponseStatus.SUCCESS, upsertResponse.status());

        RemoveRequest remove = new RemoveRequest(key, 1233443, bucket());
        assertEquals(ResponseStatus.EXISTS, cluster().<RemoveResponse>send(remove).toBlocking().single().status());
        remove = new RemoveRequest(key, upsertResponse.cas(), bucket());
        assertEquals(ResponseStatus.SUCCESS, cluster().<RemoveResponse>send(remove).toBlocking().single().status());
    }

    @Test
    public void shouldIncrementFromCounter() {
        String key = "counter-incr";

        CounterResponse response1 = cluster().<CounterResponse>send(new CounterRequest(key, 0, 10, 0, bucket())).toBlocking().single();
        assertEquals(0, response1.value());

        CounterResponse response2 = cluster().<CounterResponse>send(new CounterRequest(key, 0, 10, 0, bucket())).toBlocking().single();
        assertEquals(10, response2.value());

        CounterResponse response3 = cluster().<CounterResponse>send(new CounterRequest(key, 0, 10, 0, bucket())).toBlocking().single();
        assertEquals(20, response3.value());

        assertTrue(response1.cas() != response2.cas());
        assertTrue(response2.cas() != response3.cas());
    }

    @Test
    public void shouldDecrementFromCounter() {
        String key = "counter-decr";

        CounterResponse response1 = cluster().<CounterResponse>send(new CounterRequest(key, 100, -10, 0, bucket())).toBlocking().single();
        assertEquals(100, response1.value());

        CounterResponse response2 = cluster().<CounterResponse>send(new CounterRequest(key, 100, -10, 0, bucket())).toBlocking().single();
        assertEquals(90, response2.value());

        CounterResponse response3 = cluster().<CounterResponse>send(new CounterRequest(key, 100, -10, 0, bucket())).toBlocking().single();
        assertEquals(80, response3.value());

        assertTrue(response1.cas() != response2.cas());
        assertTrue(response2.cas() != response3.cas());
    }

    @Test
    public void shouldGetAndTouch() throws Exception {
        String key = "get-and-touch";

        UpsertRequest request = new UpsertRequest(key, Unpooled.copiedBuffer("content", CharsetUtil.UTF_8), 3, 0, bucket());
        UpsertResponse response = cluster().<UpsertResponse>send(request).toBlocking().single();
        assertEquals(ResponseStatus.SUCCESS, response.status());

        Thread.sleep(2000);

        GetResponse getResponse = cluster().<GetResponse>send(new GetRequest(key, bucket(), false, true, 3)).toBlocking().single();
        assertEquals(ResponseStatus.SUCCESS, response.status());
        assertEquals("content", getResponse.content().toString(CharsetUtil.UTF_8));

        Thread.sleep(2000);

        getResponse = cluster().<GetResponse>send(new GetRequest(key, bucket(), false, true, 3)).toBlocking().single();
        assertEquals(ResponseStatus.SUCCESS, response.status());
        assertEquals("content", getResponse.content().toString(CharsetUtil.UTF_8));
    }

    @Test
    public void shouldGetAndLock() throws Exception {
        String key = "get-and-lock";

        UpsertRequest request = new UpsertRequest(key, Unpooled.copiedBuffer("content", CharsetUtil.UTF_8), bucket());
        UpsertResponse response = cluster().<UpsertResponse>send(request).toBlocking().single();
        assertEquals(ResponseStatus.SUCCESS, response.status());

        GetResponse getResponse = cluster().<GetResponse>send(new GetRequest(key, bucket(), true, false, 2)).toBlocking().single();
        assertEquals(ResponseStatus.SUCCESS, response.status());
        assertEquals("content", getResponse.content().toString(CharsetUtil.UTF_8));

        request = new UpsertRequest(key, Unpooled.copiedBuffer("content", CharsetUtil.UTF_8), bucket());
        response = cluster().<UpsertResponse>send(request).toBlocking().single();
        assertEquals(ResponseStatus.EXISTS, response.status());

        Thread.sleep(3000);

        request = new UpsertRequest(key, Unpooled.copiedBuffer("content", CharsetUtil.UTF_8), bucket());
        response = cluster().<UpsertResponse>send(request).toBlocking().single();
        assertEquals(ResponseStatus.SUCCESS, response.status());
    }

    @Test
    public void shouldTouch() throws Exception {
        String key = "touch";

        UpsertRequest request = new UpsertRequest(key, Unpooled.copiedBuffer("content", CharsetUtil.UTF_8), 3, 0, bucket());
        UpsertResponse response = cluster().<UpsertResponse>send(request).toBlocking().single();
        assertEquals(ResponseStatus.SUCCESS, response.status());

        Thread.sleep(2000);

        TouchResponse touchResponse = cluster().<TouchResponse>send(new TouchRequest(key, 3, bucket())).toBlocking().single();
        assertEquals(ResponseStatus.SUCCESS, touchResponse.status());

        Thread.sleep(2000);

        GetResponse getResponse = cluster().<GetResponse>send(new GetRequest(key, bucket())).toBlocking().single();
        assertEquals(ResponseStatus.SUCCESS, response.status());
        assertEquals("content", getResponse.content().toString(CharsetUtil.UTF_8));
    }

    @Test
    public void shouldUnlock() throws Exception {
        String key = "unlock";

        UpsertRequest request = new UpsertRequest(key, Unpooled.copiedBuffer("content", CharsetUtil.UTF_8), bucket());
        UpsertResponse response = cluster().<UpsertResponse>send(request).toBlocking().single();
        assertEquals(ResponseStatus.SUCCESS, response.status());

        GetResponse getResponse = cluster().<GetResponse>send(new GetRequest(key, bucket(), true, false, 15)).toBlocking().single();
        assertEquals(ResponseStatus.SUCCESS, response.status());
        assertEquals("content", getResponse.content().toString(CharsetUtil.UTF_8));

        request = new UpsertRequest(key, Unpooled.copiedBuffer("content", CharsetUtil.UTF_8), bucket());
        response = cluster().<UpsertResponse>send(request).toBlocking().single();
        assertEquals(ResponseStatus.EXISTS, response.status());

        UnlockRequest unlockRequest = new UnlockRequest(key, getResponse.cas(), bucket());
        UnlockResponse unlockResponse = cluster().<UnlockResponse>send(unlockRequest).toBlocking().single();
        assertEquals(ResponseStatus.SUCCESS, unlockResponse.status());

        request = new UpsertRequest(key, Unpooled.copiedBuffer("content", CharsetUtil.UTF_8), bucket());
        response = cluster().<UpsertResponse>send(request).toBlocking().single();
        assertEquals(ResponseStatus.SUCCESS, response.status());
    }

}
