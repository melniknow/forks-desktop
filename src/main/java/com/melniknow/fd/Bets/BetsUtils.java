package com.melniknow.fd.Bets;

import java.math.BigDecimal;

public class BetsUtils {
    public record BetsParams(BigDecimal delayBetweenAttempts, BigDecimal waitSecondShoulder,
                             BigDecimal minBetSum, BigDecimal maxBetSum) { }
}
