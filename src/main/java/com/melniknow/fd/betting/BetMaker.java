package com.melniknow.fd.betting;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.ShoulderInfo;
import com.melniknow.fd.betting.bookmakers._188bet.BetsSupport;
import com.melniknow.fd.core.Logger;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Currency;
import com.melniknow.fd.utils.BetUtils;
import com.melniknow.fd.utils.MathUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BetMaker {
    private static final Integer kSecondsForPlaceBet = 60;
    private static final Object OBJ = new Object();
    public static BetUtils.CompleteBetsFork make(MathUtils.CalculatedFork calculated) throws InterruptedException {
        var executor = Executors.newFixedThreadPool(2);

        try {

            Context.log.info("Ставки перед началом make" + "\n" +
                                calculated.fork().betInfo1().BK_name() + ": " + calculated.fork().betInfo1().BK_bet() + " \n" +
                                "Cf = " + calculated.fork().betInfo1().BK_cf() + " \n" +
                                calculated.fork().betInfo2().BK_name() + ": " + calculated.fork().betInfo2().BK_bet() + " \n" +
                                "Cf = " + calculated.fork().betInfo2().BK_cf() + " \n");

            // Берём двух букмекеров в вилке
            var bookmaker1 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo1().BK_name());
            var bookmaker2 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo2().BK_name());

            // Получаем настройку связки для этой пары бк (или null если нет такой)
            var bundle = Context.bundleStorage.get(bookmaker1, bookmaker2);

            // Проверяем перевёрнут ли порядок в связке
            var isReversed = bundle != null && !bundle.bk1().equals(bookmaker1);

            var isValue = bundle != null && bundle.isValue();
            var isVerifiedValue = bundle != null && bundle.isVerifiedValue();
            var isValueWithOneDollar = bundle != null && bundle.isValueWithOneDollar();

            // Если порядок перевёрнут, то мы должны поменять порядок очерёдности букмекеров
            if (isReversed) {
                calculated = reverseCalculated(calculated);
                bookmaker1 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo1().BK_name());
                bookmaker2 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo2().BK_name());
            }

            // Idea немного ебанулась и хочет здесь final
            final var bookmaker1Final = bookmaker1;
            final var bookmaker2Final = bookmaker2;
            final var calculatedFinal = calculated;

            // Получаем параметры от наших бк
            var bkParams1 = Context.betsParams.get(bookmaker1Final);
            var bkParams2 = Context.betsParams.get(bookmaker2Final);

            // Берём реализацию для наших бк
            var realization1 = bookmaker1Final.realization;
            var realization2 = bookmaker2Final.realization;

            var openLink1 = executor.submit(() -> realization1.openLink(bookmaker1Final, calculatedFinal.fork().betInfo1()));

            if (!isValue) { // Если это не валуй по заходим
                var openLink2 = executor.submit(() -> realization2.openLink(bookmaker2Final, calculatedFinal.fork().betInfo2()));
                openLink2.get(60, TimeUnit.SECONDS);
            }

            openLink1.get(60, TimeUnit.SECONDS);

            var futureBalance1 = executor.submit(() ->
                realization1.clickOnBetTypeAndReturnBalanceAsRub(bookmaker1Final, calculatedFinal.fork().betInfo1(), calculatedFinal.fork().sport(), true));

            var balance2Rub = new BigDecimal("1000000000");

            if (!isValue) {
                var futureBalance2 = executor.submit(() ->
                    realization2.clickOnBetTypeAndReturnBalanceAsRub(bookmaker2Final, calculatedFinal.fork().betInfo2(), calculatedFinal.fork().sport(), !isVerifiedValue));
                balance2Rub = futureBalance2.get(30, TimeUnit.SECONDS);
                if (isVerifiedValue) { // Если это проверяемый валуй, то мы проверив баланс, перезаписываем его на большое число
                    balance2Rub = new BigDecimal("1000000000");
                }
            }

            var balance1Rub = futureBalance1.get(30, TimeUnit.SECONDS);

            var bets = calculateBetsSize(
                bkParams1.currency(),
                bkParams2.currency(),
                isValueWithOneDollar ? new BigDecimal("1000000000") : balance1Rub,
                balance2Rub,
                Context.currencyToRubCourse.get(bkParams1.currency()).multiply(bkParams1.minBetSum()),
                Context.currencyToRubCourse.get(bkParams2.currency()).multiply(bkParams2.minBetSum()),
                Context.currencyToRubCourse.get(bkParams1.currency()).multiply(bkParams1.maxBetSum()),
                Context.currencyToRubCourse.get(bkParams2.currency()).multiply(bkParams2.maxBetSum()),
                calculatedFinal.betCoef1(),
                calculatedFinal.betCoef2(),
                bkParams1.accuracy().intValue(),
                bkParams2.accuracy().intValue()
            );

            // Получаем размеры ставок с учётом округления и валют на бк
            var bet1 = bets.get(0);
            var bet2 = bets.get(1);

            BigDecimal finalBet = bet1;
            var enterSumAndCHeckCfFuture1 = executor.submit(() -> realization1.enterSumAndCheckCf(bookmaker1Final, calculatedFinal.fork().betInfo1(), isValueWithOneDollar ? bkParams1.currency().minValue : finalBet));

            if (!isValue && !isVerifiedValue) {
                var enterSumAndCHeckCfFuture2 = executor.submit(() -> realization2.enterSumAndCheckCf(bookmaker2Final, calculatedFinal.fork().betInfo2(), bet2));
                enterSumAndCHeckCfFuture2.get(30, TimeUnit.SECONDS);
            }

            enterSumAndCHeckCfFuture1.get(30, TimeUnit.SECONDS);

            var realCf1 = BigDecimal.ZERO;
            var realCf2 = BigDecimal.ZERO;

            try {
                var betFuture1 = executor.submit(() -> realization1.placeBetAndGetRealCf(bookmaker1Final, calculatedFinal.fork().betInfo1(), new ShoulderInfo(true, null, null, null)));
                realCf1 = betFuture1.get(kSecondsForPlaceBet, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                var errorMessage = "Не удалось поставить вилку" + e.getCause().getLocalizedMessage();
                Context.log.info(errorMessage);
                throw new RuntimeException(errorMessage);
            } catch (TimeoutException e) {
                var errorMessage = "Не удалось поставить вилку" + e.getLocalizedMessage();
                Context.log.info(errorMessage);
                throw new RuntimeException(errorMessage);
            }

            var isClosed = false;
            String causeOfFail = "Ok";
            if (!isValue && !isVerifiedValue) {
                try {
                    BigDecimal finalRealCf = realCf1;
                    String bk1name = calculated.fork().betInfo1().BK_name();
                    var betFuture2 = executor.submit(() -> realization2.placeBetAndGetRealCf(bookmaker2Final, calculatedFinal.fork().betInfo2(), new ShoulderInfo(false, finalRealCf, finalBet, bk1name)));
                    realCf2 = betFuture2.get(kSecondsForPlaceBet, TimeUnit.SECONDS);
                } catch (ExecutionException | TimeoutException e) {
                    if (bookmaker1Final.equals(Bookmaker._188BET)) { // Пытаемся сделать кешаут из 188bet
                        isClosed = BetsSupport.cashOut(Context.screenManager.getScreenForBookmaker(bookmaker1Final));
                    }

                    causeOfFail = e.getCause().getLocalizedMessage();

                    var errorMessage = "Не удалось поставить плечо(%s) - %s \nПричина: %s".formatted(
                        isClosed ? "Сделали CashOut" : "Не сделали CashOut",
                        calculatedFinal.fork().betInfo2().BK_name(), causeOfFail);
                    Context.log.info(errorMessage);
                    Logger.writeToLogSession(errorMessage);
                }
            }

            executor.shutdownNow(); // Убиваем потоки

            // Сохраняем вилку в кеш
            var fork = calculatedFinal.fork();
            Context.forksCache.put(new MathUtils.ForkKey(fork.betInfo1().BK_name().trim(), fork.betInfo1().BK_event_id().trim(), fork.betInfo1().BK_bet().trim()), OBJ);
            Context.forksCache.put(new MathUtils.ForkKey(fork.betInfo2().BK_name().trim(), fork.betInfo2().BK_event_id().trim(), fork.betInfo2().BK_bet().trim()), OBJ);

            if (isValueWithOneDollar) {
                bet1 = bkParams1.currency().minValue;
            }

            var bet1Rub = bet1.multiply(Context.currencyToRubCourse.get(bkParams1.currency())).setScale(2, RoundingMode.DOWN);
            var bet2Rub = bet2.multiply(Context.currencyToRubCourse.get(bkParams2.currency())).setScale(2, RoundingMode.DOWN);

            return buildCompleteBetsFork(calculatedFinal, realCf1, realCf2, balance1Rub,
                balance2Rub, bet1Rub, bet2Rub, isValue || isVerifiedValue, isValueWithOneDollar, isVerifiedValue, isClosed, causeOfFail);
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } catch (ExecutionException e) {
            Context.log.info("Ошибка в постановке ставки - " + e.getCause().getLocalizedMessage());
            throw new RuntimeException("Ошибка в постановке ставки - " + e.getCause().getLocalizedMessage());
        } catch (TimeoutException e) {
            Context.log.info("Ошибка в постановке ставки - слишком медленное соединение с сетью");
            throw new RuntimeException("Ошибка в постановке ставки - слишком медленное соединение с сетью");
        } catch (Exception e) {
            Context.log.info("Ошибка в постановке ставки - " + e.getLocalizedMessage());
            throw new RuntimeException("Ошибка в постановке ставки - " + e.getLocalizedMessage());
        } finally {
            executor.shutdownNow();
        }
    }

    private static MathUtils.CalculatedFork reverseCalculated(MathUtils.CalculatedFork calculated) {
        // Переворачиваем betCoef-ы
        var fork = reverseFork(calculated.fork());
        return new MathUtils.CalculatedFork(fork, calculated.betCoef2(), calculated.betCoef1());
    }
    private static Parser.Fork reverseFork(Parser.Fork fork) {
        // Переворачиваем betInfo
        return new Parser.Fork(fork.forkId(), fork.income(), fork.eventId(), fork.sport(), fork.isMiddles(), fork.betType(),
            fork.betInfo2(), fork.betInfo1());
    }

    private static List<BigDecimal> calculateBetsSize(Currency currency1, Currency currency2, BigDecimal balanceRub1,
                                                      BigDecimal balanceRub2, BigDecimal minSt1Rub, BigDecimal minSt2Rub,
                                                      BigDecimal maxSt1Rub, BigDecimal maxSt2Rub, BigDecimal calcCf1,
                                                      BigDecimal calcCf2, int scale1, int scale2) {
        if (minSt1Rub.compareTo(balanceRub1) > 0 || minSt2Rub.compareTo(balanceRub2) > 0)
            throw new RuntimeException("Невозможно поставить ставку при текущих настройках");

        var data = new ArrayList<BigDecimal>(2);

        Integer rubValue1 = null;
        Integer rubValue2 = null;

        if (calcCf1.compareTo(calcCf2) > 0) { // Идём от максимально возможной ставки на плече с минимальным коэффициентом
            for (int s1 = maxSt1Rub.intValue(); s1 >= minSt1Rub.intValue(); s1--) {
                var tempRubValue2 = calcCf2.multiply(BigDecimal.valueOf(s1)).divide(calcCf1, 0, RoundingMode.DOWN).intValue();
                if (s1 <= balanceRub1.intValue() && tempRubValue2 <= balanceRub2.intValue()
                    && tempRubValue2 >= minSt2Rub.intValue() && tempRubValue2 <= maxSt2Rub.intValue()) {
                    rubValue1 = s1; // Берём первое значение, которое подходит под все ограничения
                    rubValue2 = tempRubValue2;
                    break;
                }
            }
        } else {
            for (int s2 = maxSt2Rub.intValue(); s2 >= minSt2Rub.intValue(); s2--) {
                var tempRubValue1 = calcCf1.multiply(BigDecimal.valueOf(s2)).divide(calcCf2, 0, RoundingMode.DOWN).intValue();
                if (s2 <= balanceRub2.intValue() && tempRubValue1 <= balanceRub1.intValue()
                    && tempRubValue1 >= minSt1Rub.intValue() && tempRubValue1 <= maxSt1Rub.intValue()) {
                    rubValue1 = tempRubValue1;
                    rubValue2 = s2;
                    break;
                }
            }
        }

        if (rubValue1 == null)
            throw new RuntimeException("Невозможно поставить ставку при текущих настройках");

        var value1 = BigDecimal.valueOf(rubValue1).divide(Context.currencyToRubCourse.get(currency1), 3, RoundingMode.DOWN).setScale(scale1, RoundingMode.DOWN);
        var value2 = BigDecimal.valueOf(rubValue2).divide(Context.currencyToRubCourse.get(currency2), 3, RoundingMode.DOWN).setScale(scale2, RoundingMode.DOWN);

        if (value1.compareTo(BigDecimal.ONE) < 0 || value2.compareTo(BigDecimal.ONE) < 0)
            throw new RuntimeException("Невозможно поставить ставку при текущих настройках");

        data.add(value1);
        data.add(value2);

        return data;
    }

    private static BetUtils.CompleteBetsFork buildCompleteBetsFork(MathUtils.CalculatedFork calculated,
                                                                   BigDecimal realCf1, BigDecimal realCf2,
                                                                   BigDecimal balance1Rub, BigDecimal balance2Rub,
                                                                   BigDecimal bet1Rub, BigDecimal bet2Rub, boolean isValue,
                                                                   boolean isValueWithOneDollar, boolean isVerifiedValue, boolean isClosed,
                                                                   String causeOfFail) {
        String income;
        BigDecimal realRubBalance1;
        BigDecimal realRubBalance2;

        if (isSuccessFork(realCf1, realCf2)) {
            realRubBalance1 = balance1Rub.subtract(bet1Rub);
            realRubBalance2 = balance2Rub.subtract(bet2Rub);

            if (isValueWithOneDollar) {
                income = "Валуй с $1 на первом плече";
            } else {
                income = "1) %s₽. 2) %s₽".formatted(
                    bet1Rub.multiply(realCf1).setScale(2, RoundingMode.DOWN),
                    bet2Rub.multiply(realCf2).setScale(2, RoundingMode.DOWN)
                );
            }

        } else if (isValue) {
            income = "Был поставлен валуй";
            realRubBalance1 = balance1Rub.subtract(bet1Rub);
            realRubBalance2 = balance2Rub;
            bet2Rub = BigDecimal.ZERO;
        } else if (isFirstForkSuccessAndSecondFail(realCf1, realCf2)) {
            var desc = isClosed ? "Сделан CashOut" : "Не сделан CashOut";
            income = "Одно из плечей не было поставлено(%s)\nПричина: %s".formatted(desc, causeOfFail);
            realRubBalance1 = balance1Rub.subtract(bet1Rub);
            realRubBalance2 = balance2Rub;
            bet2Rub = BigDecimal.ZERO;
        } else {
            throw new RuntimeException("Вилка не была поставлена");
        }

        if (isVerifiedValue) {
            income = "Был поставлен валуй с проверкой";
        }

        return new BetUtils.CompleteBetsFork(calculated, income, realRubBalance1, realRubBalance2, bet1Rub, bet2Rub, realCf1, realCf2);
    }

    private static boolean isSuccessFork(BigDecimal realCf1, BigDecimal realCf2) {
        return !realCf1.equals(BigDecimal.ZERO) && !realCf2.equals(BigDecimal.ZERO);
    }

    private static boolean isFirstForkSuccessAndSecondFail(BigDecimal realCf1, BigDecimal realCf2) {
        return !realCf1.equals(BigDecimal.ZERO) && realCf2.equals(BigDecimal.ZERO);
    }
}
