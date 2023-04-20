package com.melniknow.fd.core;

import com.melniknow.fd.oddscorp.Parser;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class MathUtils {
    public record BetsParams() { }
    public record CalculatedFork(Parser.Fork fork, BigDecimal подумать_чё_надо) { }

    public static List<CalculatedFork> calculate(BetsParams params, List<Parser.Fork> forks) {
        return forks.stream().map(n -> calculatedOne(params, n)).collect(Collectors.toList());
    }

    private static CalculatedFork calculatedOne(BetsParams params, Parser.Fork fork) {
        return new CalculatedFork(fork, BigDecimal.ONE);
    }
}
