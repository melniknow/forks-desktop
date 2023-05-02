package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.betting.bookmakers.BetsSupport;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Sports;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import java.math.BigDecimal;
import java.util.Objects;

public class ClickSportsTotals {
    static public BigDecimal clickAndReturnBalanceAsRub(ChromeDriver driver, Parser.BetInfo info, Sports sport) throws InterruptedException {
        var market = BetsSupport.getMarketByMarketName(driver,
            BetsSupport.buildH4ByText(info.BK_market_meta().get("marketName").getAsString()),
            sport, PartOfGame.fromString(info.BK_bet()));

        var buttons = BetsSupport.findElementsWithClicking(market.getCorrectWebElement(),
                BetsSupport.buildDivByText(info.BK_market_meta().get("selectionName").getAsString())).stream()
            .map(e -> e.findElement(By.xpath("./..")))
            .toList();

        var line = info.BK_market_meta().get("line").getAsString();

        Objects.requireNonNull(buttons.stream().filter(n ->
            BetsSupport.getTotalsByStr(n.getText()).equals(line)).findAny().orElse(null)).click();

        return BetsSupport.getBalance(driver);
    }
}
