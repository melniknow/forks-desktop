package com.melniknow.fd.betting;

import com.melniknow.fd.utils.BetUtils;
import com.melniknow.fd.utils.MathUtils;

public class BetMaker {
    public static BetUtils.CompleteBetsFork make(MathUtils.CalculatedFork calculated) throws InterruptedException {
        var bookmaker1 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo1().BK_name());
        var bookmaker2 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo2().BK_name());

        var realization1 = bookmaker1.realization;
        var realization2 = bookmaker2.realization;

        realization1.openLink(bookmaker1, calculated.fork().betInfo1());
        realization2.openLink(bookmaker2, calculated.fork().betInfo2());

        var balance1 = realization1.clickOnBetTypeAndReturnBalanceAsRub(bookmaker1, calculated.fork().betInfo1(), calculated.fork().sport());
        var balance2 = realization2.clickOnBetTypeAndReturnBalanceAsRub(bookmaker2, calculated.fork().betInfo2(), calculated.fork().sport());

        var bet1 = 1; // посчитали их здесь с учётом балансов, коэфициентов, валют, минимальной и максимальной ставкой и если всё норм, то ставим
        var bet2 = 1;

        var curCf1 = realization1.enterSumAndGetCf(bookmaker1, calculated.betCoef1(), calculated.fork().betInfo1());
        var curCf2 = realization2.enterSumAndGetCf(bookmaker2, calculated.betCoef2(), calculated.fork().betInfo2());

        try {
            realization1.placeBet(bookmaker1, calculated.betCoef1(), curCf1, calculated.fork().betInfo1());
            realization2.placeBet(bookmaker2, calculated.betCoef2(), curCf2, calculated.fork().betInfo2());

            System.out.println("Ставка поставлена!");
            var balance1_ = realization1.clickOnBetTypeAndReturnBalanceAsRub(bookmaker1, calculated.fork().betInfo1(), calculated.fork().sport());
            var balance2_ = realization2.clickOnBetTypeAndReturnBalanceAsRub(bookmaker2, calculated.fork().betInfo2(), calculated.fork().sport());
            System.out.println(balance1_);
            System.out.println(balance2_);
            System.out.println("---------------");
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка в постановке ставки - " + e.getMessage());
        }

        return new BetUtils.CompleteBetsFork(calculated, "some info");
    }
}
