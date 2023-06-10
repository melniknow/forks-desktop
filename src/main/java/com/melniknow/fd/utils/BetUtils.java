package com.melniknow.fd.utils;

import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Currency;

import java.math.BigDecimal;
import java.util.Arrays;

public class BetUtils {

    public record BetData(BigDecimal realCf, BigDecimal realSum) { }
    // Параметры для букмекера
    public record BetsParams(String link, Currency currency, BigDecimal minBetSum,
                             BigDecimal maxBetSum,
                             String userAgent, String proxyIp, Integer proxyPort,
                             String proxyLogin, String proxyPassword, String screenSize,
                             BigDecimal minCf,
                             BigDecimal maxCf, BigDecimal accuracy,
                             String login, String password, String dolphinAntiId) { }

    // Поставленная вилка
    public record CompleteBetsFork(MathUtils.CalculatedFork calculatedFork, String income,
                                   BigDecimal betRubBalance1,
                                   BigDecimal betRubBalance2,
                                   BigDecimal bet1Rub,
                                   BigDecimal bet2Rub,
                                   BigDecimal realCf1,
                                   BigDecimal realCf2,
                                   BigDecimal realIncome) { }

    public record Proxy(String ip, String port, String username, String password) { }

    public static Bookmaker getBookmakerByNameInApi(String nameInApi) {
        return Arrays.stream(Bookmaker.values()).
            filter(n -> n.nameInAPI.equals(nameInApi))
            .findAny()
            .orElse(null);
    }
}
