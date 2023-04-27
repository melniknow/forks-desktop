package com.melniknow.fd.betting.utils._188bet.soccer;

import com.melniknow.fd.core.Parser;
import org.openqa.selenium.chrome.ChromeDriver;

public class SoccerTotals {
    // "{\"marketName\":\"Goals: Over \\/ Under\",
    // \"selectionName\":\"Over\",
    // \"marketId\":117056061,
    // \"outcomeId\":9616351025,
    // \"original_cf\":\"0.83\",
    // \"line\":\"2.5\"}"
    static public void click(ChromeDriver driver, Parser.BetInfo info) {
//        String data = """
//                {\"marketName\":\"Goals: Over \\/ Under\",
//                \"selectionName\":\"Over\",
//                \"marketId\":117056061,
//                \"outcomeId\":9616351025,
//                \"original_cf\":\"0.83\",
//                \"line\":\"2.5\"}""";
//
//        var parsed = JsonParser.parseString(data).getAsJsonObject();
//
//        driver.get("https://sports.188sbk.com/en-gb/sports/all-market/6952969/Atletico-Madrid-vs-Mallorca");
//
//        TimeUnit.SECONDS.sleep(10);
//        String gols = "4";
//
//        var market = driver.findElement(By.xpath("//h4[text()='" + parsed.get("marketName").getAsString() + "']"));
//        market = market.findElement(By.xpath("./.."));
//        market = market.findElement(By.xpath("./.."));
//        market = market.findElement(By.xpath("./.."));
//        market = market.findElement(By.xpath("./.."));
//        market = market.findElement(By.xpath("./.."));
//
//        List<WebElement> overs = market.findElements(By.xpath(".//div[text()='Over']"));
//        var parents = overs.stream().map(e -> e.findElement(By.xpath("./.."))).toList();
//
//
//        for (var parent : parents) {
//            System.out.println("TEXT=" + parent.getText());
//            var number = getTotalsByStr(parent.getText());
//            System.out.println(number);
//            if (number.equals(gols)) {
//                System.out.println("FIND!!!");
//                parent.click();
//            }
//        }
    }
}
