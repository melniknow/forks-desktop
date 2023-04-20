package com.melniknow.fd.tg;

import com.melniknow.fd.core.MathUtils;
import io.mikael.urlbuilder.UrlBuilder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Sender {
    public static final HttpClient client = HttpClient.newHttpClient();

    public static void send(MathUtils.CalculatedFork fork) {
        var data = getDataMessage(fork);

        var uri = UrlBuilder.fromString("https://api.telegram.org/bot6061363285:AAGhtAmbN4A37_2IS7kx2zIvpZG8rRgcoGg/sendMessage?chat_id=-1001704593015")
            .addParameter("text", data).toUri();
        var request = HttpRequest.newBuilder(uri).build();

        client.sendAsync(request, (HttpResponse.BodyHandler<String>) responseInfo -> null);
    }

    private static String getDataMessage(MathUtils.CalculatedFork fork) {
        return fork.toString();
    }
}
