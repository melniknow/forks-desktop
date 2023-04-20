package com.melniknow.fd.tg;

import io.netty.handler.codec.http.HttpHeaders;
import org.asynchttpclient.*;

public class Sender {
    private static final AsyncHttpClient client = Dsl.asyncHttpClient();

    public static void send(String message) {
        Request getRequest = Dsl.get(String.format(
            "https://api.telegram.org/bot6061363285:AAGhtAmbN4A37_2IS7kx2zIvpZG8rRgcoGg/sendMessage?chat_id=-1001704593015&text=%s",
            message)).build();

        client.executeRequest(getRequest, new AsyncHandler<Object>() {
            @Override
            public State onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
                return null;
            }
            @Override
            public State onHeadersReceived(HttpHeaders httpHeaders) throws Exception {
                return null;
            }
            @Override
            public State onBodyPartReceived(HttpResponseBodyPart httpResponseBodyPart) throws Exception {
                return null;
            }
            @Override
            public void onThrowable(Throwable throwable) {

            }
            @Override
            public Object onCompleted() throws Exception {
                return null;
            }
        });
    }
}
