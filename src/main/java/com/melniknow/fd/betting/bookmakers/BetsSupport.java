package com.melniknow.fd.betting.bookmakers;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers._188bet.PartOfGame;
import com.melniknow.fd.domain.Currency;
import com.melniknow.fd.domain.Sports;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

public class BetsSupport {

    private static final String firstQuarter = "1st Quarter";
    private static final String secondQuarter = "2nd Quarter";
    private static final String thirdQuarter = "3rd Quarter";
    private static final String fourthQuarter = "4th Quarter";

    private static final String firstHalf = "1st Half";
    private static final String firstSet = "1st Set";

    private static final String secondHalf = "2nd Half";
    private static final String secondSet = "2nd Set";
    private static final String thirdSet = "3rd Set";

    public static String getTotalsByStr(String str) {
        return str.split("\n")[1];
    }

    public static WebElement getParentByDeep(WebElement element, int deep) {
        for (var i = 0; i < deep; i++) element = element.findElement(By.xpath("./.."));
        return element;
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

    public static By buildLocalSpanByText(String text) {
        return By.xpath(".//span[text()='" + text + "']");
    }

    public static By buildLocalDivByText(String text) {
        return By.xpath(".//div[text()='" + text + "']");
    }

    public static By buildLocalH4ByText(String text) {
        return By.xpath(".//h4[text()='" + text + "']");
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

    public static boolean notContainsItem(WebElement market, String item) {
        try {
            market.findElement(buildLocalSpanByText(item));
            return false;
        } catch (NoSuchElementException e) {
            return true;
        }
    }

    public static boolean containsItem(WebElement market, PartOfGame partOfGame) {
        try {
            market.findElement(buildLocalSpanByText(partOfGame.toString()));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public static boolean containsItem(WebElement market, PartOfGame partOfGame, Sports sport) {
        if (partOfGame == PartOfGame.totalGame) {
            return isPureMarket(market, sport);
        }
        return containsItem(market, partOfGame);
    }

    public static boolean isPureMarket(WebElement elem, Sports sport) {
        switch (sport) {
            case BASKETBALL -> {
                return notContainsItem(elem, firstQuarter) &&
                    notContainsItem(elem, secondQuarter) &&
                    notContainsItem(elem, thirdQuarter) &&
                    notContainsItem(elem, fourthQuarter) &&
                    notContainsItem(elem, firstHalf) &&
                    notContainsItem(elem, secondHalf);
            }
            case TENNIS -> {
                return notContainsItem(elem, firstSet) &&
                    notContainsItem(elem, secondSet);
            }
            case SOCCER, HANDBALL -> {
                return notContainsItem(elem, firstHalf) &&
                    notContainsItem(elem, secondHalf);
            }
            case HOCKEY, VOLLEYBALL -> {
                return notContainsItem(elem, firstSet) &&
                    notContainsItem(elem, secondSet) &&
                    notContainsItem(elem, thirdSet);
            }
        }
        return false;
    }

    public static void waitLoadingOfPage(ChromeDriver driver, Sports sport) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        for (int i = 0; i < 60; ++i) {
            try {
                // main button
                wait.until(ExpectedConditions.elementToBeClickable(By.id("tabMT")));
                return;
            } catch (TimeoutException e) { }
            try {
                switch (sport) {
                    case SOCCER -> {
                        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//h4[text()='Popular']")));
                        return;
                    }
                    case TENNIS, BASKETBALL, HOCKEY, VOLLEYBALL -> {
                        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//h4[text()='Main Markets']")));
                        return;
                    }
                }
                return;
            } catch (TimeoutException e) { }
        }
        throw new RuntimeException("Page`s not loading!");
    }

    public static WebElement getMarketByMarketName(ChromeDriver driver,
                                                   By byMarketName, Sports sport,
                                                   PartOfGame partOfGame) throws InterruptedException {
        waitLoadingOfPage(driver, sport);
        sleep(500L);
        clearPreviousBets(driver);
        return getMarketImpl(driver, byMarketName, sport, partOfGame);
    }

    public static WebElement getMarketImpl(ChromeDriver driver, By byName, Sports sport, PartOfGame partOfGame) throws InterruptedException {
        int scrollPosition = 0;
        int scroll = ((Number) ((JavascriptExecutor) driver).executeScript("return window.innerHeight")).intValue();
        int curScroll = scroll / 4;
        var wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        while (scrollPosition < 10000) {
            try {
                List<WebElement> visibleMarkets = wait.until(driver1 -> driver1.findElements(byName));
                for (var market : visibleMarkets) {
                    var parent = getParentByDeep(market, 5);
                    if (containsItem(parent, partOfGame, sport)) {
                        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, " + scroll / 2 + ")");
                        return parent;
                    }
                }
            } catch (NoSuchElementException e) { }
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, " + curScroll + ")");
            sleep(500L); // Wait for the page to finish scrolling
            scrollPosition += curScroll;
        }
        throw new RuntimeException("Market not found in sport: " + sport + " [188bet]");
    }

    public static void clearPreviousBets(ChromeDriver driver) throws InterruptedException {
        WebDriverWait wait_ = new WebDriverWait(driver, Duration.ofSeconds(10));
        var button = wait_.until(driver1 -> driver1.findElement(By.xpath("//h4[text()='Bet Slip']")));
        button = BetsSupport.getParentByDeep(button, 1);
        try {
            var countOfPreviousBets = button.findElement(By.xpath(".//h1[text()='1']"));
            countOfPreviousBets.click();
            sleep(1000L);
            wait_.until((ExpectedConditions.elementToBeClickable(By.cssSelector("[data-btn-trash-can='true']")))).click();
            wait_.until((ExpectedConditions.elementToBeClickable(By.cssSelector("[data-btn-remove-all='true']")))).click();
        } catch (NoSuchElementException e) {
        }
    }

    public static void closeBetWindow(ChromeDriver driver) throws InterruptedException {
        try {
            var wait = new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                driver_ -> driver_.findElement(By.xpath("//span[text()='@']")));
            var tmp = BetsSupport.getParentByDeep(wait, 1);
            sleep(200L);
            tmp.findElement(By.xpath(".//following::div[1]")).click();
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("Don`t close mini window! [188bet]");
        }
    }

