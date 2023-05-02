package com.melniknow.fd.betting;

import com.melniknow.fd.core.Logger;
import com.melniknow.fd.utils.BetUtils;
import com.melniknow.fd.utils.MathUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BetMaker {
    public static BetUtils.CompleteBetsFork make(MathUtils.CalculatedFork calculated) throws InterruptedException {
        try {
            var bookmaker1 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo1().BK_name());
            var bookmaker2 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo2().BK_name());

            if (bookmaker1.equals(bookmaker2))
                throw new RuntimeException("Букмекеры в вилке должны различаться");

            var realization1 = bookmaker1.realization;
            var realization2 = bookmaker2.realization;

            realization1.openLink(bookmaker1, calculated.fork().betInfo1());
            realization2.openLink(bookmaker2, calculated.fork().betInfo2());

            var balance1 = realization1.clickOnBetTypeAndReturnBalanceAsRub(bookmaker1, calculated.fork().betInfo1(), calculated.fork().sport());
            var balance2 = realization2.clickOnBetTypeAndReturnBalanceAsRub(bookmaker2, calculated.fork().betInfo2(), calculated.fork().sport());

            // TODO: Математика суммы ставки
            var bet1 = BigDecimal.ONE;
            var bet2 = BigDecimal.ONE;

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

            if (!realCf1.equals(BigDecimal.ZERO) && !realCf2.equals(BigDecimal.ZERO))
                income = ((bet1.multiply(realCf1)).add((bet2.multiply(realCf2)))).divide(new BigDecimal("2"), 4, RoundingMode.DOWN);

            return new BetUtils.CompleteBetsFork(calculated, income);
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка в постановке ставки - " + e.getMessage());
        }
    }
}
