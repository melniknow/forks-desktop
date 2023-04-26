package com.melniknow.fd.betting.bookmakers.impl;

import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Sports;
import com.melniknow.fd.utils.BetUtils;
import com.melniknow.fd.utils.MathUtils;
import org.openqa.selenium.chrome.ChromeDriver;

public class Pinnacle implements IBookmaker {
    @Override
    public void openLink(ChromeDriver driver, BetUtils.Proxy proxy, String link) {

    }

    @Override
    public void clickOnBetType(ChromeDriver driver, BetUtils.Proxy proxy, Parser.BetInfo info, Sports sport) {

    }

    @Override
    public void enterSumAndCheckCf(ChromeDriver driver, BetUtils.Proxy proxy, Parser.BetInfo info) {

    }
    @Override
    public void placeBet(ChromeDriver driver, BetUtils.Proxy proxy, Parser.BetInfo info) {

    }
}
