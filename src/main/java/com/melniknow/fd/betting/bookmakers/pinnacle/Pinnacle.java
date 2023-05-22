package com.melniknow.fd.betting.bookmakers.pinnacle;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.betting.bookmakers._188bet.BetsSupport;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Currency;
import com.melniknow.fd.domain.Sport;
import com.melniknow.fd.utils.MathUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
        removeAllPreviousWindows(driver);

        String marketName;
        String selectionName;
        if (!info.BK_market_meta().getAsJsonObject().get("is_special").getAsBoolean()) {
            marketName = getMarketName(info.BK_bet(), sport, info.BK_href());
            selectionName = getSelectionName(info, sport);
        } else {
            marketName = info.BK_market_meta().getAsJsonObject().get("market_name").getAsString();
            if (marketName.contains(" | ")) {
                marketName = marketName.split(" - ")[0];
                selectionName = marketName.split(" - ")[1];
            } else {
                throw new RuntimeException("Don`t support BetType [pinnacle]:" + info.BK_bet() + " | sport: " + sport);
            }
        }

        System.out.println("info.BK_bet() = " + info.BK_bet());
        System.out.println("marketName [pinnacle] = " + marketName);
        System.out.println("selectionName [pinnacle] = " + selectionName);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        String finalMarketName = marketName;
        var market = wait.until(driver1 -> driver1.findElement(SeleniumSupport.buildGlobalSpanByText(finalMarketName)));

        market = SeleniumSupport.getParentByDeep(market, 2);

        WebElement button;
        if (marketName.contains("Handicap") && selectionName.equals("0")) {
            var buttons = SeleniumSupport.findElementsWithClicking(driver, market, SeleniumSupport.buildLocalSpanByText(selectionName));
            if (info.BK_bet().contains("P1")) {
                button = buttons.get(0);
            } else if (info.BK_bet().contains("P2")) {
                button = buttons.get(1);
            } else {
                throw new RuntimeException("Don`t support [pinnacle]: " + info.BK_bet());
            }
        } else {
            button = SeleniumSupport.findElementWithClicking(driver, market,
                By.xpath(".//span[contains(text(), '" + selectionName + "')]"));
        }

        wait.until(ExpectedConditions.elementToBeClickable(button)).click();

        return getBalance(driver, Context.betsParams.get(bookmaker).currency());
    }
    @Override
    public void enterSumAndCheckCf(Bookmaker bookmaker, Parser.BetInfo info, BigDecimal sum) {
        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);

        var currentCf = getCurrentCf(driver);

        System.out.println("Current Cf [pinnacle] = " + currentCf);

        if (currentCf.compareTo(info.BK_cf().setScale(2, RoundingMode.DOWN)) < 0) {
            throw new RuntimeException("betCoef is too low [pinnacle] - было %s, стало %s".formatted(info.BK_cf().setScale(2, RoundingMode.DOWN), currentCf));
        }

        if (sum.compareTo(new BigDecimal("1")) < 0) {
            throw new RuntimeException("Very small min Bet [pinnacle]; sum = " + sum);
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(driver_ -> driver_.findElement(By.cssSelector("[placeholder='Stake']"))).sendKeys(sum.toString());
    }

    @Override
    public BigDecimal placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info, boolean isFirst, BigDecimal cf1) {
        try {
            var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
            waitLoop(driver, info.BK_cf(), cf1);
            return getCurrentCf(driver);
        } catch (Exception e) {
            throw new RuntimeException("bet not place [pinncale]");
        }
    }

    private void waitLoop(ChromeDriver driver, BigDecimal oldCf, BigDecimal cf1) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        while (true) {

            try {
                var curCf = getCurrentCf(driver);
                if (curCf.compareTo(oldCf) >= 0) {
                    var placeBet = wait.until(driver1 -> driver1.findElement(By.cssSelector("[data-test-id='Betslip-ConfirmBetButton']")));
                    wait.until(ExpectedConditions.elementToBeClickable(placeBet)).click();
                } else {
                    var income = MathUtils.calculateIncome(curCf, cf1);
                    if (income.compareTo(Context.maxMinus) < 0) {
                        throw new RuntimeException("Ставка не поставленаЖ максимальный минус - " + income);
                    } else {
                        // TODO
                    }
                }
            } catch (TimeoutException ignored) {
                try {
                    wait.until(driver1 -> driver1.findElement(SeleniumSupport.buildGlobalSpanByText("Bet Accepted")));
                    break;
                } catch (TimeoutException e) {
                }
            }
        }
    }

    private BigDecimal getCurrentCf(ChromeDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        var curCfTest = wait.until(driver1 -> driver1.findElement(By.cssSelector("[data-test-id='SelectionDetails-Odds']")));
        System.out.println("curCfTest [pinnacle] = " + curCfTest);
        return new BigDecimal(curCfTest.getText());
    }

    private void removeAllPreviousWindows(ChromeDriver driver) {
        try {
            var removeAll = driver.findElement(SeleniumSupport.buildGlobalSpanByText("Remove all"));
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.elementToBeClickable(removeAll)).click();
            var confirm = wait.until(driver1 -> driver1.findElement(SeleniumSupport.buildGlobalSpanByText("Confirm")));
            wait.until(ExpectedConditions.elementToBeClickable(confirm)).click();
        } catch (NoSuchElementException | TimeoutException ignored) {
            // it`s possible that there are no previous windows
        }
    }

    private BigDecimal getBalance(ChromeDriver driver, Currency currency) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        var curBalanceText = wait.until(driver1 -> driver1.findElement(By.cssSelector("[data-test-id='QuickCashier-BankRoll']"))).getText();
        System.out.println("curBalanceTest [pinnacle] = " + curBalanceText);
        curBalanceText = curBalanceText.substring(4);
        curBalanceText = curBalanceText.replace(",", "");
        var balance = new BigDecimal(curBalanceText);
        if (balance.equals(BigDecimal.ZERO)) {
            throw new RuntimeException("Balance is zero [pinnacle]");
        }
        System.out.println("Balance from header " + currency + " : " + balance + " [pinnacle]");
        return balance.multiply(Context.currencyToRubCourse.get(currency));
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
            digits = info.BK_market_meta().getAsJsonObject().get("points").getAsString();
            if (digits.equals("0.0") || digits.equals("-0.0") || digits.equals("+0.0")) {
                return "0";
            }
            if (digits.startsWith("-")) {
                return digits;
            }
            return "+" + digits;
        }
        throw new RuntimeException("Don`t support BetType [pinnacle]:" + info.BK_bet() + "| sport: " + sport);
    }

    private String getMarketName(String betType, Sport sport, String ref) {
        return getBetType(betType, sport, ref) + " – " + getPartOfGame(betType, sport);
    }

    private String getBetType(String betType, Sport sport, String ref) {
        var tennisSuffix = "";
        if (sport.equals(Sport.TENNIS) && ref.contains("(games)")) {
            tennisSuffix = " (Games)";
        } else if (sport.equals(Sport.TENNIS)) {
            tennisSuffix = " (Sets)";
        }
        if (betType.contains("WIN")) {
            return "Money Line" + tennisSuffix;
        } else if (betType.contains("TEAM_TOTALS")) {
            return "Team Total" + tennisSuffix;
        } else if (betType.contains("TOTALS")) {
            return "Total" + tennisSuffix;
        } else if (betType.contains("HANDICAP")) {
            return "Handicap" + tennisSuffix;
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
            case TENNIS, VOLLEYBALL -> {
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
                } else if (betType.contains("SET_01__")) {
                    return "1st Quarter";
                } else if (betType.contains("SET_02__")) {
                    return "2nd Quarter";
                } else if (betType.contains("SET_03__")) {
                    return "3rd Quarter";
                } else if (betType.contains("SET_04__")) {
                    return "4th Quarter";
                }
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
        }
        var isFullGame = betType.startsWith("WIN") || betType.startsWith("TEAM_TOTALS") || betType.startsWith("TOTALS") || betType.startsWith("HANDICAP");
        if (isFullGame) {
            switch (sport) {
                case SOCCER, TENNIS, HANDBALL, VOLLEYBALL -> { return "Match"; }
                case BASKETBALL -> { return "Game"; }
                case HOCKEY -> { return "Regulation Time"; }
            }
        }
        throw new RuntimeException("Don`t support BetType [pinnacle]:" + betType + "| sport: " + sport);
    }
}
