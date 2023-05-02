package com.melniknow.fd.betting;

import com.melniknow.fd.utils.BetUtils;
import com.melniknow.fd.utils.MathUtils;

import java.math.BigDecimal;

public class BetMaker {
    public static BetUtils.CompleteBetsFork make(MathUtils.CalculatedFork calculated) throws InterruptedException {
        var bookmaker1 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo1().BK_name());
        var bookmaker2 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo2().BK_name());

        if (bookmaker1.equals(bookmaker2)) throw new RuntimeException("Букмекеры в вилке должны различаться");

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

        try {
            // TODO: Многопоточность + обработка того, что одна или обе ставки могли не поставиться
            var realCf1 = realization1.placeBetAndGetRealCf(bookmaker1, calculated.fork().betInfo1());
            var realCf2 = realization2.placeBetAndGetRealCf(bookmaker2, calculated.fork().betInfo2());

            // TODO: Поменять CompleteBetsFork, tg, Logger
            return new BetUtils.CompleteBetsFork(calculated, "some info");
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка в постановке ставки - " + e.getMessage());
        }
    }
}