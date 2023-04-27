package com.melniknow.fd.betting.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Utils {
    public static String getTotalsByStr(String str) {
        return str.split("\n")[1];
    }
    public static WebElement getParentByDeep(WebElement element, int deep) {
        for (var i = 0; i < deep; i++) element = element.findElement(By.xpath("./.."));
        return element;
    }
}
