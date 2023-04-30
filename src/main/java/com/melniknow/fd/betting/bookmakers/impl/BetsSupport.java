package com.melniknow.fd.betting.bookmakers.impl;

import com.melniknow.fd.betting.bookmakers.impl._188bet.MarketProxy;
import com.melniknow.fd.domain.Sports;
import org.openqa.selenium.*;
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

    public static void sleep(Long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e1) {
        }
    }

    public static WebElement findElementWithClicking(WebElement element, By by) {
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

    public static List<WebElement> findElementsWithClicking(WebElement element, By by) {
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

    public static boolean checkBasketQuarters(WebElement market) {
        try {
            market.findElement(buildSpanByText(firstQuarter));
        } catch (NoSuchElementException e) {
            try {
                market.findElement(buildSpanByText(secondQuarter));
            } catch (NoSuchElementException e1) {
                try {
                    market.findElement(buildSpanByText(thirdQuarter));
                } catch (NoSuchElementException e2) {
                    try {
                        market.findElement(buildSpanByText(fourthQuarter));
                    } catch (NoSuchElementException e3) {
                        try {
                            market.findElement(buildSpanByText(firstHalf));
                        } catch (NoSuchElementException e4) {
                            try {
                                market.findElement(buildSpanByText(secondHalf));
                            } catch (NoSuchElementException e5) {
                                // fucking basket =(
                                // Сюда дойдёт только глобальный marketName, который везде кинул исключение
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isPureMarket(WebElement elem, Sports sport) {
        if (sport.equals(Sports.BASKETBALL)) {
            return checkBasketQuarters(elem);
        }
        try {
            switch (sport) {
                case TENNIS -> elem.findElement(buildSpanByText(firstSet));
                case SOCCER -> elem.findElement(buildSpanByText(firstHalf));
            }
        } catch (NoSuchElementException e) {
            try {
                switch (sport) {
                    case TENNIS -> elem.findElement(buildSpanByText(secondSet));
                    case SOCCER -> elem.findElement(buildSpanByText(secondHalf));
                }
            } catch (NoSuchElementException e1) {
                return true;
            }
        }
        return false;
    }

    public static void waitLoadingOfPage(WebDriver driver, Sports sport) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        // wait the main button
        switch (sport) {
            case BASKETBALL, TENNIS, SOCCER -> wait.until(ExpectedConditions.elementToBeClickable(By.id("tabMT")));
        }
    }

    public static MarketProxy getMarketByMarketName(WebDriver driver, By byMarketName, Sports sport) {
        waitLoadingOfPage(driver, sport);
        return getMarketImpl(driver, byMarketName, sport);
    }


    public static MarketProxy getMarketImpl(WebDriver driver, By byName, Sports sport) {
        int scrollPosition = 0;
        int scroll = ((Number) ((JavascriptExecutor) driver).executeScript("return window.innerHeight")).intValue();
        while (scrollPosition < 7000) {
            try {
                List<WebElement> visibleMarkets = driver.findElements(byName);
                for (var market : visibleMarkets) {
                    if (isPureMarket(market, sport)) {
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

