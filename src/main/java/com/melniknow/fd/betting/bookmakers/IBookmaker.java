package com.melniknow.fd.betting.bookmakers;

import com.melniknow.fd.utils.BetUtils.Proxy;
import com.melniknow.fd.utils.MathUtils;
import org.openqa.selenium.chrome.ChromeDriver;

public interface IBookmaker {
    void openLink(ChromeDriver driver, Proxy proxy, MathUtils.CalculatedFork calculated);
    void clickOnBetType();
    void enterSumAndCheckCf();
    void placeBet();
}
