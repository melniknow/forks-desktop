package com.melniknow.fd.betting;

import com.melniknow.fd.Context;
import com.melniknow.fd.utils.BetUtils;
import com.melniknow.fd.utils.MathUtils;

public class BetMaker {
    public static BetUtils.CompleteBetsFork make(MathUtils.CalculatedFork calculated) {
        var bookmaker1 = BetUtils.getBookmakerByNameInApi(calculated.fork().bkName1());
        var bookmaker2 = BetUtils.getBookmakerByNameInApi(calculated.fork().bkName2());

        var realization1 = bookmaker1.realization;
        var realization2 = bookmaker2.realization;

        var driver1 = Context.screenManager.getScreenForBookmaker(bookmaker1);
        var driver2 = Context.screenManager.getScreenForBookmaker(bookmaker2);

        var proxy1 = Context.screenManager.getProxyForApiBookmaker(bookmaker1);
        var proxy2 = Context.screenManager.getProxyForApiBookmaker(bookmaker2);

        realization1.openLink(driver1, proxy1, calculated);
        realization2.openLink(driver2, proxy2, calculated);

        realization1.clickOnBetType();
        realization2.clickOnBetType();

        return new BetUtils.CompleteBetsFork(calculated, "some info");
    }
}
