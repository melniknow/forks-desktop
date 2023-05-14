package com.melniknow.fd;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.melniknow.fd.betting.ScreenManager;
import com.melniknow.fd.utils.BetUtils;
import com.melniknow.fd.domain.Currency;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.core.Parser;

import java.math.BigDecimal;
import java.util.concurrent.*;

public class Context {
    public static volatile Parser.ParserParams parserParams;
    public static volatile ConcurrentMap<Currency, BigDecimal> currencyToRubCourse = new ConcurrentHashMap<>();
    public static volatile ConcurrentMap<Bookmaker, BetUtils.BetsParams> betsParams = new ConcurrentHashMap<>();
    public static volatile ExecutorService botPool = Executors.newSingleThreadExecutor();
    public static volatile ExecutorService parsingPool = Executors.newCachedThreadPool();
    public static final ScreenManager screenManager = new ScreenManager();

    public static volatile boolean isRepeatFork = false;

    public static final LoadingCache<String, Parser.Fork> forksCache = CacheBuilder.newBuilder()
        .expireAfterAccess(30, TimeUnit.MINUTES)
        .build(
            new CacheLoader<>() {
                @Override
                public Parser.Fork load(String key) throws NullPointerException {
                    return null;
                }
            });
}

