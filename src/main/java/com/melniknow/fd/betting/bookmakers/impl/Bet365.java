package com.melniknow.fd.betting.bookmakers.impl;

import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.utils.BetUtils.Proxy;
import com.melniknow.fd.utils.MathUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class Bet365 implements IBookmaker {
    @Override
    public void openLink(ChromeDriver driver, Proxy proxy, MathUtils.CalculatedFork calculated) {
        driver.get("https://sports.188sbk.com/en-gb/sports/all-market/6989377/Molotpro-vs-Eaglespro");

        List<WebElement> button = driver.findElements(By.xpath("//div[contains(text(),'r')]"));

        System.out.println("\n\n\nSIZE= " + button.size());
    }

    @Override
    public void clickOnBetType() {

    }

    @Override
    public void enterSumAndCheckCf() {

    }
    @Override
    public void placeBet() {

    }
}
