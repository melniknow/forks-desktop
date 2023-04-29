package com.melniknow.fd.betting.utils;

import com.melniknow.fd.domain.Sports;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class BetsSupport {
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

    private static final String firstQuarter = "1st Quarter";
    private static final String secondQuarter = "2nd Quarter";
    private static final String thirdQuarter = "3rd Quarter";
    private static final String fourthQuarter = "4th Quarter";

    private static final String firstHalf = "1st Half";
    private static final String firstSet = "1st Set";

    private static final String secondHalf = "2nd Half";
    private static final String secondSet = "2nd Set";

    public static String buildSpanByText(String text) {
        return ".//span[text()='" + text + "']";
    }

    public static String buildDivByText(String text) {
        return ".//div[text()='" + text + "']";
    }

    public static WebElement findElementWithClicking(WebElement element, By by) {
        WebElement res;
        try {
            res = element.findElement(by);
            return res;
        } catch (NoSuchElementException e) {
            element.click();
            try { Thread.sleep(300);} catch (InterruptedException e1) {} // TODO
            return element.findElement(by);
        }
    }

    public static WebElement filterBasketQuarters(List<WebElement> markets) {
        WebElement result = null;
        for (var market : markets) {
            try {
                market.findElement(By.xpath(buildSpanByText(firstQuarter)));
            } catch (NoSuchElementException e) {
                try {
                    market.findElement(By.xpath(buildSpanByText(secondQuarter)));
                } catch (NoSuchElementException e1) {
                    try {
                        market.findElement(By.xpath(buildSpanByText(thirdQuarter)));
                    } catch (NoSuchElementException e2) {
                        try {
                            market.findElement(By.xpath(buildSpanByText(fourthQuarter)));
                        } catch (NoSuchElementException e3) {
                            try {
                                market.findElement(By.xpath(buildSpanByText(firstHalf)));
                            } catch (NoSuchElementException e4) {
                                try {
                                    market.findElement(By.xpath(buildSpanByText(secondHalf)));
                                } catch (NoSuchElementException e5) {
                                    // fucking basket =(
                                    // Сюда дойдёт только глобальный marketName, который везде кинул исключение
                                    result = market;
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public static WebElement marketsFilter(ChromeDriver driver, List<WebElement> markets, Sports sport) {
        if (sport.equals(Sports.BASKETBALL)) {
            return filterBasketQuarters(markets);
        }

        WebElement result = null;
        for (var market : markets) {
            try {
                switch (sport) {
                    case TENNIS -> market.findElement(By.xpath(buildSpanByText(firstSet)));
                    case SOCCER -> market.findElement(By.xpath(buildSpanByText(firstHalf)));
                }
            } catch (NoSuchElementException e) {
                try {
                    switch (sport) {
                        case TENNIS -> market.findElement(By.xpath(buildSpanByText(secondSet)));
                        case SOCCER -> market.findElement(By.xpath(buildSpanByText(secondHalf)));
                    }
                } catch (NoSuchElementException e1) {
                    result = market;
                }
            }
        }
        return result;
    }

    public static void waitLoadingOfPage(ChromeDriver driver, String searchMarketName, Sports sport) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        // wait the main button
        switch (sport) {
            case TENNIS, BASKETBALL ->
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-btn='All Markets']")));
            case SOCCER -> wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-btn='Popular']")));
        }



        int scroll = ((Number) ((JavascriptExecutor) driver).executeScript("return window.innerHeight")).intValue() - 50;
        var totalHeight = 0;
        while (true) {
            try {
                if (totalHeight > 6000) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                } // TODO
                driver.findElement(By.xpath(searchMarketName));
                break;
            } catch (NoSuchElementException e) {
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0," + scroll + ")");
                totalHeight += scroll;
            }
        }
    }

    public static WebElement getMarketByMarketName(ChromeDriver driver, String marketName, Sports sport) {
        waitLoadingOfPage(driver, marketName, sport);

        var markets = new WebDriverWait(driver, Duration.ofSeconds(200))
            .until(driver_ -> driver_.findElements(By.xpath(marketName)));

        markets = markets.stream().map(m -> BetsSupport.getParentByDeep(m, 5)).toList();

        var res = BetsSupport.marketsFilter(driver, markets, sport); // return only 'pure' market

        if (res == null) {
            throw new RuntimeException("Market not found in sport: " + sport);
        }

        return res;
    }
}
