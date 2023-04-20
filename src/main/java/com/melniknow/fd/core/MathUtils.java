package com.melniknow.fd.core;

import com.melniknow.fd.oddscorp.BetType;
import com.melniknow.fd.oddscorp.Parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MathUtils {
    public record CalculatedFork(Parser.Fork fork, BigDecimal подумать_чё_надо) { }
    public static List<CalculatedFork> calculate(List<Parser.Fork> forks) {
        return new ArrayList<>() {{
            add(new CalculatedFork(new Parser.Fork(BigDecimal.ONE, "sport", false, BetType.GAME_WIN, "bkNmae1", "event1", BetType.GAME_WIN, "link1", BigDecimal.ZERO, "bet1", "bkNmae1", "event1", BetType.GAME_WIN, "link1", BigDecimal.ZERO, "bet1"), BigDecimal.ONE));
        }};
    }
}
