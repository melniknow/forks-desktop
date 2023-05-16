package com.melniknow.fd.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.melniknow.fd.domain.BetType;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {
    public static final String oddscorpToken = "c9af132a2632cb74c1f59d524dbbb5b2";
    public record ParserParams(BigDecimal minFi, BigDecimal maxFi, BigDecimal minCf,
                               BigDecimal maxCf, int middles, List<Bookmaker> bookmakers,
                               List<BetType> types, BigDecimal forkLive, List<Sport> sports,
                               BigDecimal pauseAfterSuccess, BigDecimal maxMinus,
                               BigDecimal countFork, boolean isRepeatFork) { }

    public record BetInfo(String BK_name, String BK_event_id, BetType BK_bet_type, String BK_bet,
                          String BK_href,
                          BigDecimal BK_cf, String BK_game, String BK_league,
                          JsonObject BK_market_meta, JsonObject BK_event_meta,
                          String BK_event_native_id) { }

    public record Fork(String forkId, BigDecimal income, BigDecimal eventId, Sport sport,
                       int isMiddles, BetType betType, BetInfo betInfo1, BetInfo betInfo2) { }

    public static List<Fork> getForks(ParserParams params) {
        if (params == null) return null;

//        var url = UrlBuilder.fromString("http://194.67.68.124:8080/forks")
//            .addParameter("bk2_name", buildArrayParamsWithLowerCase(params.bookmakers.stream().map(n -> n.nameInAPI)))
//            .addParameter("sport", buildArrayParamsWithLowerCase(params.sports.stream().map(Enum::name)))
//            .addParameter("is_middles", Integer.toString(params.middles))
//            .addParameter("bet_types", buildArrayParamsWithUpperCase(params.types.stream().map(Enum::name)))
//            .addParameter("min_cf", params.minCf.toPlainString())
//            .addParameter("max_cf", params.maxCf.toPlainString())
//            .addParameter("min_fi", params.minFi.toPlainString())
//            .addParameter("max_fi", params.maxFi.toPlainString())
//            .addParameter("alive_sec", params.forkLive.toPlainString())
//            .addParameter("token", oddscorpToken)
//            .toUri();
//
//        var stringForks = "";
//
//        var timeout = 2;
//
//        var config = RequestConfig.custom()
//            .setConnectTimeout(timeout * 1000)
//            .setConnectionRequestTimeout(timeout * 1000)
//            .setSocketTimeout(timeout * 1000).build();
//
//        try (var httpClient = HttpClientBuilder.create()
//            .setDefaultRequestConfig(config)
//            .build()) {
//            var request = new HttpGet(url);
//            try (CloseableHttpResponse response = httpClient.execute(request)) {
//                if (response.getStatusLine().getStatusCode() != 200) {
//                    return null;
//                }
//                var entity = response.getEntity();
//                if (entity != null) {
//                    stringForks = EntityUtils.toString(entity);
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        var jsonParser = JsonParser.parseString(stringForks);
        var jsonParser = JsonParser.parseString(FakeServer.get());

        var forks = new ArrayList<Fork>();
        if (!jsonParser.isJsonArray()) return null;

        for (var fork : jsonParser.getAsJsonArray()) {
            if (!fork.isJsonObject()) return null;
            forks.add(buildForkByJson(fork.getAsJsonObject()));
        }

        return forks;
    }

    private static String buildArrayParamsWithLowerCase(Stream<String> strings) {
        return strings.map(String::toLowerCase).collect(Collectors.joining(","));
    }

    private static String buildArrayParamsWithUpperCase(Stream<String> strings) {
        return strings.map(String::toUpperCase).collect(Collectors.joining(","));
    }

    private static Fork buildForkByJson(JsonObject forkObject) {
        return new Fork(
            forkObject.get("fork_id").getAsString(),
            forkObject.get("income").getAsBigDecimal(),
            forkObject.get("event_id").getAsBigDecimal(),
            Sport.valueOf(forkObject.get("sport").getAsString().toUpperCase()),
            Integer.parseInt(forkObject.get("is_middles").getAsString()),
            BetType.valueOf(forkObject.get("bet_type").getAsString()),
            new BetInfo(forkObject.get("BK1_name").getAsString(), forkObject.get("BK1_event_id").getAsString(),
                BetType.valueOf(forkObject.get("BK1_bet_type").getAsString()), forkObject.get("BK1_bet").getAsString(), forkObject.get("BK1_href").getAsString(),
                forkObject.get("BK1_cf").getAsBigDecimal(), forkObject.get("BK1_game").getAsString(),
                forkObject.get("BK1_league").getAsString(), JsonParser.parseString(forkObject.get("BK1_market_meta").getAsString()).getAsJsonObject(),
                JsonParser.parseString(forkObject.get("BK1_event_meta").getAsString()).getAsJsonObject(),
                forkObject.get("BK1_event_native_id").getAsString()
            ),
            new BetInfo(forkObject.get("BK2_name").getAsString(), forkObject.get("BK2_event_id").getAsString(),
                BetType.valueOf(forkObject.get("BK2_bet_type").getAsString()), forkObject.get("BK2_bet").getAsString(), forkObject.get("BK2_href").getAsString(),
                forkObject.get("BK2_cf").getAsBigDecimal(), forkObject.get("BK2_game").getAsString(),
                forkObject.get("BK2_league").getAsString(), JsonParser.parseString(forkObject.get("BK2_market_meta").getAsString()).getAsJsonObject(),
                JsonParser.parseString(forkObject.get("BK2_event_meta").getAsString()).getAsJsonObject(),
                forkObject.get("BK2_event_native_id").getAsString()
            )
        );
    }
}