package com.melniknow.fd.betting.bookmakers;

import com.melniknow.fd.core.Parser;
import com.melniknow.fd.utils.BetUtils.Proxy;
import com.melniknow.fd.utils.MathUtils;
import org.openqa.selenium.chrome.ChromeDriver;

public interface IBookmaker {
    void openLink(ChromeDriver driver, Proxy proxy, String link);
    void clickOnBetType(ChromeDriver driver, Proxy proxy, Parser.BetInfo info, String sport);
    void enterSumAndCheckCf(ChromeDriver driver, Proxy proxy, Parser.BetInfo info);
    void placeBet(ChromeDriver driver, Proxy proxy, Parser.BetInfo info);
}
