package com.melniknow.fd.betting;

import com.melniknow.fd.utils.BetUtils;
import com.melniknow.fd.utils.MathUtils;

public class BetMaker {
    public static BetUtils.CompleteBetsFork make(MathUtils.CalculatedFork calculated) {
        var bookmaker1 = BetUtils.getBookmakerByNameInApi(calculated.fork().bkName1());
        var bookmaker2 = BetUtils.getBookmakerByNameInApi(calculated.fork().bkName2());

        var realization1 = bookmaker1.realization;
        var realization2 = bookmaker2.realization;

        realization1.stepA();
        realization2.stepA();

        realization1.stepB();
        realization2.stepB();

        return new BetUtils.CompleteBetsFork(calculated, "some info");
    }
}
