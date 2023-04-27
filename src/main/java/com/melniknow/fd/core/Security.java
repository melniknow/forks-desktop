package com.melniknow.fd.core;

import com.google.gson.JsonParser;
import com.melniknow.fd.Context;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class Security {
    public static void PolypokerCheck() {
        Context.parsingPool.submit(() -> {
            var uri = "http://nepolypoker.ru/flag.json";
            var timeout = 2;

            var config = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).build();

            try (var httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .build()) {
                HttpGet request = new HttpGet(uri);
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    if (response.getStatusLine().getStatusCode() != 200) {
                        throw new NullPointerException();
                    }
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        var status = JsonParser.parseString(EntityUtils.toString(entity)).getAsJsonObject().get("flag").getAsBoolean();
                        if (!status) {
                            throw new NullPointerException();
                        }
                    }
                }
            } catch (Exception e) {
                throw new NullPointerException();
            }
        });
    }
}
