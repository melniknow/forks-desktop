package com.melniknow.fd.betting.bookmakers.pinnacle;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.betting.bookmakers._188bet.BetsSupport;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sport;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public class Pinnacle implements IBookmaker {
    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {
        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);

        driver.get(info.BK_href().replace("https://www.pinnacle.com/ru/", "https://www.pinnacle.com/en/"));
    }

    @Override
    public BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sport sport) {
        String marketName;
        String selectionName;
        if (info.BK_market_meta().getAsJsonObject().get("is_special").getAsBoolean()) {
            marketName = getMarketName(info.BK_bet(), sport);
            selectionName = getSelectionName(info, sport);
        } else {
            marketName = info.BK_market_meta().getAsJsonObject().get("market_name").getAsString();
            if (marketName.contains(" | ")) {
                marketName = marketName.split(" - ")[0];
                selectionName = marketName.split(" - ")[1];
            } else {
                throw new RuntimeException("Don`t support BetType [pinnacle]:" + info.BK_bet() + "| sport: " + sport);
            }
        }
        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        String finalMarketName = marketName;
        var market = wait.until(driver1 -> driver1.findElement(SeleniumSupport.buildGlobalSpanByText(finalMarketName)));

        var button = SeleniumSupport.findElementWithClicking(driver, market, SeleniumSupport.buildLocalSpanByText(selectionName));

        wait.until(ExpectedConditions.elementToBeClickable(button)).click();

        return new BigDecimal("0");
    }
    @Override
    public void enterSumAndCheckCf(Bookmaker bookmaker, Parser.BetInfo info, BigDecimal sum) {
        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        var curCfTest = wait.until(driver1 -> driver1.findElement(By.cssSelector("[data-test-id='SelectionDetails-Odds']")));
        var currentCf = new BigDecimal(curCfTest.getText());

        if (currentCf.compareTo(info.BK_cf().setScale(2, RoundingMode.DOWN)) < 0) {
            throw new RuntimeException("betCoef is too low [pinnacle] - было %s, стало %s".formatted(info.BK_cf().setScale(2, RoundingMode.DOWN), currentCf));
        }

        if (sum.compareTo(new BigDecimal("50")) < 0) { // TODO
            throw new RuntimeException("Very small min Bet [pinnacle]");
        }

        wait.until(driver_ -> driver_.findElement(By.cssSelector("[placeholder='Stake']"))).sendKeys(sum.toString());
    }

    @Override
    public BigDecimal placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info) {
        return info.BK_cf();
    }

    private String getSelectionName(Parser.BetInfo info, Sport sport) {
        var bkBet = info.BK_bet();
        if (bkBet.contains("WIN__P1")) {
           return BetsSupport.getTeamFirstNameByTitle(info.BK_game());
        } else if (bkBet.contains("WIN__P2")) {
            return BetsSupport.getTeamSecondNameByTitle(info.BK_game());
        } else if (bkBet.contains("WIN__PX")) {
            return "Draw";
        }

        String digits = bkBet;
        digits = digits.substring(digits.indexOf("(") + 1);
        digits = digits.substring(0, digits.indexOf(")"));

        if (bkBet.contains("__OVER")) {
            return "Over " + digits;
        } else if (bkBet.contains("__UNDER")) {
            return "Under " + digits;
        } else if (bkBet.contains("HANDICAP")) {
            if (digits.startsWith("-")) {
                return digits;
            }
            return "+" + digits;
        }
        throw new RuntimeException("Don`t support BetType [pinnacle]:" + info.BK_bet() + "| sport: " + sport);
    }

    private String getMarketName(String betType, Sport sport) {
        return getBetType(betType, sport) + " - " + getPartOfGame(betType, sport);
    }

    private String getBetType(String betType, Sport sport) {
        var tennisSets = "";
        if (sport.equals(Sport.TENNIS)) {
            tennisSets = " (Sets)";
        }
        if (betType.contains("WIN")) {
            return "Money Line" + tennisSets;
        } else if (betType.contains("TEAM_TOTALS")) {
            return "Team Total" + tennisSets;
        } else if (betType.contains("TOTALS")) {
            return "Total" + tennisSets;
        } else if (betType.contains("HANDICAP")) {
            return "Handicap" + tennisSets;
        }
        throw new RuntimeException("Don`t support BetType [pinnacle]:" + betType + "| sport: " + sport);
    }

    private String getPartOfGame(String betType, Sport sport) {
        switch (sport) {
            case SOCCER, HANDBALL -> {
                if (betType.contains("HALF_01__")) {
                    return "1st Half";
                } else if (betType.contains("HALF_02__")) {
                    return "2nd Half";
                }
            }
            case TENNIS -> {
                if (betType.contains("SET_01__")) {
                    return "1st Set";
                } else if (betType.contains("SET_02__")) {
                    return "2nd Set";
                } else if (betType.contains("SET_03__")) {
                    return "3rd Set";
                } else if (betType.contains("SET_04__")) {
                    return "4th Set";
                }
            }
            case BASKETBALL -> {
                if (betType.contains("HALF_01__")) {
                    return "1st Half";
                } else if (betType.contains("HALF_02__")) {
                    return "2nd Half";
                }
                // TODO: quarters ? - not found
            }
            case HOCKEY -> {
                if (betType.contains("SET_01__")) {
                    return "1st Period";
                } else if (betType.contains("SET_02__")) {
                    return "2nd Period";
                } else if (betType.contains("SET_03__")) {
                    return "3rd Period";
                }
            }
            case VOLLEYBALL -> {
                // TODO: not found
            }
        }
        var isFullGame = betType.startsWith("WIN") || betType.startsWith("TEAM_TOTALS") || betType.startsWith("TOTALS") || betType.startsWith("HANDICAP");
        if (isFullGame) {
            switch (sport) {
                case SOCCER, TENNIS, HANDBALL -> { return "Match"; }
                case BASKETBALL -> { return "Game"; }
                case HOCKEY -> { return "Regulation Time"; }
                case VOLLEYBALL -> {
                    // TODO: not found
                }
            }
        }
        throw new RuntimeException("Don`t support BetType [pinnacle]:" + betType + "| sport: " + sport);
    }
}
