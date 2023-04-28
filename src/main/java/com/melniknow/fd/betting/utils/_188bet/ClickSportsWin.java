package com.melniknow.fd.betting.utils._188bet;

import com.melniknow.fd.betting.utils.BetsSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ClickSportsWin {
    static public void click(ChromeDriver driver, Parser.BetInfo info) {
        /*
        check json:
          marketName:
            tennis: Winner
            soccer: 1 X 2
            basketball: WINNER
         */

        // TODO Scroll problem
        var market = new WebDriverWait(driver, Duration.ofSeconds(200))
            .until(driver_ -> driver_.findElement(By.xpath("//h4[text()='" + info.BK_market_meta().get("marketName").getAsString() + "']")));

        market = BetsSupport.getParentByDeep(market, 5);
        var selectionName = "";
        if (info.BK_bet().startsWith("WIN__P1")) {
            selectionName = BetsSupport.getTeamFirstNameByTitle(info.BK_game());
        } else if (info.BK_bet().startsWith("WIN__P2")) {
            selectionName = BetsSupport.getTeamSecondNameByTitle(info.BK_game());
        } else {
            // TODO support others
            throw new RuntimeException("Not supported Handicap");
        }

        market.findElement(By.xpath(
                ".//div[text()='" + selectionName + "']")).click();
    }
}
