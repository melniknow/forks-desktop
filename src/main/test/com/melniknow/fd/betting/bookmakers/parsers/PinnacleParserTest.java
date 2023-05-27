package com.melniknow.fd.betting.bookmakers.parsers;

import com.google.gson.JsonObject;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.BetType;
import com.melniknow.fd.domain.Sport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PinnacleParserTest {
    @Test
    void simple() {
        Parser.BetInfo info = new Parser.BetInfo("", "", BetType.TOTALS, "TOTALS__UNDER(2.25)",
            "", BigDecimal.valueOf(1), "", "", new JsonObject(), new JsonObject(), "");
        Sport sport = Sport.SOCCER;

        var parser = new PinnacleParser(info, sport);

        var res = parser.parse();

        assertEquals("Total – Match", res.marketName());
        assertEquals("Under 2.25", res.selectionName());
    }

    @Test
    void games() {
        Parser.BetInfo info = new Parser.BetInfo("", "", BetType.TOTALS, "GAME__02_03__P1",
            "", BigDecimal.valueOf(1), "Pidor1 vs Pidor2", "", new JsonObject(), new JsonObject(), "");
        Sport sport = Sport.TENNIS;

        var parser = new PinnacleParser(info, sport);

        var res = parser.parse();

        assertEquals("Money Line (Games) – Set 2 Game 3", res.marketName());
        assertEquals("Pidor1", res.selectionName());
    }
}