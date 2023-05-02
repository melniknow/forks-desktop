package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.betting.bookmakers.BetsSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;

public class PlaceBet {

    private static boolean isClickable(ChromeDriver driver, By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            wait.until(driver_ -> driver_.findElement(by));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private static final By byAccepChanges = By.xpath("//h4[text()='Accept Changes']");
    private static final By byPlaceBet = By.xpath("//h4[text()='Place Bet']");

    public static BigDecimal placeBet(ChromeDriver driver, Parser.BetInfo info) throws InterruptedException {
        try {
            int tryingPlace = 0;
            while (!isClickable(driver, byPlaceBet) && tryingPlace != 3) {
                if (isClickable(driver, byAccepChanges)) {
                    driver.findElement(byAccepChanges).click();
                }
                tryingPlace++;
            }
            driver.findElement(byPlaceBet).click();

            // Wait response of successfully
            new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.
                visibilityOfElementLocated(By.xpath("//h4[text()='Your bet has been successfully placed.']")));

//            new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.
//                visibilityOfElementLocated(By.xpath("//h4[text()='Confirmed']")));

            var realCf = BetsSupport.getCurrentCf(driver);
            BetsSupport.closeAfterSuccessfulBet(driver);
            BetsSupport.closeBetWindow(driver);
            return realCf;
        } catch (NoSuchElementException e) {
            BetsSupport.closeBetWindow(driver);
            System.out.println("Don`t Place Bet");
            throw new RuntimeException("Don`t Place Bet");
        }
    }
}
