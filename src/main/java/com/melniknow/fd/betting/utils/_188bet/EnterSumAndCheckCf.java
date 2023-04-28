package com.melniknow.fd.betting.utils._188bet;

import com.melniknow.fd.betting.utils.BetsSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;

public class EnterSumAndCheckCf {
    public static void enterSumAndCheckCf(ChromeDriver driver, Parser.BetInfo info) {
        // find 'Please Log In' -> parent up 3 -> find '@'
        var tmpButton = new WebDriverWait(driver, Duration.ofSeconds(200))
            .until(driver_ -> BetsSupport.getParentByDeep(
                    driver_.findElement(By.xpath("//h4[text()='Please Log In']")),
                    3)
                .findElement(By.xpath(".//span[text()='@']")));

        var title = BetsSupport.getParentByDeep(tmpButton, 1).getText();
        var currentCf = new BigDecimal(title.substring(title.indexOf("@") + 1));
        System.out.println(currentCf);

        var finalButton = driver.findElement(By.xpath("//h4[text()='Please Log In']")); // click on here to place bet

        var enterSnake = BetsSupport.getParentByDeep(finalButton, 3).findElement(By.cssSelector("[placeholder='Enter Stake']"));

        enterSnake.sendKeys("12");
    }
}
