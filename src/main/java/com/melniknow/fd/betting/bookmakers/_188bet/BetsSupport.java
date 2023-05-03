package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.domain.Currency;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

public class BetsSupport {
    public static String getTotalsByStr(String str) {
        return str.split("\n")[1];
    }

    public static String getPartOfGameByMarketName(String marketName) {
        if (marketName.contains(" - ")) {
            return marketName.split(" - ")[1];
        } else {
            return "";
        }
    }

    public static String getTeamFirstNameByTitle(String title) {
        if (title.split("vs", -1).length - 1 == 1) {
            return title.substring(0, title.indexOf("vs") - 1);
        }
        return null;
    }

    public static String getTeamSecondNameByTitle(String title) {
        if (title.split("vs", -1).length - 1 == 1) {
            return title.substring(title.indexOf("vs") + 3);
        }
        return null;
    }

    public static String buildLine(String line) {
        if (!line.startsWith("-") && !line.startsWith("+") && !line.equals("0")) {
            line = "+" + line;
        }
        return line;
    }

    public static void sleep(Long milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }

    public static WebElement findElementWithClicking(WebElement element, By by) throws InterruptedException {
        WebElement res;
        try {
            res = element.findElement(by);
            return res;
        } catch (NoSuchElementException e) {
            element.click();
            sleep(300L);
            return element.findElement(by);
        }
    }

    public static List<WebElement> findElementsWithClicking(WebElement element, By by) throws InterruptedException {
        List<WebElement> res;
        try {
            res = element.findElements(by);
            return res;
        } catch (NoSuchElementException e) {
            element.click();
            sleep(300L);
            return element.findElements(by);
        }
    }

    public static boolean containsItem(WebElement market, String partOfGame) {
        try {
            market.findElement(SeleniumSupport.buildLocalSpanByText(partOfGame));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public static boolean isCorrectMarket(WebElement market, String partOfGame) {
        if (partOfGame.isEmpty()) {
            return isPureMarket(market);
        }
        return containsItem(market, partOfGame);
    }

    public static boolean isPureMarket(WebElement market) {
        return !market.getText().contains("\n");
    }

    public static WebElement getMarketByMarketName(ChromeDriver driver,
                                                   By byMarketName,
                                                   String partOfGame) throws InterruptedException {
        sleep(500L);
        clearPreviousBets(driver);
        return getMarketImpl(driver, byMarketName, partOfGame);
    }

    public static WebElement getMarketImpl(ChromeDriver driver, By byName, String partOfGame) throws InterruptedException {
        int scrollPosition = 0;
        int scroll = ((Number) ((JavascriptExecutor) driver).executeScript("return window.innerHeight")).intValue();
        int curScroll = scroll / 4;
        while (scrollPosition < 10000) {
            try {
                List<WebElement> visibleMarkets = driver.findElements(byName);
                for (var market : visibleMarkets) {
                    var parent = SeleniumSupport.getParentByDeep(market, 2);
                    if (isCorrectMarket(parent, partOfGame)) {
                        parent = SeleniumSupport.getParentByDeep(market, 3);
                        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, " + scroll / 2 + ")");
                        return parent;
                    }
                }
            } catch (NoSuchElementException e) { }
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, " + curScroll + ")");
            sleep(500L); // Wait for the page to finish scrolling
            scrollPosition += curScroll;
        }
        throw new RuntimeException("Market not found" + byName.toString() + " [188bet]");
    }

    public static void clearPreviousBets(ChromeDriver driver) {
        WebDriverWait wait_ = new WebDriverWait(driver, Duration.ofSeconds(10));
        var button = wait_.until(driver1 -> driver1.findElement(By.xpath("//h4[text()='Bet Slip']")));
        button = SeleniumSupport.getParentByDeep(button, 1);
        try {
            var countOfPreviousBets = button.findElement(By.xpath(".//h1[text()!='0']"));
            countOfPreviousBets.click();
            // TODO: need some sleep?
            wait_.until((ExpectedConditions.elementToBeClickable(By.cssSelector("[data-btn-trash-can='true']")))).click();
            wait_.until((ExpectedConditions.elementToBeClickable(By.cssSelector("[data-btn-remove-all='true']")))).click();
        } catch (NoSuchElementException e) {
        }
    }

    public static void closeBetWindow(ChromeDriver driver) {
        try {
            var wait = new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                driver_ -> driver_.findElement(By.xpath("//span[text()='@']")));
            var tmp = SeleniumSupport.getParentByDeep(wait, 1);
            // waiting?
            tmp.findElement(By.xpath(".//following::div[1]")).click();
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("Don`t close mini window! [188bet]");
        }
    }

    public static BigDecimal getBalance(ChromeDriver driver, Currency currency) {
        try {
            var balanceButton = new WebDriverWait(driver, Duration.ofSeconds(60)).until(driver1
                -> driver1.findElement(By.className("print:text-black/80")).getText());
            balanceButton = balanceButton.substring(4).replace(",", "");
            var balance = new BigDecimal(balanceButton);
            System.out.println("Balance from header THB: " + balance + " [188bet]");
            return balance.multiply(Context.currencyToRubCourse.get(currency));
        } catch (NoSuchElementException e) {
            System.out.println("Balance in mini-window not found  [188bet]");
            throw new RuntimeException("Balance not found [188bet]");
        }
    }

    public static BigDecimal getCurrentCf(ChromeDriver driver) {
        WebElement tmpTitle = new WebDriverWait(driver, Duration.ofSeconds(30))
            .until(driver1 -> driver1.findElement(By.xpath("//span[text()='@']")));

        var title = SeleniumSupport.getParentByDeep(tmpTitle, 1).getText();
        return new BigDecimal(title.substring(title.indexOf("@") + 1));
    }

    public static void closeAfterSuccessfulBet(ChromeDriver driver) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(driver_ ->
                    driver_.findElement(By.cssSelector("[data-txt-bet-status='Confirmed']")));


            WebElement tmpButton = new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(driver_ -> SeleniumSupport.getParentByDeep(
                    driver_.findElement(By.xpath("//span[text()='@']")),
                    7));

            tmpButton.findElement(By.xpath(".//h4[text()='OK']")).click();

        } catch (TimeoutException | NoSuchElementException e) {
            System.out.println("Not Close mini-window after success betting!  [188bet]");
        }
    }
}
