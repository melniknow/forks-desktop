package com.melniknow.fd.context;

import com.melniknow.fd.core.BetsUtils;
import com.melniknow.fd.core.Currency;
import com.melniknow.fd.oddscorp.Parser;

import java.math.BigDecimal;
import java.util.Map;

public class Context {
    public static volatile Parser.ParserParams parserParams;
    public static volatile Map<Currency, BigDecimal> currencyToRubCourse;
    public static final String URITokenAuth = "c9af132a2632cb74c1f59d524dbbb5b2";

    // Для каждого букмекера отдельно?
    public static volatile BetsUtils.BetsParams betsParams = new BetsUtils.BetsParams(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
}

