package com.melniknow.fd.betting.bookmakers.pinnacle;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.betting.bookmakers.ShoulderInfo;
import com.melniknow.fd.betting.bookmakers._188bet.BetsSupport;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Currency;
import com.melniknow.fd.domain.Sport;
import com.melniknow.fd.utils.BetUtils;
import com.melniknow.fd.utils.MathUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;

public class Pinnacle implements IBookmaker {
    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {
        try {
            Context.log.info("Call openLink Pinnacle");
            var driver = Context.screenManager.getScreenForBookmaker(bookmaker);

            // ебаный oddscorp присылает ru
            driver.get(info.BK_href().replace("https://www.pinnacle.com/ru/", "https://www.pinnacle.com/en/"));
        } catch (TimeoutException e) {
            throw new RuntimeException("[Pinnacle]: Страница не загружается!");
        }
    }

    @Override
    public BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sport sport, boolean isNeedToClick) {
        Context.log.info("Call clickOnBetTypeAndReturnBalanceAsRub Pinnacle");
        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
        removeAllPreviousWindows(driver);

        // Далее ебанутейшая логика с нахождением marketName, selectionName
        String marketName;
        String selectionName;
        if (!info.BK_market_meta().getAsJsonObject().get("is_special").getAsBoolean()) {
            marketName = getMarketName(info.BK_bet(), sport, info.BK_href());
            selectionName = getSelectionName(info, sport);
        } else {
            marketName = info.BK_market_meta().getAsJsonObject().get("market_name").getAsString();
            if (marketName.contains(" | ")) {
                marketName = marketName.split(" \\| ")[0];
                selectionName = marketName.split(" \\| ")[1];
            } else {
                throw new RuntimeException("[pinnacle]: неподдерживаемый BetType: " + info.BK_bet() + " | sport: " + sport);
            }
        }

        Context.log.info("[pinnacle]: info.BK_cf() = " + info.BK_cf() + "\n" +
            "[pinnacle]: info.BK_bet() = " + info.BK_bet() + "\n" +
            "[pinnacle]: marketName = " + marketName + "\n" +
            "[pinnacle]: selectionName = " + selectionName);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        WebElement market;
        try {
            // забираем маркет
            String finalMarketName = marketName;
            market = wait.until(driver1 -> driver1.findElement(SeleniumSupport.buildGlobalSpanByText(finalMarketName)));
        } catch (TimeoutException e) {
            throw new RuntimeException("[pinnacle]: Событие пропало со страницы");
        }

        // Проверка входа в аккаунт
        try {
            new WebDriverWait(driver, Duration.ofMillis(200)).until(driver_ -> driver_.findElement(By.xpath("/html/body/div[2]/div/div[1]/div[1]/div[2]/div[2]/div/div/div[4]/button[text() = 'Log in']")));
            SeleniumSupport.login(driver, bookmaker);
            throw new RuntimeException("Мы вошли в аккаунт [pinnacle]");
        } catch (WebDriverException e) {
            if (e.getCause() instanceof InterruptedException)
                throw new RuntimeException("Поток прерван [pinnacle]");
        } catch (Exception ignored) {
        }
        // -----------------------

        market = SeleniumSupport.getParentByDeep(market, 2);

        WebElement button;
        // Этот случай нужно обработать отдельно, тк там просто две идентичные кнопки с нулём
        if (marketName.contains("Handicap") && selectionName.equals("0")) {
            List<WebElement> buttons;
            try {
                // забираем эти 2 нуля
                buttons = SeleniumSupport.findElementsWithClicking(driver, market, SeleniumSupport.buildLocalSpanByText(selectionName));
            } catch (NoSuchElementException ignored) {
                throw new RuntimeException("[pinnacle]: Коэффициенты события изменились. Не найдена кнопка: " + selectionName);
            }
            if (buttons.size() != 2) {
                throw new RuntimeException("[pinnacle]: Коэффициенты события изменились. Не найдена кнопка: " + selectionName);
            }
            // первый в списке это команда слева, второй - справа
            if (info.BK_bet().contains("P1")) {
                button = buttons.get(0);
            } else if (info.BK_bet().contains("P2")) {
                button = buttons.get(1);
            } else {
                throw new RuntimeException("[pinnacle]: неподдерживаемый BetType: " + info.BK_bet());
            }
        } else {
            try {
                // Находим нужную кнопку
                button = SeleniumSupport.findElementWithClicking(driver, market,
                    By.xpath(".//span[contains(text(), '" + selectionName + "')]"));
            } catch (RuntimeException e) {
                throw new RuntimeException("[pinnacle]: Коэффициенты события изменились. Не найдена кнопка: " + selectionName);
            }
        }

        try {
            var buttonText = SeleniumSupport.getParentByDeep(button, 1).getText();
            var curCf = new BigDecimal(buttonText.split("\n")[1]);

            Context.log.info("[pinnacle]: buttonText = " + buttonText + "\n" +
                "[pinnacle]: Current Cf from click = " + curCf);

            var inaccuracy = new BigDecimal("0.01");

            if (curCf.add(inaccuracy).setScale(2, RoundingMode.DOWN).compareTo(info.BK_cf().setScale(2, RoundingMode.DOWN)) < 0) {
                throw new RuntimeException("[pinnacle]: коэффициент упал - было %s, стало %s".formatted(info.BK_cf().setScale(2, RoundingMode.DOWN), curCf));
            }

            if (isNeedToClick) {
                wait.until(ExpectedConditions.elementToBeClickable(button)).click();
            }

        } catch (StaleElementReferenceException | ElementNotInteractableException |
                 IndexOutOfBoundsException e) {
            throw new RuntimeException("[pinnacle]: Событие пропало со страницы");
        }
        return getBalance(driver, Context.betsParams.get(bookmaker).currency());
    }

    @Override
    public void enterSumAndCheckCf(Bookmaker bookmaker, Parser.BetInfo info, BigDecimal sum) {
        Context.log.info("Call enterSumAndCheckCf Pinnacle");
        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);

        var currentCf = getCurrentCf(driver, false, info.BK_cf());

        Context.log.info("[pinnacle]: Current Cf = " + currentCf);

        var inaccuracy = new BigDecimal("0.01");

        if (currentCf.add(inaccuracy).setScale(2, RoundingMode.DOWN).compareTo(info.BK_cf().setScale(2, RoundingMode.DOWN)) < 0) {
            throw new RuntimeException("[pinnacle]: коэффициент упал - было %s, стало %s"
                .formatted(info.BK_cf().setScale(2, RoundingMode.DOWN), currentCf.setScale(2, RoundingMode.DOWN)));
        }

        if (sum.compareTo(new BigDecimal("1")) < 0) {
            throw new RuntimeException("[pinnacle]: Не ставим ставки меньше 1,  sum = " + sum);
        }

        SeleniumSupport.enterSum(driver, By.cssSelector("[placeholder='Stake']"), sum, "pinnacle");
    }

    @Override
    public BigDecimal placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info, ShoulderInfo shoulderInfo) {
        Context.log.info("Call placeBetAndGetRealCf Pinnacle");
        try {
            var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
            return waitLoop(driver, info.BK_name(), info.BK_cf(), shoulderInfo);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // наши состояния во время простановки ставки
    private static final By byPlaceBet = By.cssSelector("[data-test-id='Betslip-ConfirmBetButton']");
    private static final By byPlaceBetSpan = SeleniumSupport.buildGlobalSpanByText("CONFIRM 1 SINGLE BET");
    private static final By byOddsChanges = SeleniumSupport.buildGlobalSpanByText("Odds changed:");
    private static final By byBetSuccess = SeleniumSupport.buildGlobalSpanByText("Bet Accepted");
    private static final By byBetClosed = SeleniumSupport.buildGlobalSpanByText("Bet not accepted. Please try again or remove this selection from your Bet Slip.");

    private BigDecimal waitLoop(ChromeDriver driver, String bkName, BigDecimal oldCf, ShoulderInfo shoulderInfo) {
        // в цикле - жмём на кнопку - пытаемся подождать результата
        for (int i = 0; i < 15; ++i) {
            updateOdds(driver, bkName, oldCf, shoulderInfo);
            if (waitSuccess(driver)) {
                return getCurrentCf(driver, true, oldCf);
            }
        }
        throw new RuntimeException("[pinnacle]: Плечо не может быть проставлено");
    }

    private boolean waitSuccess(ChromeDriver driver) {
        for (int i = 0; i < 30; ++i) {
            try {
                Context.log.info("[pinnacle]: Wait....");
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
                wait.until(driver1 -> driver1.findElement(byBetSuccess));
                return true;
            } catch (Exception e) {
                // После попытки подождать чекаем ничего ли не произошло пока мы ждали?
                if (windowContains(driver, byOddsChanges) || windowContains(driver, byPlaceBet) || windowContains(driver, byBetClosed)) { // isActivePlaceBet???
                    Context.log.info("[pinnacle]: Exit from wait");
                    // что-то появилось, ждать бесполезно идём нажимать на новую кнопку
                    return false;
                }
            }
        }
        throw new RuntimeException("[pinnacle]: Плечо не может быть проставлено");
    }

    private void updateOdds(ChromeDriver driver, String bkName, BigDecimal oldCf, ShoulderInfo shoulderInfo) {
        // Ставка закрыта?
        if (windowContains(driver, byBetClosed)) {
            throw new RuntimeException("[pinnacle]: Ставка закрыта");
        }
        // Кнопка может быть не активна
        if (!isActivePlaceBet(driver)) {
            Context.log.info("[pinnacle]: Is not active");
            return;
        }

        var curCf = getCurrentCf(driver, false, oldCf);
        var inaccuracy = new BigDecimal("0.01");
        if (curCf.add(inaccuracy).setScale(2, RoundingMode.DOWN).compareTo(oldCf.setScale(2, RoundingMode.DOWN)) >= 0) {
            Context.log.info("[pinnacle]: Click Place 1");
            clickIfIsClickable(driver, byPlaceBet);
        } else if (!shoulderInfo.isFirst()) { // Мы второе плечо - пересчитываем и пытаемся перекрыться если коэф упал
            var newIncome = MathUtils.calculateIncome(curCf, shoulderInfo.cf1());
            Context.log.info("[pinnacle]: newIncome = " + newIncome);
            if (newIncome.compareTo(Context.parserParams.maxMinus()) < 0) { // если превысили максимальный минус
                Context.log.info("[pinnacle]: Max minus: newIncome = " + newIncome);
                throw new RuntimeException("[pinnacle]: превышен максимальный минус: maxMinus = " + Context.parserParams.maxMinus() + ", а текущий минус = " + newIncome);
            } else {
                // считаем новую сумму
                Context.log.info("[pinnacle]: Click Place 2");
                // забираем наши валюты
                var currencySecondShoulder = Context.currencyToRubCourse.get(Context.betsParams.get(BetUtils.getBookmakerByNameInApi(bkName)).currency());
                var currencyFirstShoulder = Context.currencyToRubCourse.get(Context.betsParams.get(BetUtils.getBookmakerByNameInApi(shoulderInfo.bk1Name())).currency());

                var scale = Context.betsParams.get(BetUtils.getBookmakerByNameInApi(bkName)).accuracy().intValue();

                var newSum = shoulderInfo.cf1()
                    .multiply(shoulderInfo.sum1().multiply(currencyFirstShoulder))
                    .divide(curCf, 2, RoundingMode.DOWN)
                    .divide(currencySecondShoulder, scale, RoundingMode.DOWN);

                Context.log.info("[pinnacle]: newSum = " + newSum + " | with cf = " + curCf);

                SeleniumSupport.enterSum(driver, By.cssSelector("[placeholder='Stake']"),  newSum, "pinnacle");

                clickIfIsClickable(driver, byPlaceBet);
            }
        } else {
            throw new RuntimeException("[pinnacle]: Коэфициент на первом плече упал. Было - " + oldCf + " стало - " + curCf);
        }
    }

    private static boolean windowContains(ChromeDriver driver, By by) {
        try {
            var wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            wait.until(
                driver1 -> driver1.findElement(by));
            return true;
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted() || e.getCause() instanceof InterruptedException)
                throw new RuntimeException();
            return false;
        }
    }

    private static boolean isActivePlaceBet(ChromeDriver driver) {
        try {
            var bt = driver.findElement(byPlaceBet);
            return bt.isEnabled();
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted() || e.getCause() instanceof InterruptedException)
                throw new RuntimeException();
            return false;
        }
    }

    private static boolean clickIfIsClickable(ChromeDriver driver, By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        try {
            var button = wait.until(driver_ -> driver_.findElement(by));
            wait.until(ExpectedConditions.elementToBeClickable(button));
            driver.executeScript("arguments[0].click();", button);
            return true;
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted() || e.getCause() instanceof InterruptedException)
                throw new RuntimeException();
            return false;
        }
    }

    private BigDecimal getCurrentCf(ChromeDriver driver, boolean isAfterSuccess, BigDecimal expectedCf) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            var curCfTest = wait.until(driver1 -> driver1.findElement(By.cssSelector("[data-test-id='SelectionDetails-Odds']")));
            Context.log.info("[pinnacle]: curCfTest = " + curCfTest);
            return new BigDecimal(curCfTest.getText());
        } catch (TimeoutException e) {
            // Если вдруг мы смогли поставить, но коэффициент пропал, мы ни в коем случае не кинем исключение - бот не поймёт, что мы поставили
            if (isAfterSuccess) {
                return expectedCf;
            }
            throw new RuntimeException("[pinnacle]: Коэффициент события не найден!");
        }
    }

    private void removeAllPreviousWindows(ChromeDriver driver) {
        try {
            // нажимаем на Remove all
            var removeAll = driver.findElement(SeleniumSupport.buildGlobalSpanByText("Remove all"));
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.elementToBeClickable(removeAll)).click();
            // Подтверждаем удаление предыдущих окон - Confirm
            var confirm = wait.until(driver1 -> driver1.findElement(SeleniumSupport.buildGlobalSpanByText("Confirm")));
            wait.until(ExpectedConditions.elementToBeClickable(confirm)).click();
        } catch (NoSuchElementException | TimeoutException | StaleElementReferenceException |
                 ElementNotInteractableException ignored) {
            // it`s possible that there are no previous windows
        }
    }

    private BigDecimal getBalance(ChromeDriver driver, Currency currency) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            // ищем окошко с балансом
            var curBalanceText = wait.until(driver1 -> driver1.findElement(By.cssSelector("[data-test-id='QuickCashier-BankRoll']"))).getText();
            Context.log.info("curBalanceTest [pinnacle] = " + curBalanceText);
            // откусываем валюту и пробел
            curBalanceText = curBalanceText.substring(4);
            curBalanceText = curBalanceText.replace(",", "");
            var balance = new BigDecimal(curBalanceText);
            if (balance.equals(BigDecimal.ZERO)) {
                throw new RuntimeException("[pinnacle]: Нулевой баланс!");
            }
            Context.log.info("Balance from header " + currency + " : " + balance + " [pinnacle]");
            return balance.multiply(Context.currencyToRubCourse.get(currency));
        } catch (TimeoutException e) {
            throw new RuntimeException("[pinnacle]: баланс не найден");
        }
    }

    private String getSelectionName(Parser.BetInfo info, Sport sport) {
        var bkBet = info.BK_bet();
        if (bkBet.contains("WIN__P1")) {
            return BetsSupport.getTeamFirstNameByTitle(info.BK_game());
        } else if (bkBet.contains("WIN__P2")) {
            return BetsSupport.getTeamSecondNameByTitle(info.BK_game());
        } else if (bkBet.contains("WIN__PX")) {
            return "Draw";
        } else if (bkBet.contains("GAME") && bkBet.contains("P1")) {
            return BetsSupport.getTeamFirstNameByTitle(info.BK_game());
        } else if (bkBet.contains("GAME") && bkBet.contains("P2")) {
            return BetsSupport.getTeamSecondNameByTitle(info.BK_game());
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
            Context.log.info("[pinnacle] points = " + digits);
            if (digits.equals("0.0") || digits.equals("-0.0") || digits.equals("+0.0")) {
                return "0";
            }
            if (digits.startsWith("-")) {
                return digits;
            }
            return "+" + digits;
        }
        throw new RuntimeException("[pinnacle]: Don`t support BetType " + info.BK_bet() + "| sport: " + sport);
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
        } else if (betType.contains("GAME")) {
            return "Money Line (Games)";
        } else if (betType.contains("TEAM_TOTALS")) {
            return "Team Total" + tennisSuffix;
        } else if (betType.contains("TOTALS")) {
            return "Total" + tennisSuffix;
        } else if (betType.contains("HANDICAP")) {
            return "Handicap" + tennisSuffix;
        }
        throw new RuntimeException("[pinnacle]: Don`t support BetType " + betType + "| sport: " + sport);
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
                } else if (betType.startsWith("GAME")) {
                    betType = betType.substring(6);
                    betType = removePrefix(betType);
                    var set = Integer.parseInt(betType.split("_")[0]);
                    betType = betType.substring(betType.indexOf("_") + 1);
                    betType = removePrefix(betType);
                    var game = Integer.parseInt(betType.split("_")[0]);
                    return "Set " + set + " Game " + game;
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
        throw new RuntimeException("[pinnacle]: Don`t support BetType:" + betType + "| sport: " + sport);
    }

    private static String removePrefix(String str) {
        var newStr = str;
        while (newStr.startsWith("_") || newStr.startsWith("0")) {
            newStr = newStr.substring(1);
        }
        return newStr;
    }
}
