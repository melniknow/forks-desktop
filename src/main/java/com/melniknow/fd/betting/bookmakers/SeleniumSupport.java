package com.melniknow.fd.betting.bookmakers;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SeleniumSupport {
    public static WebElement getParentByDeep(WebElement element, int deep) {
        for (var i = 0; i < deep; i++) element = element.findElement(By.xpath("./.."));
        return element;
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

    public static By buildGlobalSpanByText(String text) {
        return By.xpath("//span[text()='" + text + "']");
    }

    public static By buildGlobalDivByText(String text) {
        return By.xpath("//div[text()='" + text + "']");
    }

    public static By buildGlobalH4ByText(String text) {
        return By.xpath("//h4[text()='" + text + "']");
    }

    public static WebElement findElementWithClicking(ChromeDriver driver, WebElement element, By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        WebElement res;
        try {
            res = wait.until(driver1 -> element.findElement(by));
            return res;
        } catch (TimeoutException e) {
            element.click();
            try {
                res = wait.until(driver1 -> element.findElement(by));
                return res;
            } catch (TimeoutException e1) {
                throw new RuntimeException("Button not found [pinnacle] with by: " + by);
            }
        }
    }

    public static List<WebElement> findElementsWithClicking(ChromeDriver driver, WebElement element, By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        List<WebElement> res;
        try {
            res = wait.until(driver1 -> element.findElements(by));
            return res;
        } catch (TimeoutException e) {
            element.click();
            try {
                res = wait.until(driver1 -> element.findElements(by));
                return res;
            } catch (TimeoutException e1) {
                throw new RuntimeException("Button not found [pinnacle] with by: " + by);
            }
        }
    }
}
