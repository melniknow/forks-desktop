package com.melniknow.fd.core;

import java.math.BigDecimal;

public class BetsUtils {
    public record BetsParams(Currency currency, BigDecimal minBetSum, BigDecimal maxBetSum,
                             String userAgent, String proxyIp, Integer proxyPort,
                             String proxyLogin, String proxyPassword) { }
    public record CompleteBetsFork(MathUtils.CalculatedFork calculatedFork, String info) { }
}
