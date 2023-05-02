package com.melniknow.fd.betting.bookmakers.impl._188bet;

import com.melniknow.fd.betting.bookmakers.impl.BetsSupport;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.utils.BetUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;

public class EnterSumAndCheckCf {
    public static void enterSumAndCheckCf(ChromeDriver driver, Parser.BetInfo info, BigDecimal sum) {
        try {
            // Get Cf
            var currentCf = BetsSupport.getCurrentCf(driver);
            if (currentCf.compareTo(info.BK_cf()) < 0) {
                throw new RuntimeException("betCoef is too low");
            }

            // Enter sum
            if (sum.compareTo(new BigDecimal("50")) < 0) {

                throw new RuntimeException("Very small min Bet");
            }
            // TODO check MAX
            WebElement enterSnake = driver.findElement(By.cssSelector("[placeholder='Enter Stake']"));

            enterSnake.sendKeys(sum.toString());

        } catch (RuntimeException e) {
            BetsSupport.closeBetWindow(driver);
            throw new RuntimeException("Don`t enter Stake!");
        }
    }
}


