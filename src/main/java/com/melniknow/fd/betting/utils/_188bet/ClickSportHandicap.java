package com.melniknow.fd.betting.utils._188bet;

import com.melniknow.fd.betting.utils.BetsSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Objects;

public class ClickSportHandicap {
    static public void click(ChromeDriver driver, Parser.BetInfo info) {

        var market = BetsSupport.getMarketByMarketName(driver,
            "//h4[text()='" + info.BK_market_meta().get("marketName").getAsString() + "']");

        var line = info.BK_market_meta().get("line").getAsString();
        var selectionName = "";
        if (info.BK_bet().startsWith("HANDICAP__P1")) {
            selectionName = BetsSupport.getTeamFirstNameByTitle(info.BK_game());
        } else if (info.BK_bet().startsWith("HANDICAP__P2")) {
            selectionName = BetsSupport.getTeamSecondNameByTitle(info.BK_game());
        } else {
            // TODO support others
            throw new RuntimeException("Not supported Handicap");
        }

        var buttons = market.findElements(By.xpath(
                ".//div[text()='" + selectionName + "']"))
            .stream()
            .map(e -> e.findElement(By.xpath("./..")))
            .toList();

        Objects.requireNonNull(buttons.stream().filter(n -> BetsSupport.getTotalsByStr(n.getText()).equals(line)).findAny().orElse(null)).click();
    }
}
