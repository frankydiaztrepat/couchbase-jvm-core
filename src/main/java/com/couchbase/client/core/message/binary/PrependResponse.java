package com.couchbase.client.core.message.binary;

import com.couchbase.client.core.message.CouchbaseRequest;
import com.couchbase.client.core.message.ResponseStatus;
import io.netty.buffer.ByteBuf;

/**
 * .
 *
 * @author Michael Nitschinger
 */
public class PrependResponse extends AbstractBinaryResponse {

    private final long cas;

    public PrependResponse(ResponseStatus status, long cas, String bucket, ByteBuf content, CouchbaseRequest request) {
        super(status, bucket, content, request);
        this.cas = cas;
    }

    public long cas() {
        return cas;
    }
}
