package com.melniknow.fd.betting;

import com.melniknow.fd.utils.BetUtils;
import com.melniknow.fd.utils.MathUtils;

public class BetMaker {
    public static BetUtils.CompleteBetsFork make(MathUtils.CalculatedFork calculated) {
        var bookmaker1 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo1().BK_name());
        var bookmaker2 = BetUtils.getBookmakerByNameInApi(calculated.fork().betInfo2().BK_name());

        var realization1 = bookmaker1.realization;
        var realization2 = bookmaker2.realization;

        realization1.openLink(bookmaker1, calculated.fork().betInfo1());
        realization2.openLink(bookmaker2, calculated.fork().betInfo2());

        realization1.clickOnBetType(bookmaker1, calculated.fork().betInfo1(), calculated.fork().sport());
        realization2.clickOnBetType(bookmaker2, calculated.fork().betInfo2(), calculated.fork().sport());

        realization1.enterSumAndCheckCf(bookmaker1, calculated.betCoef1(), calculated.fork().betInfo1());
        realization1.enterSumAndCheckCf(bookmaker2, calculated.betCoef2(), calculated.fork().betInfo2());

        return new BetUtils.CompleteBetsFork(calculated, "some info");
    }
}
