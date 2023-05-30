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
    // Параметры парсера
    public static volatile Parser.ParserParams parserParams;
    // Хранилище связок
    public static final BundleStorage bundleStorage = new BundleStorage();
    // Подсчёт количество поставленных вилок на event
    public static volatile ConcurrentMap<Long, Long> eventIdToCountSuccessForks = new ConcurrentHashMap<>();
    // Список допустимых BetTypes для спорта
    public static volatile ConcurrentMap<Sport, ArrayList<BetType>> sportToBetTypes = new ConcurrentHashMap<>();
    // Курсы валют
    public static volatile ConcurrentMap<Currency, BigDecimal> currencyToRubCourse = new ConcurrentHashMap<>();
    // Параметры для каждого букмекера
    public static volatile ConcurrentMap<Bookmaker, BetUtils.BetsParams> betsParams = new ConcurrentHashMap<>();
    // Исключения для букмекера
    public static volatile ConcurrentMap<Bookmaker, ArrayList<Exception>> exceptionForBookmaker = new ConcurrentHashMap<>();
    // Многопоточная работа с основным жизненным циклом бота
    public static volatile ExecutorService botPool = Executors.newSingleThreadExecutor();
    // Многопоточная работа с сетью и экранами браузера
    public static volatile ExecutorService parsingPool = Executors.newCachedThreadPool();
    // Менеджер экранов
    public static final ScreenManager screenManager = new ScreenManager();
    // Профиль
    public static volatile Profile profile;

    // Логгер
    public static final Logger log = Logger.getLogger("mainLogger");

    // Кеш для повтора вилок
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
