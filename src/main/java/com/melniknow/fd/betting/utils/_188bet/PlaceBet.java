package com.melniknow.fd.betting.utils._188bet;

import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import java.math.BigDecimal;

public class PlaceBet {
    public static void PlaceBet(ChromeDriver driver, BigDecimal betCoef, BigDecimal curCf, Parser.BetInfo info) {
        var placeBetButton = driver.findElement(By.xpath("//h4[text()='Please Log In']"));



        placeBetButton.click();
    }
}
