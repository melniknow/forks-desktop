package com.melniknow.fd.betting.utils._188bet;

import com.melniknow.fd.betting.utils.BetsSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

public class ClickSportsTotals {
    static public void click(ChromeDriver driver, Parser.BetInfo info) {
        /*
        check:
          marketName:
            basketball: TOTAL POINTS: OVER \\/ UNDER
            soccer: GOALS: OVER \\/ UNDER
            tennis: TOTAL GAMES: OVER \\/ UNDER
         */

        // TODO ignore 1st and 2st half's
        var market = new WebDriverWait(driver, Duration.ofSeconds(200))
            .until(driver_ -> driver_.findElement(By.xpath("//h4[text()='" + info.BK_market_meta().get("marketName").getAsString() + "']")));

        market = BetsSupport.getParentByDeep(market, 5);

        var buttons = market.findElements(By.xpath(
                ".//div[text()='" + info.BK_market_meta().get("selectionName").getAsString() + "']"))
            .stream()
            .map(e -> e.findElement(By.xpath("./..")))
            .toList();

        Objects.requireNonNull(buttons.stream().filter(n -> BetsSupport.getTotalsByStr(n.getText()).equals(info.BK_market_meta().get("line").getAsString())).findAny().orElse(null)).click();
    }
}
