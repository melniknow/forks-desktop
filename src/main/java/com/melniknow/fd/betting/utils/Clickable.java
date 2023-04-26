package com.melniknow.fd.betting.utils;

import com.melniknow.fd.core.Parser;
import com.melniknow.fd.utils.BetUtils;
import org.openqa.selenium.chrome.ChromeDriver;

public interface Clickable {
    void click(ChromeDriver driver, BetUtils.Proxy proxy, Parser.BetInfo info);
}
