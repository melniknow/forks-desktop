package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;

public class EnterSumAndCheckCf {
    public static void enterSumAndCheckCf(ChromeDriver driver, Parser.BetInfo info, BigDecimal sum) {
        try {
            var currentCf = BetsSupport.getCurrentCf(driver);
            System.out.println("Current cf = " + currentCf);
            if (currentCf.compareTo(info.BK_cf()) < 0) {
                throw new RuntimeException("betCoef is too low [188bet]");
            }

            if (sum.compareTo(new BigDecimal("50")) < 0) {
//                sum = new BigDecimal("50");
                throw new RuntimeException("Very small min Bet [188bet]");
            }
            // TODO: check MAX bet
            WebElement enterSnake = new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(driver_ ->
                    driver_.findElement(By.cssSelector("[placeholder='Enter Stake']")));

            enterSnake.sendKeys(sum.toString());
        } catch (RuntimeException e) {
            BetsSupport.closeBetWindow(driver);
            throw new RuntimeException(e.getMessage());
        }
    }
}


