package com.melniknow.fd;

import com.melniknow.fd.utils.BetUtils;
import com.melniknow.fd.domain.Currency;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.core.Parser;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Context {
    public static volatile Parser.ParserParams parserParams;
    public static volatile ConcurrentMap<Currency, BigDecimal> currencyToRubCourse = new ConcurrentHashMap<>();
    public static volatile ConcurrentMap<Bookmaker, BetUtils.BetsParams> betsParams = new ConcurrentHashMap<>();
}

