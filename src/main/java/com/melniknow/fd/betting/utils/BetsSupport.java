package com.melniknow.fd.betting.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
    private static final String secondHalf = "2nd Half";

    public static WebElement marketsFilter(List<WebElement> markets) {
        WebElement result = null;
        for (var market : markets) {
            try {
                market.findElement(By.xpath(".//span[text()='" + firstHalf + "']"));
            } catch (NoSuchElementException e) {
                try {
                    market.findElement(By.xpath(".//span[text()='" + secondHalf + "']"));
                } catch (NoSuchElementException e1) {
                    result = market;
                }
            }
        }
        return result;
    }

    public static void waitLoadingOfPage(ChromeDriver driver, String searchMarketName) {
        new WebDriverWait(driver, Duration.ofSeconds(200))
            .until(driver_ -> driver_.findElement(By.xpath(searchMarketName)));
    }

    public static WebElement getMarketByMarketName(ChromeDriver driver, String marketName) {
        // TODO Scroll problem
        BetsSupport.waitLoadingOfPage(driver, marketName);

        var markets = new WebDriverWait(driver, Duration.ofSeconds(200))
            .until(driver_ -> driver_.findElements(By.xpath(marketName)));

        markets = markets.stream().map(m -> BetsSupport.getParentByDeep(m, 5)).toList();

        return BetsSupport.marketsFilter(markets); // delete 1st and 2nd half
    }
}
