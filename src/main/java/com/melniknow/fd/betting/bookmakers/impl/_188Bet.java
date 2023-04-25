package com.melniknow.fd.betting.bookmakers.impl;

import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.domain.BetType;
import com.melniknow.fd.utils.MathUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import com.melniknow.fd.utils.BetUtils.Proxy;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class _188Bet implements IBookmaker {

    @Override
    public void openLink(ChromeDriver driver, Proxy proxy, MathUtils.CalculatedFork calculated) {
        driver.get(calculated.fork().link1());
    }

    @Override
    public void clickOnBetType() {
        //        if (calculated.fork().betType().equals(BetType.WIN)) {
//            var winTable = driver.findElement(By.className("data-btn=\"Winner\""));
//
//            var winnerName = calculated.fork().bet1();
//            if (winnerName.equals("WIN__P1")) {
//
//            } else if (winnerName.equals("WIN__P2")) {
//
//            } else {
//                throw new RuntimeException("not support betType"); // TODO
//            }
//        }
//
//        if (calculated.fork().betType().equals(BetType.TOTALS)) {
//            var totals = getTotalsByStr(calculated.fork().bet1());
//            if (calculated.fork().bet1().contains("TOTALS__OVER")) {
//                var curtTotal = driver.findElement(By.className("sc-ftTHYK eRWnrS"));
//                var value = BigDecimal.valueOf(Double.parseDouble(curtTotal.getText()));
//                if (value.equals(totals)) {
//                    curtTotal.click();
//                }
//            } else if (calculated.fork().bet2().contains("TOTALS__UNDER")) {
//
//            } else {
//
//            }
//        }
//
//        if (calculated.fork().betType().equals(BetType.HANDICAP)) {
//
//        }
    }


    @Override
    public void enterSumAndCheckCf() {

    }
    @Override
    public void placeBet() {

    }
    private static BigDecimal getTotalsByStr(String str) {
        Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return BigDecimal.valueOf(Double.parseDouble(m.group(1)));
        }
        throw new RuntimeException("Not found a totals");
    }
}
