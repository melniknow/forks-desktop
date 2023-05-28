package com.melniknow.fd.advanced;

import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.BetType;
import com.melniknow.fd.domain.Sport;

import java.math.BigDecimal;
import java.util.List;

public record Exception(String name, Sport sport, ExceptionType type) {
    public static boolean checkException(List<Exception> ex, Sport sport, Parser.BetInfo betInfo, boolean isFirst, boolean isMiddles) {
        try {
            if (ex == null) return true;

            var exForSport = ex.stream().filter(e -> e.sport().equals(sport)).toList();
            if (exForSport.isEmpty()) return true;

            for (Exception exception : exForSport) {
                switch (exception.type()) {
                    case ИСКЛ_ЕСЛИ_ТБ -> {
                        if ((betInfo.BK_bet_type().equals(BetType.TOTALS) || betInfo.BK_bet_type().equals(BetType.HALF_TOTALS)
                            || betInfo.BK_bet_type().equals(BetType.SET_TOTALS)) && betInfo.BK_bet().contains("OVER")) {
                            return false;
                        }
                    }
                    case ИСКЛ_ЕСЛИ_ТМ -> {
                        if ((betInfo.BK_bet_type().equals(BetType.TOTALS) || betInfo.BK_bet_type().equals(BetType.HALF_TOTALS)
                            || betInfo.BK_bet_type().equals(BetType.SET_TOTALS)) && betInfo.BK_bet().contains("UNDER")) {
                            return false;
                        }
                    }
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
}
