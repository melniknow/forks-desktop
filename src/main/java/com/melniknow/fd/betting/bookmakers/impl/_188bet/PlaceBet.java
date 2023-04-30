package com.melniknow.fd.betting.bookmakers.impl._188bet;

import com.melniknow.fd.betting.bookmakers.impl.BetsSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import java.math.BigDecimal;
import java.time.Duration;

public class PlaceBet {
    public static void PlaceBet(ChromeDriver driver, BigDecimal betCoef, BigDecimal curCf, Parser.BetInfo info) {
        var placeBetButton = driver.findElement(By.xpath("//h4[text()='Please Log In']"));


//        placeBetButton.click();


        // close mini-window
        BetsSupport.getParentByDeep(driver.findElement(BetsSupport.buildSpanByText("@")), 1)
            .findElement(By.xpath(".//following::div[1]")).click();
    }
}
