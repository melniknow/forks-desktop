package com.melniknow.fd.betting.utils._188bet;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.utils.BetsSupport;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.utils.BetUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;

public class EnterSumAndCheckCf {
    public static void enterSumAndCheckCf(ChromeDriver driver, Parser.BetInfo info, BigDecimal betCoef, BetUtils.BetsParams betsParams) {
        // find 'Enter Stake' -> parent up 7 -> find '@'
        var mainWindow = new WebDriverWait(driver, Duration.ofSeconds(200))
            .until(driver_ -> BetsSupport.getParentByDeep(
                driver_.findElement(By.cssSelector("[placeholder='Enter Stake']")),
                7)); // find main block

        var tmpButton = mainWindow.findElement(By.xpath(".//span[text()='@']"));

        var title = BetsSupport.getParentByDeep(tmpButton, 1).getText();
        var currentCf = new BigDecimal(title.substring(title.indexOf("@") + 1));
        System.out.println(currentCf);

        if (currentCf.compareTo(betCoef) < 0) {
            throw new RuntimeException("betCoef is too low");
        }

        var enterSnake = mainWindow.findElement(By.cssSelector("[placeholder='Enter Stake']"));

        enterSnake.sendKeys(betsParams.maxBetSum().toString());
    }
}


