package com.melniknow.fd.utils;

import com.melniknow.fd.domain.Currency;

import java.math.BigDecimal;

public class BetsUtils {
    public record BetsParams(String link, Currency currency, BigDecimal minBetSum,
                             BigDecimal maxBetSum,
                             String userAgent, String proxyIp, Integer proxyPort,
                             String proxyLogin, String proxyPassword) { }
    public record CompleteBetsFork(MathUtils.CalculatedFork calculatedFork, String info) { }
}
