package com.melniknow.fd.oddscorp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.melniknow.fd.context.Context;
import io.mikael.urlbuilder.UrlBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {
    public record ParserParams(BigDecimal minIncome, List<Bookmakers> bookmakers, int middles,
                               List<BetType> types, BigDecimal minCf, BigDecimal maxCf,
                               BigDecimal minFi, BigDecimal maxFi, BigDecimal aliveSec) { }

    public record Fork(BigDecimal income, String sport, int isMiddles, BetType betType,
                       String bkName1, String event1, BetType type1, String link1,
                       BigDecimal ratio1, String bet1,
                       String bkName2, String event2, BetType type2, String link2,
                       BigDecimal ratio2, String bet2) { }

    public static List<Fork> getForks(ParserParams params) {
        var uri = UrlBuilder.fromString("http://api.oddscp.com:8111/forks")
            .addParameter("bk2_name", buildArrayParams(params.bookmakers.stream().map(Enum::name)))
            .addParameter("is_middles", Integer.toString(params.middles))
            .addParameter("bet_types", buildArrayParams(params.types.stream().map(Enum::toString)))
            .addParameter("min_cf", params.minCf.toString())
            .addParameter("max_cf", params.maxCf.toString())
            .addParameter("min_fi", params.minFi.toString())
            .addParameter("max_fi", params.maxFi.toString())
            .addParameter("alive_sec", params.aliveSec.toString())
            .addParameter("token", Context.URITokenAuth)
            .toUri();

        System.out.println(buildArrayParams(params.bookmakers.stream().map(Enum::name)));
        System.out.println(uri);

        var stringForks = FakeServer.get(uri.getQuery());

        var jsonParser = JsonParser.parseString(stringForks);

        var forks = new ArrayList<Fork>();
        if (!jsonParser.isJsonArray()) {
            return forks;
        }

        for (var fork : jsonParser.getAsJsonArray()) {
            if (!fork.isJsonObject()) return forks; // Or null?
            forks.add(buildForkByJson(fork.getAsJsonObject()));
        }

        return forks;
    }

    private static String buildArrayParams(Stream<String> strings) {
        var result = strings.map(String::toLowerCase).collect(Collectors.toList());
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