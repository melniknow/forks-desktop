package com.melniknow.fd.oddscorp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.melniknow.fd.context.Context;
import io.mikael.urlbuilder.UrlBuilder;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {
    public record ParserParams(BigDecimal minFi, BigDecimal maxFi, BigDecimal minCf,
                               BigDecimal maxCf, int middles, List<Bookmakers> bookmakers,
                               List<BetType> types, BigDecimal forkLive) { }

    public record Fork(BigDecimal income, String sport, int isMiddles, BetType betType,
                       String bkName1, String event1, BetType type1, String link1,
                       BigDecimal ratio1, String bet1,
                       String bkName2, String event2, BetType type2, String link2,
                       BigDecimal ratio2, String bet2) { }

    public static List<Fork> getForks(ParserParams params) {
        if (params == null) return null;

        var uri = UrlBuilder.fromString("http://194.67.68.124/forks")
            .addParameter("bk2_name", buildArrayParams(params.bookmakers.stream().map(Enum::name)))
            .addParameter("is_middles", Integer.toString(params.middles))
            .addParameter("bet_types", buildArrayParamsWithUpperCase(params.types.stream().map(Enum::toString)))
            .addParameter("min_cf", params.minCf.toPlainString())
            .addParameter("max_cf", params.maxCf.toPlainString())
            .addParameter("min_fi", params.minFi.toPlainString())
            .addParameter("max_fi", params.maxFi.toPlainString())
            .addParameter("alive_sec", params.forkLive.toPlainString())
            .addParameter("token", Context.URITokenAuth)
            .toUri();

        var stringForks = "";

        var timeout = 2;

        var config = RequestConfig.custom()
            .setConnectTimeout(timeout * 1000)
            .setConnectionRequestTimeout(timeout * 1000)
            .setSocketTimeout(timeout * 1000).build();

        try (var httpClient = HttpClientBuilder.create()
            .setDefaultRequestConfig(config)
            .build()) {
            var request = new HttpGet(uri);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    return null;
                }
                var entity = response.getEntity();
                if (entity != null) {
                    stringForks = EntityUtils.toString(entity);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        var jsonParser = JsonParser.parseString(stringForks);

        var forks = new ArrayList<Fork>();
        if (!jsonParser.isJsonArray()) return null;

        for (var fork : jsonParser.getAsJsonArray()) {
            if (!fork.isJsonObject()) return null;
            var f = buildForkByJson(fork.getAsJsonObject());
            forks.add(f);
        }

        return forks;
    }

    private static String buildArrayParams(Stream<String> strings) {
        var result = strings.map(String::toLowerCase).collect(Collectors.toList());
        return String.join(",", result);
    }

    private static String buildArrayParamsWithUpperCase(Stream<String> strings) {
        var result = strings.map(String::toUpperCase).collect(Collectors.toList());
        return String.join(",", result);
    }

    private static Fork buildForkByJson(JsonObject forkObject) {
        return new Fork(
            forkObject.get("income").getAsBigDecimal(),
            forkObject.get("sport").getAsString(),
            Integer.parseInt(forkObject.get("is_middles").getAsString()),
            BetType.valueOf(forkObject.get("bet_type").getAsString()),
            forkObject.get("BK1_name").getAsString(),
            forkObject.get("BK1_event_id").getAsString(),
            BetType.valueOf(forkObject.get("BK1_bet_type").getAsString()),
            forkObject.get("BK1_href").getAsString(),
            forkObject.get("BK1_cf").getAsBigDecimal(),
            forkObject.get("BK1_bet").getAsString(),
            forkObject.get("BK2_name").getAsString(),
            forkObject.get("BK2_event_id").getAsString(),
            BetType.valueOf(forkObject.get("BK2_bet_type").getAsString()),
            forkObject.get("BK2_href").getAsString(),
            forkObject.get("BK2_cf").getAsBigDecimal(),
            forkObject.get("BK2_bet").getAsString()
        );
    }
}