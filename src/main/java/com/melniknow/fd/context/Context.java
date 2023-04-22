package com.melniknow.fd.context;

import com.melniknow.fd.core.BetsUtils;
import com.melniknow.fd.core.Currency;
import com.melniknow.fd.oddscorp.Bookmakers;
import com.melniknow.fd.oddscorp.Parser;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Context {
    public static final String URITokenAuth = "c9af132a2632cb74c1f59d524dbbb5b2";
    public static volatile Parser.ParserParams parserParams;
    public static volatile Map<Currency, BigDecimal> currencyToRubCourse;
    public static volatile Map<Bookmakers, BetsUtils.BetsParams> betsParams = new HashMap<>();
}

