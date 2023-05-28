package com.melniknow.fd;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.melniknow.fd.advanced.BundleStorage;
import com.melniknow.fd.advanced.Exception;
import com.melniknow.fd.betting.ScreenManager;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.BetType;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Currency;
import com.melniknow.fd.domain.Sport;
import com.melniknow.fd.profile.Profile;
import com.melniknow.fd.utils.BetUtils;
import com.melniknow.fd.utils.MathUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class Context {
    public static volatile Parser.ParserParams parserParams;
    public static final BundleStorage bundleStorage = new BundleStorage();
    public static volatile ConcurrentMap<Long, Long> eventIdToCountSuccessForks = new ConcurrentHashMap<>();
    public static volatile ConcurrentMap<Sport, ArrayList<BetType>> sportToBetTypes = new ConcurrentHashMap<>();
    public static volatile ConcurrentMap<Currency, BigDecimal> currencyToRubCourse = new ConcurrentHashMap<>();
    public static volatile ConcurrentMap<Bookmaker, BetUtils.BetsParams> betsParams = new ConcurrentHashMap<>();
    public static volatile ConcurrentMap<Bookmaker, ArrayList<Exception>> exceptionForBookmaker = new ConcurrentHashMap<>();
    public static volatile ExecutorService botPool = Executors.newSingleThreadExecutor();
    public static volatile ExecutorService parsingPool = Executors.newCachedThreadPool();
    public static final ScreenManager screenManager = new ScreenManager();
    public static volatile Profile profile;

    public static final Logger log = Logger.getLogger("mainLogger");

    public static final LoadingCache<MathUtils.ForkKey, Parser.Fork> forksCache = CacheBuilder.newBuilder()
        .expireAfterAccess(120, TimeUnit.MINUTES)
        .build(
            new CacheLoader<>() {
                @Override
                public Parser.Fork load(MathUtils.ForkKey s) {
                    return null;
                }
            });
}
