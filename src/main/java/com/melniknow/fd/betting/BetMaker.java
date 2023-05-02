package com.melniknow.fd.betting;

import com.melniknow.fd.Context;
import com.melniknow.fd.core.Logger;
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

public class BetMaker {
    public static BetUtils.CompleteBetsFork make(MathUtils.CalculatedFork calculated) throws InterruptedException {
        try {
            var bookmaker1 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo1().BK_name());
            var bookmaker2 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo2().BK_name());

            var bkParams1 = Context.betsParams.get(bookmaker1);
            var bkParams2 = Context.betsParams.get(bookmaker2);

            if (bookmaker1.equals(bookmaker2))
                throw new RuntimeException("Букмекеры в вилке должны различаться");

            var realization1 = bookmaker1.realization;
            var realization2 = bookmaker2.realization;

            realization1.openLink(bookmaker1, calculated.fork().betInfo1());
            realization2.openLink(bookmaker2, calculated.fork().betInfo2());

            var balance1Rub = realization1.clickOnBetTypeAndReturnBalanceAsRub(bookmaker1, calculated.fork().betInfo1(), calculated.fork().sport());
            var balance2Rub = realization2.clickOnBetTypeAndReturnBalanceAsRub(bookmaker2, calculated.fork().betInfo2(), calculated.fork().sport());

            var bets = calculateBetsSize(
                bkParams1.currency(),
                bkParams2.currency(),
                balance1Rub,
                balance2Rub,
                Context.currencyToRubCourse.get(bkParams1.currency()).multiply(bkParams1.minBetSum()),
                Context.currencyToRubCourse.get(bkParams2.currency()).multiply(bkParams2.minBetSum()),
                Context.currencyToRubCourse.get(bkParams1.currency()).multiply(bkParams1.maxBetSum()),
                Context.currencyToRubCourse.get(bkParams2.currency()).multiply(bkParams2.maxBetSum()),
                calculated.fork().betInfo1().BK_cf(),
                calculated.fork().betInfo2().BK_cf()
            );

            var bet1 = bets.get(0);
            var bet2 = bets.get(1);

            realization1.enterSumAndCheckCf(bookmaker1, calculated.fork().betInfo1(), bet1);
            realization2.enterSumAndCheckCf(bookmaker2, calculated.fork().betInfo2(), bet2);

            var executor = Executors.newFixedThreadPool(2);

            var future1 = executor.submit(() -> realization1.placeBetAndGetRealCf(bookmaker1, calculated.fork().betInfo1()));
            var future2 = executor.submit(() -> realization2.placeBetAndGetRealCf(bookmaker2, calculated.fork().betInfo2()));

            var realCf1 = BigDecimal.ZERO;
            var realCf2 = BigDecimal.ZERO;

            try {
                realCf1 = future1.get(1, TimeUnit.MINUTES);
            } catch (ExecutionException e) {
                Logger.writeToLogSession("Не удалось поставить плечо - %s".formatted(calculated.fork().betInfo1().BK_name()));
            }

            try {
                realCf2 = future2.get(1, TimeUnit.MINUTES);
            } catch (ExecutionException e) {
                Logger.writeToLogSession("Не удалось поставить плечо - %s".formatted(calculated.fork().betInfo2().BK_name()));
            }

            executor.shutdownNow();

            var income = BigDecimal.ZERO;

            if (!realCf1.equals(BigDecimal.ZERO) && !realCf2.equals(BigDecimal.ZERO)) {
                var bet1Rub = bet1.multiply(Context.currencyToRubCourse.get(bkParams1.currency()));
                var bet2Rub = bet2.multiply(Context.currencyToRubCourse.get(bkParams2.currency()));

                income = (((bet1Rub.multiply(realCf1)).add((bet2Rub.multiply(realCf2))))
                    .divide(new BigDecimal("2"), 4, RoundingMode.DOWN)).subtract((bet1Rub.add(bet2Rub)));
            }

            return new BetUtils.CompleteBetsFork(calculated, income.setScale(2, RoundingMode.DOWN));
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка в постановке ставки - " + e.getMessage());
        }
    }

    private static List<BigDecimal> calculateBetsSize(Currency currency1, Currency currency2, BigDecimal balanceRub1,
                                                      BigDecimal balanceRub2, BigDecimal minSt1Rub, BigDecimal minSt2Rub,
                                                      BigDecimal maxSt1Rub, BigDecimal maxSt2Rub, BigDecimal cf1,
                                                      BigDecimal cf2) {
        var data = new ArrayList<BigDecimal>(2);

        data.add(BigDecimal.ONE);
        data.add(BigDecimal.ONE);

        return data;
    }
}