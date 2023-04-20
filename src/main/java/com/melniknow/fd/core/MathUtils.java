package com.melniknow.fd.core;

import com.melniknow.fd.oddscorp.Parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MathUtils {
    public record CalculatedFork(Parser.Fork fork, BigDecimal подумать_чё_надо) {}
    public static List<CalculatedFork> calculate(List<Parser.Fork> forks) {
        return new ArrayList<>();
    }
}
