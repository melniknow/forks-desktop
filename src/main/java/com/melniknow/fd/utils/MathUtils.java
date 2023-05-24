package com.melniknow.fd.utils;

import com.melniknow.fd.Context;
import com.melniknow.fd.advanced.Exception;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.BetType;
import com.melniknow.fd.domain.Sport;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MathUtils {
    public record CalculatedFork(Parser.Fork fork, BigDecimal betCoef1, BigDecimal betCoef2) { }

    public record ForkKey(String bookmaker, BigDecimal eventId, String BK_bet) { }

    public static CalculatedFork calculate(List<Parser.Fork> forks_) {
        if (forks_ == null || forks_.isEmpty()) return null;

        var forks = new ArrayList<>(
            forks_.stream()
                .filter(fork -> {
                    var count = Context.eventIdToCountSuccessForks.get(fork.eventId().longValue());
                    return count == null || count < Context.parserParams.countFork().longValue();
                })
                .filter(fork -> {
                    var betTypes = Context.sportToBetTypes.get(fork.sport());
                    if (betTypes == null) return false;

                    return betTypes.contains(fork.betInfo1().BK_bet_type()) && betTypes.contains(fork.betInfo2().BK_bet_type());
                })
                .filter(fork -> {
                    var ex1 = Context.exceptionForBookmaker.get(BetUtils.getBookmakerByNameInApi(fork.betInfo1().BK_name()));
                    var ex2 = Context.exceptionForBookmaker.get(BetUtils.getBookmakerByNameInApi(fork.betInfo2().BK_name()));

                    return checkException(ex1, fork.sport(), fork.betInfo1(), true, fork.isMiddles()) &&
                        checkException(ex2, fork.sport(), fork.betInfo2(), false, fork.isMiddles());
                })
                .filter(fork -> {
                    if (!Context.parserParams.isRepeatFork()) {
                        return !Context.forksCache.asMap().containsKey(new ForkKey(fork.betInfo1().BK_name(), fork.eventId(), fork.betInfo1().BK_bet()))
                            && !Context.forksCache.asMap().containsKey(new ForkKey(fork.betInfo2().BK_name(), fork.eventId(), fork.betInfo2().BK_bet()));
                    }
                    return true;
                })
                .toList()
        );

        forks.sort(Comparator.comparing(Parser.Fork::income).reversed());

        if (forks.isEmpty()) {
            return null;
        }

        Parser.Fork fork = forks.get(0);

        var mode = RoundingMode.DOWN;
        var scale = 8;

        var income1 = BigDecimal.ONE.divide(fork.betInfo1().BK_cf(), scale, mode);
        var income2 = BigDecimal.ONE.divide(fork.betInfo2().BK_cf(), scale, mode);

        var income = income1.add(income2);

        var c1 = BigDecimal.ONE.divide(fork.betInfo1().BK_cf(), scale, mode).divide(income, scale, mode);
        var c2 = BigDecimal.ONE.divide(fork.betInfo2().BK_cf(), scale, mode).divide(income, scale, mode);

        return new CalculatedFork(fork, c1, c2);
    }
    private static boolean checkException(List<Exception> ex, Sport sport, Parser.BetInfo betInfo, boolean isFirst, boolean isMiddles) {
        try {
            if (ex == null) return true;

            var exForSport = ex.stream().filter(e -> e.sport().equals(sport)).toList();
            if (exForSport.isEmpty()) return true;

            for (Exception exception : exForSport) {
                switch (exception.type()) {
                    case ИСКЛ_ЕСЛИ_ПЕРВАЯ_ТМ -> {
                        if (isFirst && (betInfo.BK_bet_type().equals(BetType.TOTALS) || betInfo.BK_bet_type().equals(BetType.HALF_TOTALS)
                            || betInfo.BK_bet_type().equals(BetType.SET_TOTALS)) && betInfo.BK_bet().contains("UNDER")) {
                            return false;
                        }
                    }
                    case ИСКЛ_ЕСЛИ_ПЕРВАЯ_ТБ -> {
                        if (isFirst && (betInfo.BK_bet_type().equals(BetType.TOTALS) || betInfo.BK_bet_type().equals(BetType.HALF_TOTALS)
                            || betInfo.BK_bet_type().equals(BetType.SET_TOTALS)) && betInfo.BK_bet().contains("OVER")) {
                            return false;
                        }
                    }
                    case ИСКЛ_ЕСЛИ_НЕ_КОР -> {
                        if (!isMiddles) return false;
                    }
                    case ИСКЛ_ЕСЛИ_КОР -> {
                        if (isMiddles) return false;
                    }
                    case ИСКЛ_ЕСЛИ_КРУГ_ТОТАЛ -> {
                        if ((betInfo.BK_bet_type().equals(BetType.TOTALS) || betInfo.BK_bet_type().equals(BetType.HALF_TOTALS)
                            || betInfo.BK_bet_type().equals(BetType.SET_TOTALS))) {
                            var num = new BigDecimal(betInfo.BK_bet().substring(betInfo.BK_bet().indexOf("(") + 1, betInfo.BK_bet().indexOf(")")));
                            if (num.stripTrailingZeros().scale() <= 0)
                                return false;
                        }
                    }
                    case ИСКЛ_ЕСЛИ_ДРОБНЫЙ_ТОТАЛ -> {
                        if ((betInfo.BK_bet_type().equals(BetType.TOTALS) || betInfo.BK_bet_type().equals(BetType.HALF_TOTALS)
                            || betInfo.BK_bet_type().equals(BetType.SET_TOTALS))) {
                            var num = new BigDecimal(betInfo.BK_bet().substring(betInfo.BK_bet().indexOf("(") + 1, betInfo.BK_bet().indexOf(")")));
                            if (num.remainder(BigDecimal.ONE).equals(new BigDecimal("0.5")) ||
                                num.remainder(BigDecimal.ONE).equals(new BigDecimal("-0.5")))
                                return false;
                        }
                    }
                    case ИСКЛ_ЕСЛИ_АЗИАТ_ТОТАЛ -> {
                        if ((betInfo.BK_bet_type().equals(BetType.TOTALS) || betInfo.BK_bet_type().equals(BetType.HALF_TOTALS)
                            || betInfo.BK_bet_type().equals(BetType.SET_TOTALS))) {
                            var num = new BigDecimal(betInfo.BK_bet().substring(betInfo.BK_bet().indexOf("(") + 1, betInfo.BK_bet().indexOf(")")))
                                .remainder(BigDecimal.ONE);
                            if (num.equals(new BigDecimal("0.25")) ||
                                num.equals(new BigDecimal("0.75")) ||
                                num.equals(new BigDecimal("-0.25")) ||
                                num.equals(new BigDecimal("-0.75")))
                                return false;
                        }
                    }
                    case ИСКЛ_ЕСЛИ_КРУГЛАЯ_ФОРА -> {
                        if ((betInfo.BK_bet_type().equals(BetType.HANDICAP) || betInfo.BK_bet_type().equals(BetType.HALF_HANDICAP)
                            || betInfo.BK_bet_type().equals(BetType.SET_HANDICAP))) {
                            var num = new BigDecimal(betInfo.BK_bet().substring(betInfo.BK_bet().indexOf("(") + 1, betInfo.BK_bet().indexOf(")")));
                            if (num.stripTrailingZeros().scale() <= 0)
                                return false;
                        } ;
                    }
                    case ИСКЛ_ЕСЛИ_ДРОБНАЯ_ФОРА -> {
                        if ((betInfo.BK_bet_type().equals(BetType.HANDICAP) || betInfo.BK_bet_type().equals(BetType.HALF_HANDICAP)
                            || betInfo.BK_bet_type().equals(BetType.SET_HANDICAP))) {
                            var num = new BigDecimal(betInfo.BK_bet().substring(betInfo.BK_bet().indexOf("(") + 1, betInfo.BK_bet().indexOf(")")));
                            if (num.remainder(BigDecimal.ONE).equals(new BigDecimal("0.5")) ||
                                num.remainder(BigDecimal.ONE).equals(new BigDecimal("-0.5")))
                                return false;
                        }
                    }
                    case ИСКЛ_ЕСЛИ_АЗИАТ_ФОРА -> {
                        if ((betInfo.BK_bet_type().equals(BetType.HANDICAP) || betInfo.BK_bet_type().equals(BetType.HALF_HANDICAP)
                            || betInfo.BK_bet_type().equals(BetType.SET_HANDICAP))) {
                            var num = new BigDecimal(betInfo.BK_bet().substring(betInfo.BK_bet().indexOf("(") + 1, betInfo.BK_bet().indexOf(")")))
                                .remainder(BigDecimal.ONE);
                            if (num.equals(new BigDecimal("0.25")) ||
                                num.equals(new BigDecimal("0.75")) ||
                                num.equals(new BigDecimal("-0.25")) ||
                                num.equals(new BigDecimal("-0.75")))
                                return false;
                        }
                    }
                }
            }

            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static BigDecimal calculateIncome(BigDecimal cf1, BigDecimal cf2) {
        var mode = RoundingMode.DOWN;
        var scale = 8;

        var income1 = BigDecimal.ONE.divide(cf1, scale, mode);
        var income2 = BigDecimal.ONE.divide(cf2, scale, mode);

        var income = income1.add(income2);

        var _100 = new BigDecimal("100");
        return _100.subtract(income.multiply(_100));
    }
}
