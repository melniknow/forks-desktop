package com.melniknow.fd.betting.bookmakers.impl._188bet;

import com.melniknow.fd.betting.bookmakers.impl.BetsSupport;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class MarketProxy {
    private final ChromeDriver driver;
    private WebElement webElement;
    private final int y;
    private final By by;

    public MarketProxy(ChromeDriver driver, WebElement element, int y, By by) {
        this.driver = driver;
        this.webElement = element;
        this.y = y;
        this.by = by;
    }

    public WebElement getCorrectWebElement() throws InterruptedException {
        // scroll to top of page -> scroll to element -> find a new element -> getParent
        ((JavascriptExecutor) driver)
            .executeScript("window.scrollTo(0, -document.body.scrollHeight)");
        BetsSupport.sleep(200L);
        int s = this.y - 50;
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
