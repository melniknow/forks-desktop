package com.melniknow.fd.oddscorp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    public record Params(BigDecimal minIncome, List<String> bookmakers, List<String> sports,
                         boolean middles, List<BetType> types) { }
    public record Fork(BigDecimal income, String sport, boolean isMiddles, BetType betType,
                       String bkName1, String event1, BetType type1, String link1,
                       BigDecimal ratio1, String bet1,
                       String bkName2, String event2, BetType type2, String link2,
                       BigDecimal ratio2, String bet2) { }

    public static List<Fork> getForks(Params params) {
        var result = new ArrayList<Fork>();
        var req = ""; // Сначала пишем запрос, стягиваем Forks и фильтруем по Params
        var serverData = FakeServer.get(req);

        return result;
    }
}
