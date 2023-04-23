package com.melniknow.fd.context;

import com.melniknow.fd.utils.BetsUtils;
import com.melniknow.fd.domain.Currency;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.core.Parser;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Context {
    public static volatile Parser.ParserParams parserParams;
    public static volatile ConcurrentMap<Currency, BigDecimal> currencyToRubCourse = new ConcurrentHashMap<>();
    public static volatile ConcurrentMap<Bookmaker, BetsUtils.BetsParams> betsParams = new ConcurrentHashMap<>();
}

