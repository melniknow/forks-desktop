package com.melniknow.fd.betting.bookmakers.impl._188bet;

import com.melniknow.fd.betting.bookmakers.impl.BetsSupport;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Sports;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Objects;

public class ClickSportHandicap {
    static public void click(ChromeDriver driver, Parser.BetInfo info, Sports sport) throws InterruptedException {
        var selectionName = "";
        if (info.BK_bet().startsWith("HANDICAP__P1")) {
            selectionName = BetsSupport.getTeamFirstNameByTitle(info.BK_game());
        } else if (info.BK_bet().startsWith("HANDICAP__P2")) {
            selectionName = BetsSupport.getTeamSecondNameByTitle(info.BK_game());
        } else {
            throw new RuntimeException("Not supported Handicap");
        }

        var market = BetsSupport.getMarketByMarketName(driver,
            BetsSupport.buildH4ByText(info.BK_market_meta().get("marketName").getAsString()), sport);

        var buttons = BetsSupport.findElementsWithClicking(market.getCorrectWebElement(),
                BetsSupport.buildDivByText(selectionName))
            .stream()
            .map(e -> e.findElement(By.xpath("./..")))
            .toList();

        // Build correct line
        var line = info.BK_market_meta().get("line").getAsString();
        if (!line.startsWith("-") && !line.startsWith("+") && !line.equals("0")) {
            line = "+" + line;
        }
        String finalLine = line;
        Objects.requireNonNull(buttons.stream().filter(b -> BetsSupport.getTotalsByStr(b.getText()).equals(finalLine)).findAny().orElse(null)).click();
    }
}
