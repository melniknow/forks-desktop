package com.melniknow.fd.core;

import java.math.BigDecimal;

public class BetsUtils {
    public record BetsParams(BigDecimal delayBetweenAttempts, BigDecimal waitSecondShoulder,
                             BigDecimal minBetSum, BigDecimal maxBetSum) { }
    public record CompleteBetsFork(MathUtils.CalculatedFork calculatedFork, String info) { }
}
