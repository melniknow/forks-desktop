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

    private static final String firstHalf = "1st Half";
    private static final String firstSet = "1st Set";

    private static final String secondHalf = "2nd Half";
    private static final String secondSet = "2nd Set";

    public static WebElement marketsFilter(List<WebElement> markets, Sports sport) {
        WebElement result = null;
        for (var market : markets) {
            try {
                switch (sport) {
                    case TENNIS -> market.findElement(By.xpath(".//span[text()='" + firstSet + "']"));
                    case SOCCER -> market.findElement(By.xpath(".//span[text()='" + firstHalf + "']"));
                }
            } catch (NoSuchElementException e) {
                try {
                    switch (sport) {
                        case TENNIS -> market.findElement(By.xpath(".//span[text()='" + secondSet + "']"));
                        case SOCCER -> market.findElement(By.xpath(".//span[text()='" + secondHalf + "']"));
                    }
                } catch (NoSuchElementException e1) {
                    // Сюда дойдёт только "чистый" marketName, который везде кинул исключение
                    result = market;
                }
            }
        }
        return result;
    }

    public static void waitLoadingOfPage(ChromeDriver driver, String searchMarketName, Sports sport) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

        // Wait loading the page
        switch (sport) {
            case TENNIS, BASKETBALL -> wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-btn='All Markets']")));
            case SOCCER -> wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-btn='Popular']")));
        }

        int scroll = ((Number) ((JavascriptExecutor) driver).executeScript("return window.innerHeight")).intValue() - 50;

        var totalHeight = 0;
        while (true) {
            try {
                if (totalHeight > 6000) {
                    break;
                }
                driver.findElement(By.xpath(searchMarketName));
                JavascriptExecutor jse = (JavascriptExecutor)driver;
                jse.executeScript("window.scrollBy(0," + scroll +")");
                break;
            } catch (NoSuchElementException e) {
                JavascriptExecutor jse = (JavascriptExecutor)driver;
                jse.executeScript("window.scrollBy(0," + scroll +")");
                totalHeight += scroll;
            }
        }
        System.out.println(totalHeight);
    }

    public static WebElement getMarketByMarketName(ChromeDriver driver, String marketName, Sports sport) {
        BetsSupport.waitLoadingOfPage(driver, marketName, sport);

        var markets = new WebDriverWait(driver, Duration.ofSeconds(200))
            .until(driver_ -> driver_.findElements(By.xpath(marketName)));

        System.out.println(markets.size());

        markets = markets.stream().map(m -> BetsSupport.getParentByDeep(m, 5)).toList();

        return BetsSupport.marketsFilter(markets, sport); // delete 1st and 2nd half
    }
}
