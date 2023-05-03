package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Sports;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import java.math.BigDecimal;
import java.util.Objects;

public class ClickSportHandicap {
    static public void clickAndReturnBalanceAsRub(ChromeDriver driver, Parser.BetInfo info, Sports sport) throws InterruptedException {
        var selectionName = "";
        if (info.BK_bet().contains("HANDICAP__P1")) {
            selectionName = BetsSupport.getTeamFirstNameByTitle(info.BK_game());
        } else if (info.BK_bet().contains("HANDICAP__P2")) {
            selectionName = BetsSupport.getTeamSecondNameByTitle(info.BK_game());
        } else {
            throw new RuntimeException("Not supported Handicap [188Bet]");
        }

        var partOfGame = PartOfGame.fromString(info.BK_bet(), sport);

        var marketName = info.BK_market_meta().get("marketName").getAsString();

        marketName = marketName.split(" - ")[0];

        var market = BetsSupport.getMarketByMarketName(driver,
            BetsSupport.buildLocalH4ByText(marketName),
            sport, partOfGame);

        var buttons = BetsSupport.findElementsWithClicking(market,
                BetsSupport.buildLocalDivByText(selectionName))
            .stream()
            .map(e -> e.findElement(By.xpath("./..")))
            .toList();

        var line = info.BK_market_meta().get("line").getAsString();
        Objects.requireNonNull(buttons.stream().filter(b -> BetsSupport.getTotalsByStr(b.getText()).equals(BetsSupport.buildLine(line))).findAny().orElse(null)).click();
    }
}
