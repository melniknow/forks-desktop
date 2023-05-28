package com.melniknow.fd.betting;

import com.melniknow.fd.Context;
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
    public static BetUtils.CompleteBetsFork make(MathUtils.CalculatedFork calculated) throws InterruptedException {
        var executor = Executors.newFixedThreadPool(2);

        try {
            var bookmaker1 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo1().BK_name());
            var bookmaker2 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo2().BK_name());

            var bundle = Context.bundleStorage.get(bookmaker1, bookmaker2);

            var isReversed = bundle != null && !bundle.bk1().equals(bookmaker1);
            var isValue = bundle != null && bundle.isValue();
            var isVerifiedValue = bundle != null && bundle.isVerifiedValue();

            if (isReversed) {
                calculated = reverseCalculated(calculated);
                bookmaker1 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo1().BK_name());
                bookmaker2 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo2().BK_name());
            }

            final var bookmaker1Final = bookmaker1;
            final var bookmaker2Final = bookmaker2;
            final var calculatedFinal = calculated;

            var bkParams1 = Context.betsParams.get(bookmaker1Final);
            var bkParams2 = Context.betsParams.get(bookmaker2Final);

            var realization1 = bookmaker1Final.realization;
            var realization2 = bookmaker2Final.realization;

            var openLink1 = executor.submit(() -> realization1.openLink(bookmaker1Final, calculatedFinal.fork().betInfo1()));

            if (!isValue) {
                var openLink2 = executor.submit(() -> realization2.openLink(bookmaker2Final, calculatedFinal.fork().betInfo2()));
                openLink2.get(30, TimeUnit.SECONDS);
            }

            openLink1.get(30, TimeUnit.SECONDS);

            var futureBalance1 = executor.submit(() -> realization1.clickOnBetTypeAndReturnBalanceAsRub(bookmaker1Final, calculatedFinal.fork().betInfo1(), calculatedFinal.fork().sport()));

            var balance2Rub = new BigDecimal("1000000000");

            if (!isValue) {
                var futureBalance2 = executor.submit(() -> realization2.clickOnBetTypeAndReturnBalanceAsRub(bookmaker2Final, calculatedFinal.fork().betInfo2(), calculatedFinal.fork().sport()));
                balance2Rub = futureBalance2.get(30, TimeUnit.SECONDS);
            }

            var balance1Rub = futureBalance1.get(30, TimeUnit.SECONDS);

            var bets = calculateBetsSize(
                bkParams1.currency(),
                bkParams2.currency(),
                balance1Rub,
                balance2Rub,
                Context.currencyToRubCourse.get(bkParams1.currency()).multiply(bkParams1.minBetSum()),
                Context.currencyToRubCourse.get(bkParams2.currency()).multiply(bkParams2.minBetSum()),
                Context.currencyToRubCourse.get(bkParams1.currency()).multiply(bkParams1.maxBetSum()),
                Context.currencyToRubCourse.get(bkParams2.currency()).multiply(bkParams2.maxBetSum()),
                calculatedFinal.betCoef1(),
                calculatedFinal.betCoef2()
            );

            var bet1 = BigDecimal.valueOf(bets.get(0));
            var bet2 = BigDecimal.valueOf(bets.get(1));

            var enterSumAndCHeckCfFuture1 = executor.submit(() -> realization1.enterSumAndCheckCf(bookmaker1Final, calculatedFinal.fork().betInfo1(), bet1));

            if (!isValue) {
                var enterSumAndCHeckCfFuture2 = executor.submit(() -> realization2.enterSumAndCheckCf(bookmaker2Final, calculatedFinal.fork().betInfo2(), bet2));
                enterSumAndCHeckCfFuture2.get(30, TimeUnit.SECONDS);
            }

            enterSumAndCHeckCfFuture1.get(30, TimeUnit.SECONDS);

            var realCf1 = BigDecimal.ZERO;
            var realCf2 = BigDecimal.ZERO;

            try {
                var betFuture1 = executor.submit(() -> realization1.placeBetAndGetRealCf(bookmaker1Final, calculatedFinal.fork().betInfo1(), true, new BigDecimal("0")));
                realCf1 = betFuture1.get(30, TimeUnit.SECONDS);
            } catch (ExecutionException | TimeoutException e) {
                throw new RuntimeException("Не удалось поставить вилку");
            }

            var isClosed = false;
            if (!isValue && !isVerifiedValue) {
                try {
                    BigDecimal finalRealCf = realCf1;
                    var betFuture2 = executor.submit(() -> realization2.placeBetAndGetRealCf(bookmaker2Final, calculatedFinal.fork().betInfo2(), false, finalRealCf));
                    realCf2 = betFuture2.get(30, TimeUnit.SECONDS);
                } catch (ExecutionException | TimeoutException e) {
                    if (bookmaker1Final.equals(Bookmaker._188BET)) {
                        isClosed = BetsSupport.cashOut(Context.screenManager.getScreenForBookmaker(bookmaker1Final));
                    }

                    Logger.writeToLogSession("Не удалось поставить плечо(%s) - %s".formatted(
                        isClosed ? "Сделали CashOut" : "Не сделали CashOut",
                        calculatedFinal.fork().betInfo2().BK_name())
                    );
                }
            }

            executor.shutdownNow();

            var fork = calculatedFinal.fork();
            Context.forksCache.put(new MathUtils.ForkKey(fork.betInfo1().BK_name(), fork.eventId(), fork.betInfo1().BK_bet()), fork);
            Context.forksCache.put(new MathUtils.ForkKey(fork.betInfo2().BK_name(), fork.eventId(), fork.betInfo2().BK_bet()), fork);

            var bet1Rub = bet1.multiply(Context.currencyToRubCourse.get(bkParams1.currency()));
            var bet2Rub = bet2.multiply(Context.currencyToRubCourse.get(bkParams2.currency()));

            return buildCompleteBetsFork(calculatedFinal, realCf1, realCf2, balance1Rub, balance2Rub, bet1Rub, bet2Rub, isValue || isVerifiedValue, isClosed);
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } catch (ExecutionException e) {
            throw new RuntimeException("Ошибка в постановке ставки - " + e.getCause().getLocalizedMessage());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка в постановке ставки - " + e.getLocalizedMessage());
        } finally {
            executor.shutdownNow();
        }
    }

    private static MathUtils.CalculatedFork reverseCalculated(MathUtils.CalculatedFork calculated) {
        var fork = reverseFork(calculated.fork());
        return new MathUtils.CalculatedFork(fork, calculated.betCoef2(), calculated.betCoef1());
    }
    private static Parser.Fork reverseFork(Parser.Fork fork) {
        return new Parser.Fork(fork.forkId(), fork.income(), fork.eventId(), fork.sport(), fork.isMiddles(), fork.betType(),
            fork.betInfo2(), fork.betInfo1());
    }

    private static List<Integer> calculateBetsSize(Currency currency1, Currency currency2, BigDecimal balanceRub1,
                                                   BigDecimal balanceRub2, BigDecimal minSt1Rub, BigDecimal minSt2Rub,
                                                   BigDecimal maxSt1Rub, BigDecimal maxSt2Rub, BigDecimal calcCf1,
                                                   BigDecimal calcCf2) {
        if (minSt1Rub.compareTo(balanceRub1) > 0 || minSt2Rub.compareTo(balanceRub2) > 0)
            throw new RuntimeException("Невозможно поставить ставку при текущих настройках");

        var data = new ArrayList<Integer>(2);

        Integer rubValue1 = null;
        Integer rubValue2 = null;

        if (calcCf1.compareTo(calcCf2) > 0) {
            for (int s1 = maxSt1Rub.intValue(); s1 >= minSt1Rub.intValue(); s1--) {
                var tempRubValue2 = calcCf2.multiply(BigDecimal.valueOf(s1)).divide(calcCf1, 0, RoundingMode.DOWN).intValue();
                if (s1 <= balanceRub1.intValue() && tempRubValue2 <= balanceRub2.intValue()
                    && tempRubValue2 >= minSt2Rub.intValue() && tempRubValue2 <= maxSt2Rub.intValue()) {
                    rubValue1 = s1;
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

        var value1 = BigDecimal.valueOf(rubValue1).divide(Context.currencyToRubCourse.get(currency1), 0, RoundingMode.DOWN).intValue();
        var value2 = BigDecimal.valueOf(rubValue2).divide(Context.currencyToRubCourse.get(currency2), 0, RoundingMode.DOWN).intValue();

        if (value1 < 1 || value2 < 1)
            throw new RuntimeException("Невозможно поставить ставку при текущих настройках");

        data.add(value1);
        data.add(value2);

        return data;
    }

    private static BetUtils.CompleteBetsFork buildCompleteBetsFork(MathUtils.CalculatedFork calculated,
                                                                   BigDecimal realCf1, BigDecimal realCf2,
                                                                   BigDecimal balance1Rub, BigDecimal balance2Rub,
                                                                   BigDecimal bet1Rub, BigDecimal bet2Rub, boolean isValue,
                                                                   boolean isClosed) {
        String income;
        BigDecimal realRubBalance1;
        BigDecimal realRubBalance2;

        if (isSuccessFork(realCf1, realCf2)) {
            realRubBalance1 = balance1Rub.subtract(bet1Rub);
            realRubBalance2 = balance2Rub.subtract(bet2Rub);

            income = "1) %s₽. 2) %s₽".formatted(
                bet1Rub.multiply(realCf1).setScale(2, RoundingMode.DOWN),
                bet2Rub.multiply(realCf2).setScale(2, RoundingMode.DOWN)
            );

        } else if (isValue) {
            income = "Был поставлен валуй";
            realRubBalance1 = balance1Rub.subtract(bet1Rub);
            realRubBalance2 = balance2Rub;
            bet2Rub = BigDecimal.ZERO;
        } else if (isFirstForkSuccessAndSecondFail(realCf1, realCf2)) {
            var desc = isClosed ? "Сделан CashOut" : "Не сделан CashOut";
            income = "Одно из плечей не было поставлено(%s)".formatted(desc);
            realRubBalance1 = balance1Rub.subtract(bet1Rub);
            realRubBalance2 = balance2Rub;
            bet2Rub = BigDecimal.ZERO;
        } else {
            throw new RuntimeException("Вилка не была поставлена");
        }

        return new BetUtils.CompleteBetsFork(calculated, income, realRubBalance1, realRubBalance2, bet1Rub, bet2Rub);
    }

    private static boolean isSuccessFork(BigDecimal realCf1, BigDecimal realCf2) {
        return !realCf1.equals(BigDecimal.ZERO) && !realCf2.equals(BigDecimal.ZERO);
    }

    private static boolean isFirstForkSuccessAndSecondFail(BigDecimal realCf1, BigDecimal realCf2) {
        return !realCf1.equals(BigDecimal.ZERO) && realCf2.equals(BigDecimal.ZERO);
    }
}
