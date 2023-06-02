package com.melniknow.fd.domain;

import java.math.BigDecimal;

public enum Currency {
    RUB(new BigDecimal("100")), USD(new BigDecimal("2")), EUR(new BigDecimal("2")), THB(new BigDecimal("50"));

    public final BigDecimal minValue;
    Currency(BigDecimal minValue) { this.minValue = minValue; }
}
