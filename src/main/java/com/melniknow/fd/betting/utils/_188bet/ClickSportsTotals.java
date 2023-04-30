package com.melniknow.fd.betting.utils._188bet;

import com.melniknow.fd.betting.utils.BetsSupport;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Sports;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Objects;

public class ClickSportsTotals {
    static public void click(ChromeDriver driver, Parser.BetInfo info, Sports sport) {
        var market = BetsSupport.getMarketByMarketName(driver,
            By.xpath("//h4[text()='" + info.BK_market_meta().get("marketName").getAsString() + "']"), sport);

        var buttons = BetsSupport.findElementsWithClicking(market.getCorrectWebElement(), By.xpath(
                ".//div[text()='" + info.BK_market_meta().get("selectionName").getAsString() + "']")).stream()
            .map(e -> e.findElement(By.xpath("./..")))
            .toList();

        Objects.requireNonNull(buttons.stream().filter(n ->
            BetsSupport.getTotalsByStr(n.getText()).equals(
                info.BK_market_meta().get("line").getAsString())).findAny().orElse(null)).click();
    }
}
