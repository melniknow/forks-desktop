package com.melniknow.fd.betting.bookmakers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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
}
