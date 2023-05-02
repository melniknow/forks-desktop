package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.betting.bookmakers.BetsSupport;
import com.melniknow.fd.domain.Sports;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MarketProxy {
    private final WebDriver driver;
    private WebElement webElement;
    private final int y;
    private final By by;
    private Sports sport;

    public MarketProxy(WebDriver driver, WebElement element, int y, By by, Sports sport) {
        this.driver = driver;
        this.webElement = element;
        this.y = y;
        this.by = by;
        this.sport = sport;
    }

    public WebElement getCorrectWebElement() throws InterruptedException {
        // scroll to top of page -> scroll to element -> find a new element -> getParent
        ((JavascriptExecutor) driver)
            .executeScript("window.scrollTo(0, -document.body.scrollHeight)");
        BetsSupport.sleep(200L);
        int s = this.y - 50;
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0," + s + ")");
        BetsSupport.sleep(300L);

        for (var market : driver.findElements(by)) {
            var parent = BetsSupport.getParentByDeep(market, 5);
            if (BetsSupport.isPureMarket(parent, sport)) {
                System.out.println("YES IN PROXY! Y = " + parent.getLocation().y);
                return parent;
            }
            System.out.println("SKIIP IN PROXY! Y = " + parent.getLocation().y);
        }
        return null;
    }

    public void setWebElement(WebElement elem) {
        webElement = elem;
    }

    public WebElement getRawWebElement() {
        return webElement;
    }
}
