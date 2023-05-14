package com.melniknow.fd;

import com.melniknow.fd.betting.ScreenManager;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Currency;
import com.melniknow.fd.profile.Profile;
import com.melniknow.fd.utils.BetUtils;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Context {
    public static volatile Parser.ParserParams parserParams;
    public static volatile ConcurrentMap<Currency, BigDecimal> currencyToRubCourse = new ConcurrentHashMap<>();
    public static volatile ConcurrentMap<Bookmaker, BetUtils.BetsParams> betsParams = new ConcurrentHashMap<>();
    public static volatile ExecutorService botPool = Executors.newSingleThreadExecutor();
    public static volatile ExecutorService parsingPool = Executors.newCachedThreadPool();
    public static final ScreenManager screenManager = new ScreenManager();
    public static volatile Profile profile;
}

