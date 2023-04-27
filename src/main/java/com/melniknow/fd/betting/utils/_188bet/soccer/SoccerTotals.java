package com.melniknow.fd.betting.utils._188bet.soccer;

import com.google.gson.JsonParser;
import com.melniknow.fd.betting.utils.Utils;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SoccerTotals {
    // "{\"marketName\":\"Goals: Over \\/ Under\",
    // \"selectionName\":\"Over\",
    // \"marketId\":117056061,
    // \"outcomeId\":9616351025,
    // \"original_cf\":\"0.83\",
    // \"line\":\"2.5\"}"
    static public void click(ChromeDriver driver, Parser.BetInfo info) {
        String data = """
                {\"marketName\":\"Goals: Over \\/ Under\",
                \"selectionName\":\"Over\",
                \"marketId\":117056061,
                \"outcomeId\":9616351025,
                \"original_cf\":\"0.83\",
                \"line\":\"2.5\"}""";

        var parsed = JsonParser.parseString(data).getAsJsonObject();

        var market = new WebDriverWait(driver, Duration.ofSeconds(200))
            .until(driver_ -> driver_.findElement(By.xpath("//h4[text()='" + parsed.get("marketName").getAsString() + "']")));
        market = Utils.getParentByDeep(market, 5);

        var buttons = market.findElements(By.xpath(".//div[text()='Over']"))
            .stream()
            .map(e -> e.findElement(By.xpath("./..")))
            .toList();

        Objects.requireNonNull(buttons.stream().filter(n -> Utils.getTotalsByStr(n.getText()).equals(parsed.get("line").getAsString())).findAny().orElse(null)).click();
    }
}
