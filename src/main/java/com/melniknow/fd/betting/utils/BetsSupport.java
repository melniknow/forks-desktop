package com.melniknow.fd.betting.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class BetsSupport {
    public static String getTotalsByStr(String str) {
        return str.split("\n")[1];
    }
    public static WebElement getParentByDeep(WebElement element, int deep) {
        for (var i = 0; i < deep; i++) element = element.findElement(By.xpath("./.."));
        return element;
    }

    public static String getTeamFirstNameByTitle(String title) {
        return title.substring(0, title.indexOf("vs") - 1);
    }

    public static String getTeamSecondNameByTitle(String title) {
        return title.substring(title.indexOf("vs") + 3);
    }
}
