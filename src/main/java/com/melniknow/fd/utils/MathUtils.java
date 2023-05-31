package com.melniknow.fd.utils;

import com.melniknow.fd.core.Parser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

public class MathUtils {
    public record CalculatedFork(Parser.Fork fork, BigDecimal betCoef1, BigDecimal betCoef2) { }
    public record ForkKey(String bookmaker, BigDecimal eventId, String BK_bet) { }

    public static CalculatedFork calculate(List<Parser.Fork> forks_) {
        if (forks_ == null || forks_.isEmpty()) return null;

        var forks = FilterUtils.filter(forks_);
        forks.sort(Comparator.comparing(Parser.Fork::income).reversed());

        if (forks.isEmpty()) {
            return null;
        }

        Parser.Fork fork = forks.get(0);

        var mode = RoundingMode.DOWN;
        var scale = 8;

        var income1 = BigDecimal.ONE.divide(fork.betInfo1().BK_cf(), scale, mode);
        var income2 = BigDecimal.ONE.divide(fork.betInfo2().BK_cf(), scale, mode);

        var income = income1.add(income2);

        var c1 = BigDecimal.ONE.divide(fork.betInfo1().BK_cf(), scale, mode).divide(income, scale, mode);
        var c2 = BigDecimal.ONE.divide(fork.betInfo2().BK_cf(), scale, mode).divide(income, scale, mode);

        return new CalculatedFork(fork, c1, c2);
    }

    public static BigDecimal calculateIncome(BigDecimal cf1, BigDecimal cf2) {
        var mode = RoundingMode.DOWN;
        var scale = 10;

        var income1 = BigDecimal.ONE.divide(cf1, scale, mode);
        var income2 = BigDecimal.ONE.divide(cf2, scale, mode);

        var income = income1.add(income2);

        return BigDecimal.ONE.divide(income, scale, mode)
            .subtract(BigDecimal.ONE)
            .multiply(new BigDecimal("100"))
            .setScale(2, RoundingMode.DOWN);
    }
}
