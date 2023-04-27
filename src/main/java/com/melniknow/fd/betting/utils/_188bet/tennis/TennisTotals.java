package com.melniknow.fd.betting.utils._188bet.tennis;

import com.google.gson.JsonParser;
import com.melniknow.fd.betting.utils.Utils;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

public class TennisTotals {

    private static final String handicapMarketName = "Game Handicap"; // OK | but there is 2nd Set
    private static final String handicapSelectionName = "fromUri";

    private static final String winMarketName = "Winner"; // Ok | but there is 2nd Set
    private static final String winSelectionName = "fromUri";

    private static final String totalsMarketName = "Total Games: Over / Under"; // Ok | but there is 2nd Set
    private static final String totalsSelectionName = "Over"; // Ok

    static public void click(ChromeDriver driver, Parser.BetInfo info) {

//        var market = new WebDriverWait(driver, Duration.ofSeconds(200))
//            .until(driver_ -> driver_.findElement(By.xpath("//h4[text()='" + info.BK_market_meta().get("marketName").getAsString() + "']")));
//
//        market = Utils.getParentByDeep(market, 5);
//
//        var buttons = market.findElements(By.xpath(".//div[text()='Over']"))
//            .stream()
//            .map(e -> e.findElement(By.xpath("./..")))
//            .toList();
//
//        Objects.requireNonNull(buttons.stream().filter(n -> Utils.getTotalsByStr(n.getText()).equals(info.BK_market_meta().get("line").getAsString())).findAny().orElse(null)).click();
    }
}
