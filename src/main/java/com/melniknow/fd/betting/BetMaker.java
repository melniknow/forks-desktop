package com.melniknow.fd.betting;

import com.melniknow.fd.domain.BetType;
import com.melniknow.fd.utils.BetsUtils;
import com.melniknow.fd.utils.MathUtils;

public class BetMaker {
    public static BetsUtils.CompleteBetsFork make(MathUtils.CalculatedFork fork) {
        if (fork.fork().type1() == BetType.MIX) return null;

        return new BetsUtils.CompleteBetsFork(fork, "some info");
    }
}