    public static BigDecimal getBalance(ChromeDriver driver, Currency currency) {
        try {
            // Header
            try {
                var balanceButton = new WebDriverWait(driver, Duration.ofSeconds(60)).until(driver1
                    -> driver.findElement(By.className("print:text-black/80")).getText());
                balanceButton = balanceButton.substring(4, balanceButton.length() - 3);
                balanceButton = balanceButton.replace(',', '.');
                var balance = new BigDecimal(balanceButton);
                System.out.println("Balance from header THB: " + balance + " [188bet]");
                return balance.multiply(Context.currencyToRubCourse.get(currency));
            } catch (NoSuchElementException e) { }

            // BetWindow
            // TODO test
            WebElement balanceBlock = new WebDriverWait(driver, Duration.ofSeconds(200))
                .until(driver_ -> BetsSupport.getParentByDeep(
                    driver_.findElement(By.cssSelector("[placeholder='Enter Stake']")),
                    6))
                .findElement(By.xpath(".//following::div[0]"))
                .findElement(By.xpath(".//h4[contains(text(), 'THB']"));

            System.out.println("START TEXT balance = " + balanceBlock.getText() + "--- END OF TEXT");

            return null;
        } catch (NoSuchElementException e) {
            System.out.println("Balance in mini-window not found  [188bet]");
            throw new RuntimeException("Balance not found [188bet]");
        }
    }

    public static BigDecimal getCurrentCf(ChromeDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(60))
            .until(driver_ ->
                driver_.findElement(By.cssSelector("[placeholder='Enter Stake']")));

        WebElement tmpTitle = new WebDriverWait(driver, Duration.ofSeconds(30))
            .until(driver1 -> driver1.findElement(By.xpath("//span[text()='@']")));

        var title = BetsSupport.getParentByDeep(tmpTitle, 1).getText();
        return new BigDecimal(title.substring(title.indexOf("@") + 1));
    }

    public static BigDecimal getFinalCf(ChromeDriver driver) {
        WebElement tmpTitle = new WebDriverWait(driver, Duration.ofSeconds(30))
            .until(driver1 -> driver1.findElement(By.xpath("//span[text()='@']")));

        var title = BetsSupport.getParentByDeep(tmpTitle, 1).getText();
        return new BigDecimal(title.substring(title.indexOf("@") + 1));
    }

    public static void closeAfterSuccessfulBet(ChromeDriver driver) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(driver_ ->
                    driver_.findElement(By.cssSelector("[data-txt-bet-status='Confirmed']")));


            WebElement tmpButton = new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(driver_ -> BetsSupport.getParentByDeep(
                    driver_.findElement(By.xpath("//span[text()='@']")),
                    7));

            tmpButton.findElement(By.xpath(".//h4[text()='OK']")).click();

        } catch (TimeoutException | NoSuchElementException e) {
            System.out.println("Not Close mini-window after success betting!  [188bet]");
        }
    }
}

