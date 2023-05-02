package com.melniknow.fd.betting.bookmakers.impl._188bet;

import com.melniknow.fd.betting.bookmakers.impl.BetsSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;

public class PlaceBet {
    public static BigDecimal placeBet(ChromeDriver driver, Parser.BetInfo info) throws InterruptedException {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            try {
                wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//h4[text()='Place Bet']"))).click();
            } catch (TimeoutException e) {
                try {
                    var acceptChanges = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//h4[text()='Accept Changes']")));
                    acceptChanges.click();

                    wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//h4[text()='Place Bet']"))).click();
                } catch (TimeoutException e1) {
                    BetsSupport.closeBetWindow(driver);
                    throw new RuntimeException("Don`t Place Bet");
                }
            }
            // Wait response of successfully
            new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.
                visibilityOfElementLocated(By.xpath("//h4[text()='Your bet has been successfully placed.']")));

//            new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.
//                visibilityOfElementLocated(By.xpath("//h4[text()='Confirmed']")));

            var realCf = BetsSupport.getCurrentCf(driver);
            BetsSupport.closeAfterSuccessfulBet(driver);
            return realCf;
        } catch (NoSuchElementException e) {
            BetsSupport.closeBetWindow(driver);
            System.out.println("Don`t Place Bet");
            throw new RuntimeException("Don`t Place Bet");
        }
    }
}
