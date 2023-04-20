package com.melniknow.fd.tg;

import com.melniknow.fd.core.MathUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Sender {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void send(MathUtils.CalculatedFork message) {
        var url = "https://api.telegram.org/bot6061363285:AAGhtAmbN4A37_2IS7kx2zIvpZG8rRgcoGg/sendMessage?chat_id=-1001704593015&text=" + message.fork().sport();
        var request = HttpRequest.newBuilder(URI.create(url)).build();

        client.sendAsync(request, (HttpResponse.BodyHandler<String>) responseInfo -> null);
    }
}
