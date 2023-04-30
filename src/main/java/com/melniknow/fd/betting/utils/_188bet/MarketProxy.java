package com.melniknow.fd.betting.utils._188bet;

import com.melniknow.fd.betting.utils.BetsSupport;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MarketProxy {
    private final WebDriver driver;
    private WebElement webElement;
    private final int y;
    private final By by;

    public MarketProxy(WebDriver driver, WebElement element, int y, By by) {
        this.driver = driver;
        this.webElement = element;
        this.y = y;
        this.by = by;
    }

    public WebElement getCorrectWebElement() {
        // scroll to top of page -> scroll to element -> find a new element -> getParent
        ((JavascriptExecutor) driver)
            .executeScript("window.scrollTo(0, -document.body.scrollHeight)");
        BetsSupport.sleep(200L);
        int s = this.y - 100;
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0," + s + ")");
        BetsSupport.sleep(300L);
        return BetsSupport.getParentByDeep(driver.findElement(by), 5);
    }

    public void setWebElement(WebElement elem) {
        webElement = elem;
    }

    public WebElement getRawWebElement() {
        return webElement;
    }
}
