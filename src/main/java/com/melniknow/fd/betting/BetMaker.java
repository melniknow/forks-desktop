package com.melniknow.fd.betting;

import com.melniknow.fd.Context;
import com.melniknow.fd.core.Logger;
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
        var executor = Executors.newFixedThreadPool(8);

        System.out.println(calculated);

        try {
            var bookmaker1 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo1().BK_name());
            var bookmaker2 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo2().BK_name());

            var bkParams1 = Context.betsParams.get(bookmaker1);
            var bkParams2 = Context.betsParams.get(bookmaker2);

            if (bookmaker1.equals(bookmaker2))
                throw new RuntimeException("Букмекеры в вилке должны различаться");

            var realization1 = bookmaker1.realization;
            var realization2 = bookmaker2.realization;

            var openLink1 = executor.submit(() -> realization1.openLink(bookmaker1, calculated.fork().betInfo1()));
            var openLink2 = executor.submit(() -> realization2.openLink(bookmaker2, calculated.fork().betInfo2()));

            openLink1.get(30, TimeUnit.SECONDS);
            openLink2.get(30, TimeUnit.SECONDS);

            var futureBalance1 = executor.submit(() -> realization1.clickOnBetTypeAndReturnBalanceAsRub(bookmaker1, calculated.fork().betInfo1(), calculated.fork().sport()));
            var futureBalance2 = executor.submit(() -> realization2.clickOnBetTypeAndReturnBalanceAsRub(bookmaker2, calculated.fork().betInfo2(), calculated.fork().sport()));

            var balance1Rub = futureBalance1.get(30, TimeUnit.SECONDS);
            var balance2Rub = futureBalance2.get(30, TimeUnit.SECONDS);

            System.out.println("Balance 1 = " + balance1Rub);
            System.out.println("Balance 2 = " + balance2Rub);

            System.out.println("Bookmaker 1 = " + bookmaker1);
            System.out.println("Bookmaker 2 = " + bookmaker2);

            var bets = calculateBetsSize(
                bkParams1.currency(),
                bkParams2.currency(),
                balance1Rub,
                balance2Rub,
                Context.currencyToRubCourse.get(bkParams1.currency()).multiply(bkParams1.minBetSum()),
                Context.currencyToRubCourse.get(bkParams2.currency()).multiply(bkParams2.minBetSum()),
                Context.currencyToRubCourse.get(bkParams1.currency()).multiply(bkParams1.maxBetSum()),
                Context.currencyToRubCourse.get(bkParams2.currency()).multiply(bkParams2.maxBetSum()),
                calculated.betCoef1(),
                calculated.betCoef2()
            );

            System.out.println(bets);

            var bet1 = BigDecimal.valueOf(bets.get(0));
            var bet2 = BigDecimal.valueOf(bets.get(1));

            var enterSumAndCHeckCfFuture1 = executor.submit(() -> realization1.enterSumAndCheckCf(bookmaker1, calculated.fork().betInfo1(), bet1));
            var enterSumAndCHeckCfFuture2 = executor.submit(() -> realization2.enterSumAndCheckCf(bookmaker2, calculated.fork().betInfo2(), bet2));

            enterSumAndCHeckCfFuture1.get(30, TimeUnit.SECONDS);
            enterSumAndCHeckCfFuture2.get(30, TimeUnit.SECONDS);

            var realCf1 = BigDecimal.ZERO;
            var realCf2 = BigDecimal.ZERO;

            if (bookmaker1 == Bookmaker._188BET) {
                try {
                    var betFuture1 = executor.submit(() -> realization1.placeBetAndGetRealCf(bookmaker1, calculated.fork().betInfo1()));
                    realCf1 = betFuture1.get(30, TimeUnit.SECONDS);
                } catch (ExecutionException | TimeoutException e) {
                    throw new RuntimeException("Не удалось поставить вилку");
                }

                try {
                    var betFuture2 = executor.submit(() -> realization2.placeBetAndGetRealCf(bookmaker2, calculated.fork().betInfo2()));
                    realCf2 = betFuture2.get(30, TimeUnit.SECONDS);
                } catch (ExecutionException | TimeoutException e) {
                    Logger.writeToLogSession("Не удалось поставить плечо - %s".formatted(calculated.fork().betInfo2().BK_name()));
                }
            } else {
                try {
                    var betFuture2 = executor.submit(() -> realization2.placeBetAndGetRealCf(bookmaker2, calculated.fork().betInfo2()));
                    realCf2 = betFuture2.get(30, TimeUnit.SECONDS);
                } catch (ExecutionException | TimeoutException e) {
                    throw new RuntimeException("Не удалось поставить вилку");
                }

                try {
                    var betFuture1 = executor.submit(() -> realization1.placeBetAndGetRealCf(bookmaker1, calculated.fork().betInfo1()));
                    realCf1 = betFuture1.get(30, TimeUnit.SECONDS);
                } catch (ExecutionException | TimeoutException e) {
                    Logger.writeToLogSession("Не удалось поставить плечо - %s".formatted(calculated.fork().betInfo1().BK_name()));
                }
            }

            executor.shutdownNow();

            System.out.println("RealCf1 = " + realCf1);
            System.out.println("RealCf2 = " + realCf2);

            var income = BigDecimal.ZERO;

            if (!realCf1.equals(BigDecimal.ZERO) && !realCf2.equals(BigDecimal.ZERO)) {
                if (realCf1.compareTo(calculated.fork().betInfo1().BK_cf()) < 0 ||
                    realCf2.compareTo(calculated.fork().betInfo2().BK_cf()) < 0)
                    return new BetUtils.CompleteBetsFork(calculated, "Вилка была поставлена по изменённым в худшую сторону коэффициентам");

                var bet1Rub = bet1.multiply(Context.currencyToRubCourse.get(bkParams1.currency()));
                var bet2Rub = bet2.multiply(Context.currencyToRubCourse.get(bkParams2.currency()));

                income = (((bet1Rub.multiply(realCf1)).add((bet2Rub.multiply(realCf2))))
                    .divide(new BigDecimal("2"), 4, RoundingMode.DOWN)).subtract((bet1Rub.add(bet2Rub)));

                return new BetUtils.CompleteBetsFork(calculated, income.setScale(2, RoundingMode.DOWN).toString());
            }

            return new BetUtils.CompleteBetsFork(calculated, "Одно из плечей не было поставлено");
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка в постановке ставки - " + e.getMessage());
        } finally {
            executor.shutdownNow();
        }
    }

    private static List<Integer> calculateBetsSize(Currency currency1, Currency currency2, BigDecimal balanceRub1,
                                                   BigDecimal balanceRub2, BigDecimal minSt1Rub, BigDecimal minSt2Rub,
                                                   BigDecimal maxSt1Rub, BigDecimal maxSt2Rub, BigDecimal calcCf1,
                                                   BigDecimal calcCf2) {
        if (minSt1Rub.compareTo(balanceRub1) > 0 || minSt2Rub.compareTo(balanceRub2) > 0)
            throw new RuntimeException("Невозможно поставить ставку при текущих депозитах");

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
            throw new RuntimeException("Невозможно поставить ставку при текущих депозитах");

        var value1 = BigDecimal.valueOf(rubValue1).divide(Context.currencyToRubCourse.get(currency1), 0, RoundingMode.DOWN).intValue();
        var value2 = BigDecimal.valueOf(rubValue2).divide(Context.currencyToRubCourse.get(currency2), 0, RoundingMode.DOWN).intValue();

        if (value1 < 1 || value2 < 1)
            throw new RuntimeException("Невозможно поставить ставку при текущих депозитах");

        data.add(value1);
        data.add(value2);

        return data;
    }
}