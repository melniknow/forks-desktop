package com.melniknow.fd.betting.bookmakers.impl;

import com.melniknow.fd.betting.bookmakers.impl._188bet.MarketProxy;
import com.melniknow.fd.domain.Sports;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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

    public static By buildSpanByText(String text) {
        return By.xpath(".//span[text()='" + text + "']");
    }

    public static By buildDivByText(String text) {
        return By.xpath(".//div[text()='" + text + "']");
    }

    public static By buildH4ByText(String text) {
        return By.xpath(".//h4[text()='" + text + "']");
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
            market.findElement(buildSpanByText(item));
            return false;
        } catch (NoSuchElementException e) {
            return true;
        }
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
            case SOCCER -> {
                return notContainsItem(elem, firstHalf) &&
                    notContainsItem(elem, secondHalf);
            }
        }
        return false;
    }

    public static void waitLoadingOfPage(ChromeDriver driver, Sports sport) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        // wait the main button
        switch (sport) {
            case BASKETBALL, TENNIS, SOCCER -> {
                try {
                    // есть страницы, где этот элемент отсутствует, тогда подождём другой
                    wait.until(ExpectedConditions.elementToBeClickable(By.id("tabMT")));
                } catch (TimeoutException e) {
                    System.out.println("TamBt expired!");
                    switch (sport) {
                        case TENNIS, BASKETBALL ->
                            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//h4[text()='All Markets']")));
                        case SOCCER ->
                            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//h4[text()='Main Markets']")));
                    }
                }

            }
        }
    }

    public static MarketProxy getMarketByMarketName(ChromeDriver driver, By byMarketName, Sports sport) throws InterruptedException {
        waitLoadingOfPage(driver, sport);
        return getMarketImpl(driver, byMarketName, sport);
    }


    public static MarketProxy getMarketImpl(ChromeDriver driver, By byName, Sports sport) throws InterruptedException {
        int scrollPosition = 0;
        int scroll = ((Number) ((JavascriptExecutor) driver).executeScript("return window.innerHeight")).intValue() - 100;
        while (scrollPosition < 7000) {
            try {
                List<WebElement> visibleMarkets = driver.findElements(byName);
                for (var market : visibleMarkets) {
                    if (isPureMarket(market, sport)) {
                        System.out.println("YES!");
                        var res = getParentByDeep(market, 5);
                        return new MarketProxy(driver, res, res.getLocation().y, byName);
                    }
                }
            } catch (NoSuchElementException e) {

            }
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, " + scroll + ")");
            sleep(300L); // Wait for the page to finish scrolling
            scrollPosition += scroll;
        }
        throw new RuntimeException("Market not found in sport: " + sport);
    }
}


