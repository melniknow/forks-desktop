package com.melniknow.fd.oddscorp;

import com.google.gson.JsonParser;
import com.melniknow.fd.context.Context;
import io.mikael.urlbuilder.UrlBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    public record ParserParams(BigDecimal minIncome, List<Bookmakers> bookmakers, int middles,
                               List<BetType> types) { }

    public record Fork(BigDecimal income, String sport, int isMiddles, BetType betType,
                       String bkName1, String event1, BetType type1, String link1,
                       BigDecimal ratio1, String bet1,
                       String bkName2, String event2, BetType type2, String link2,
                       BigDecimal ratio2, String bet2) { }

    public record Forks(ArrayList<Fork> forks) { }


    public static List<Fork> getForks(ParserParams params) {
        var forks = new ArrayList<Fork>();

        var uri = UrlBuilder.fromString("http://api.oddscp.com:8111/forks")
            .addParameter("bk2_name", buildBookmakers(params.bookmakers))
            .addParameter("is_middles", Integer.toString(params.middles))
            .addParameter("min_fi", params.minIncome.toPlainString())
            .addParameter("bet_types", buildBetTypes(params.types))
            .addParameter("token", Context.URITokenAuth)
            .toUri();

        System.out.println(uri.toString());

        var stringForks = FakeServer.get(uri.getQuery());

        var jsonParser = JsonParser.parseString(stringForks);

        if (!jsonParser.isJsonArray()) {
            return forks;
        }

        for (var fork : jsonParser.getAsJsonArray()) {
            if (!fork.isJsonObject()) return forks;

            var forkObject = fork.getAsJsonObject();

            forks.add(new Fork(
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
                forkObject.get("BK1_name").getAsString(),
                forkObject.get("BK2_event_id").getAsString(),
                BetType.valueOf(forkObject.get("BK2_bet_type").getAsString()),
                forkObject.get("BK2_href").getAsString(),
                forkObject.get("BK2_cf").getAsBigDecimal(),
                forkObject.get("BK2_bet").getAsString()
            ));
        }

        return forks;
    }

    private static String buildBookmakers(List<Bookmakers> bookmakers) {
        StringBuilder result = new StringBuilder();
        for (var bookmaker : bookmakers) {
            result.append(bookmaker.name().toLowerCase());
            result.append(",");
        }
        return result.deleteCharAt(result.length() - 1).toString();
    }

    private static String buildBetTypes(List<BetType> types) {
        StringBuilder result = new StringBuilder();
        for (var type : types) {
            result.append(type.toString().toLowerCase());
            result.append(",");
        }
        return result.deleteCharAt(result.length() - 1).toString();
    }
}