package com.melniknow.fd.betting.bookmakers.parsers;

import com.google.gson.JsonObject;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.BetType;
import com.melniknow.fd.domain.Sport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PinnacleParserTest {

    /*
    examples: HANDICAP_OT__P1(4)
     */

    JsonObject getObjectIsSpecial(boolean isSpecial) {
        JsonObject meta = new JsonObject();
        meta.addProperty("is_special", isSpecial);
        return meta;
    }

    @Test
    void totalsOverUnder() {
        Parser.BetInfo info = new Parser.BetInfo("", "", BetType.TOTALS, "TOTALS__UNDER(2.25)",
            "", BigDecimal.valueOf(1), "", "", getObjectIsSpecial(false), new JsonObject(), "");
        Sport sport = Sport.SOCCER;

        var parser = new PinnacleParser(info, sport);

        var res = parser.parse();

        assertEquals("Total – Match", res.marketName());
        assertEquals("Under 2.25", res.selectionName());

        info = new Parser.BetInfo("", "", BetType.TOTALS, "TOTALS__OVER(2.25)",
            "", BigDecimal.valueOf(1), "", "", getObjectIsSpecial(false), new JsonObject(), "");

        parser = new PinnacleParser(info, sport);

        res = parser.parse();

        assertEquals("Total – Match", res.marketName());
        assertEquals("Over 2.25", res.selectionName());
    }

    @Test
    void games() {
        Parser.BetInfo info = new Parser.BetInfo("", "", BetType.TOTALS, "GAME__02_03__P1",
            "", BigDecimal.valueOf(1), "Pidor1 vs Pidor2", "", getObjectIsSpecial(false), new JsonObject(), "");
        Sport sport = Sport.TENNIS;

        var parser = new PinnacleParser(info, sport);

        var res = parser.parse();

        assertEquals("Money Line (Games) – Set 2 Game 3", res.marketName());
        assertEquals("Pidor1", res.selectionName());
        // ---- P2 -----
        info = new Parser.BetInfo("", "", BetType.TOTALS, "GAME__02_03__P2",
            "", BigDecimal.valueOf(1), "Pidor1 vs Pidor2", "", getObjectIsSpecial(false), new JsonObject(), "");

        parser = new PinnacleParser(info, sport);

        res = parser.parse();

        assertEquals("Money Line (Games) – Set 2 Game 3", res.marketName());
        assertEquals("Pidor2", res.selectionName());
    }

    @Test
    void half0102Totals() {
        Parser.BetInfo info = new Parser.BetInfo("", "", BetType.TOTALS, "HALF_01__TOTALS__OVER(1.75)",
            "", BigDecimal.valueOf(1), "Pidor1 vs Pidor2", "", getObjectIsSpecial(false), new JsonObject(), "");
        Sport sport = Sport.SOCCER;

        var parser = new PinnacleParser(info, sport);

        var res = parser.parse();

        assertEquals("Total – 1st Half", res.marketName());
        assertEquals("Over 1.75", res.selectionName());

        // ---- Half 2 -----

        info = new Parser.BetInfo("", "", BetType.TOTALS, "HALF_02__TOTALS__UNDER(1.75)",
            "", BigDecimal.valueOf(1), "Pidor1 vs Pidor2", "", getObjectIsSpecial(false), new JsonObject(), "");

        parser = new PinnacleParser(info, sport);

        res = parser.parse();

        assertEquals("Total – 2nd Half", res.marketName());
        assertEquals("Under 1.75", res.selectionName());
    }

    @Test
    void handicap() {
        JsonObject meta = new JsonObject();
        meta.addProperty("points", -0.75);
        meta.addProperty("is_special", false);
        Parser.BetInfo info = new Parser.BetInfo("", "", BetType.TOTALS, "HALF_01__HANDICAP__P2(-0.75)",
            "", BigDecimal.valueOf(1), "Pidor1 vs Pidor2", "", meta, new JsonObject(), "");
        Sport sport = Sport.SOCCER;

        var parser = new PinnacleParser(info, sport);

        var res = parser.parse();

        assertEquals("Handicap – 1st Half", res.marketName());
        assertEquals("-0.75", res.selectionName());
    }

    @Test
    void basketball() {
        JsonObject meta = new JsonObject();
        meta.addProperty("points", 4);
        meta.addProperty("is_special", false);
        Parser.BetInfo info = new Parser.BetInfo("", "", BetType.TOTALS, "HANDICAP_OT__P1(4)",
            "", BigDecimal.valueOf(1), "Pidor1 vs Pidor2", "", meta, new JsonObject(), "");
        Sport sport = Sport.BASKETBALL;

        var parser = new PinnacleParser(info, sport);

        var res = parser.parse();

        assertEquals("Handicap – Game", res.marketName());
        assertEquals("+4", res.selectionName());
    }

    @Test
    void draw() {
        JsonObject meta = new JsonObject();
        meta.addProperty("points", 4);
        meta.addProperty("is_special", false);
        Parser.BetInfo info = new Parser.BetInfo("", "", BetType.TOTALS, "WIN__PX",
            "", BigDecimal.valueOf(1), "Pidor1 vs Pidor2", "", meta, new JsonObject(), "");
        Sport sport = Sport.SOCCER;

        var parser = new PinnacleParser(info, sport);

        var res = parser.parse();

        assertEquals("Money Line – Match", res.marketName());
        assertEquals("Draw", res.selectionName());
    }

    @Test
    void winP1P2() {
        JsonObject meta = new JsonObject();
        meta.addProperty("points", 4);
        meta.addProperty("is_special", false);
        Parser.BetInfo info = new Parser.BetInfo("", "", BetType.TOTALS, "WIN__P1",
            "", BigDecimal.valueOf(1), "Pidor1 vs Pidor2", "", meta, new JsonObject(), "");
        Sport sport = Sport.SOCCER;

        var parser = new PinnacleParser(info, sport);

        var res = parser.parse();

        assertEquals("Money Line – Match", res.marketName());
        assertEquals("Pidor1", res.selectionName());

        info = new Parser.BetInfo("", "", BetType.TOTALS, "WIN__P2",
            "", BigDecimal.valueOf(1), "Pidor1 vs Pidor2", "", meta, new JsonObject(), "");

        parser = new PinnacleParser(info, sport);

        res = parser.parse();

        assertEquals("Money Line – Match", res.marketName());
        assertEquals("Pidor2", res.selectionName());
    }

    @Test
    void tennisSet() {
        JsonObject meta = new JsonObject();
        meta.addProperty("is_special", false);
        meta.addProperty("points", -3.5);
        Parser.BetInfo info = new Parser.BetInfo("", "", BetType.TOTALS, "SET_01__HANDICAP__P1(-3.5)",
            "(games)", BigDecimal.valueOf(1), "Pidor1 vs Pidor2", "", meta, new JsonObject(), "");
        Sport sport = Sport.TENNIS;

        var parser = new PinnacleParser(info, sport);

        var res = parser.parse();

        assertEquals("Handicap (Games) – 1st Set", res.marketName());
        assertEquals("-3.5", res.selectionName());
    }
}