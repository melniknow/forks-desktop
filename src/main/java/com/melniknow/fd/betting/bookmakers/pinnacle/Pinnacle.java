package com.melniknow.fd.betting.bookmakers.pinnacle;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.betting.bookmakers.ShoulderInfo;
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

public class Pinnacle implements IBookmaker {

    private WebElement curButton;
    private BigDecimal realSum;

    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {
        try {
            curButton = null;
            realSum = null;

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
            var parser = new PinnacleParser(info, sport);
            var clickBox = parser.parse();
            marketName = clickBox.marketName() + " – " + clickBox.partOfGame();
            selectionName = clickBox.selectionName();
        } else {
            var marketNameFromMeta = info.BK_market_meta().getAsJsonObject().get("market_name").getAsString();
            if (marketNameFromMeta.contains(" | ")) {
                marketName = marketNameFromMeta.split(" \\| ")[0];
                selectionName = marketNameFromMeta.split(" \\| ")[1];
            } else {
                throw new RuntimeException("[pinnacle]: неподдерживаемый BetType: " + info.BK_bet() + " | sport: " + sport);
            }
        }

        Context.log.info("\n\n[pinnacle]:\n info.BK_game() = " + info.BK_game() + "\n" +
            "[pinnacle]: info.BK_cf() = " + info.BK_cf() + "\n" +
            "[pinnacle]: info.BK_bet() = " + info.BK_bet() + "\n" +
            "[pinnacle]: marketName = " + marketName + "\n" +
            "[pinnacle]: selectionName = " + selectionName + "\n\n");

        var market = getMarket(driver, SeleniumSupport.buildGlobalSpanByText(marketName));

        // возможно, данные скрыты - раскроем
        if (market.getAttribute("data-collapsed") != null && market.getAttribute("data-collapsed").equals("true")) {
            market.click();
        }
        clickOnSeeMore(driver, market);

        // Проверка входа в аккаунт
        try {
            driver.findElement(By.xpath("//button[text()='Log in']"));
            SeleniumSupport.login(driver, bookmaker);
            throw new RuntimeException("Мы вошли в аккаунт [pinnacle]");
        } catch (WebDriverException e) {
            if (e.getCause() instanceof InterruptedException)
                throw new RuntimeException("Поток прерван [pinnacle]");
        } catch (Exception ignored) {
        }

        WebElement button;
        // Этот случай нужно обработать отдельно, тк там просто две идентичные кнопки
        if (marketName.contains("Handicap") || marketName.contains("Team Total")) {
            button = getButtonOnHandicapOrTeamTotals(driver, market, selectionName, info.BK_bet());
        } else {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
                // Находим нужную кнопку
                button = wait.until(driver1 -> market.findElement(By.xpath(".//span[contains(text(), '" + selectionName + "')]")));
            } catch (RuntimeException e) {
                throw new RuntimeException("[pinnacle]: Коэффициенты события изменились. Не найдена кнопка: " + selectionName);
            }
        }
        try {
            // Получаем текущий коэф и чекаем его
            var buttonText = SeleniumSupport.getParentByDeep(button, 1).getText();
            var curCf = new BigDecimal(buttonText.split("\n")[1]);

            Context.log.info("[pinnacle]: Final buttonText = " + buttonText + "\n" +
                "[pinnacle]: Current Cf from click = " + curCf);

            var inaccuracy = new BigDecimal("0.01");
            if (curCf.add(inaccuracy).setScale(2, RoundingMode.DOWN).compareTo(info.BK_cf().setScale(2, RoundingMode.DOWN)) < 0) {
                throw new RuntimeException("[pinnacle]: коэффициент упал - было %s, стало %s".formatted(info.BK_cf().setScale(2, RoundingMode.DOWN), curCf));
            }
            // сохраняем всю кнопку
            this.curButton = SeleniumSupport.getParentByDeep(button, 1);
        } catch (StaleElementReferenceException | ElementNotInteractableException |
                 IndexOutOfBoundsException e) {
            throw new RuntimeException("[pinnacle]: Событие пропало со страницы");
        }
        return getBalance(driver, Context.betsParams.get(bookmaker).currency());
    }

    @Override
    public BetUtils.BetData placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info, ShoulderInfo shoulderInfo, BigDecimal sum) {
        Context.log.info("Call placeBetAndGetRealCf Pinnacle");

        if (sum.compareTo(new BigDecimal("1")) < 0) {
            throw new RuntimeException("[pinnacle]: Не ставим ставки меньше 1,  sum = " + sum);
        }

        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
        realSum = sum;
        try {
            curButton.click();
            SeleniumSupport.enterSum(driver, By.cssSelector("[placeholder='Stake']"), sum, "pinnacle");

            return new BetUtils.BetData(waitLoop(driver, info.BK_name(), info.BK_cf(), shoulderInfo), realSum);
        } catch (StaleElementReferenceException e) {
            throw new RuntimeException("[pinnacle]: событие пропало со страницы (не смогли нажать на кнопку)");
        } catch (RuntimeException e) {
//            removeAllPreviousWindows(driver);
            throw new RuntimeException(e.getMessage());
        }
    }

    // наши состояния во время простановки ставки
    private static final By byPlaceBet = By.cssSelector("[data-test-id='Betslip-ConfirmBetButton']");
    private static final By byOddsChanges = SeleniumSupport.buildGlobalSpanByText("Odds changed:");
    private static final By byBetSuccess = SeleniumSupport.buildGlobalSpanByText("Bet Accepted");
    private static final By byBetClosed = SeleniumSupport.buildGlobalSpanByText("Bet not accepted. Please try again or remove this selection from your Bet Slip.");

    private BigDecimal waitLoop(ChromeDriver driver, String bkName, BigDecimal oldCf, ShoulderInfo shoulderInfo) {
        // в цикле - жмём на кнопку - пытаемся подождать результата
        var isFirstClick = true;
        for (int i = 0; i < 15; ++i) {
            updateOdds(driver, bkName, oldCf, shoulderInfo, isFirstClick);
            isFirstClick = false;
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
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
                wait.pollingEvery(Duration.ofMillis(100));
                wait.until(driver1 -> driver1.findElement(byBetSuccess));
                return true;
            } catch (Exception e) {
                // После попытки подождать чекаем ничего ли не произошло пока мы ждали?
                if (windowContains(driver, byOddsChanges) || isActivePlaceBet(driver) || windowContains(driver, byBetClosed)) {
                    Context.log.info("[pinnacle]: Exit from wait");
                    // что-то появилось, ждать бесполезно идём нажимать на новую кнопку
                    return false;
                }
            }
        }
        throw new RuntimeException("[pinnacle]: Плечо не может быть проставлено");
    }

    private void updateOdds(ChromeDriver driver, String bkName, BigDecimal oldCf, ShoulderInfo shoulderInfo, boolean isFirstClick) {
        if (!isFirstClick) {
            // Ставка закрыта?
            if (windowContains(driver, byBetClosed)) {
                throw new RuntimeException("[pinnacle]: Ставка закрыта");
            }
            // Кнопка может быть не активна
            if (!isActivePlaceBet(driver)) {
                Context.log.info("[pinnacle]: Is not active");
                return;
            }
        }
        var curCf = getCurrentCf(driver, false, oldCf);
        var inaccuracy = new BigDecimal("0.01");
        if (curCf.add(inaccuracy).setScale(2, RoundingMode.DOWN).compareTo(oldCf.setScale(2, RoundingMode.DOWN)) >= 0) {
            Context.log.info("[pinnacle]: Click Place 1");
            for (int i = 0; i < 10; ++i) {
                if (clickIfIsClickable(driver)) {
                    return;
                }
            }
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

                SeleniumSupport.enterSum(driver, By.cssSelector("[placeholder='Stake']"), newSum, "pinnacle");
                realSum = newSum;
                for (int i = 0; i < 10; ++i) {
                    if (clickIfIsClickable(driver)) {
                        return;
                    }
                }
            }
        } else {
            throw new RuntimeException("[pinnacle]: Коэфициент на первом плече упал. Было - " + oldCf + " стало - " + curCf);
        }
    }

    private static boolean windowContains(ChromeDriver driver, By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted() || e.getCause() instanceof InterruptedException)
                throw new RuntimeException();
            return false;
        }
    }

    private static boolean isActivePlaceBet(ChromeDriver driver) {
        try {
            var res = driver.findElement(byPlaceBet).isEnabled();
            Context.log.info("isActivePlaceBet = %b".formatted(res));
            return res;
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted() || e.getCause() instanceof InterruptedException)
                throw new RuntimeException();
            return false;
        }
    }

    private static boolean clickIfIsClickable(ChromeDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        wait.pollingEvery(Duration.ofMillis(100));
        try {
            var button = wait.until(driver_ -> driver_.findElement(Pinnacle.byPlaceBet));
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
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            wait.pollingEvery(Duration.ofMillis(100));
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

    private static WebElement getButtonOnHandicapOrTeamTotals(ChromeDriver driver, WebElement market, String selectionName, String bkBet) {
        try {
            var childNodes = (WebElement) ((JavascriptExecutor) driver).executeScript("""              
                return arguments[0].childNodes[1].childNodes[1];
                """, market);

            int goalIndex = bkBet.contains("P1") ? 0 : 1;

            // забираем все кнопки
            var buttons = childNodes.findElements(By.xpath("./child::*"));
            for (var b : buttons) {
                try {
                    // кнопки идут парами: левый - правыый
                    var twoButtons = b.findElements(By.xpath("./child::*"));
                    if (twoButtons.size() != 2) {
                        throw new RuntimeException("[pinnacle]: нарушена структура кнопок на странице, size = " + twoButtons.size());
                    }
                    return twoButtons.get(goalIndex).findElement(SeleniumSupport.buildLocalSpanByText(selectionName));
                } catch (NoSuchElementException ignored) { }
            }
            throw new RuntimeException("[pinnacle]: Не найдена кнопка: " + bkBet + " selectionName = " + selectionName);
        } catch (StaleElementReferenceException e) {
            throw new RuntimeException("[pinnacle]: Не найдена кнопка: " + bkBet + " selectionName = " + selectionName);
        }
    }

    private static WebElement getMarketOnTheFilter(ChromeDriver driver, By by) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            wait.pollingEvery(Duration.ofMillis(100));
            var market = wait.until(driver1 -> driver1.findElement(by));
            return SeleniumSupport.getParentByDeep(market, 2);
        } catch (TimeoutException | StaleElementReferenceException e) {
            throw new RuntimeException("[pinnacle]: Маркет не найден в фильтре");
        }
    }

    private static WebElement getMarket(ChromeDriver driver, By by) {
        // Первый раз всегда пытаемся найти на текущей странице, если не получилось, то уже по всем филтрам
        try {
            return getMarketOnTheFilter(driver, by);
        } catch (RuntimeException ignored) { }

        try {
            // имя класса начинается с "style_filterBarContent__"
            var filtersBar = driver.findElement(By.cssSelector("[class^='style_filterBarContent__']"));
            // забираем все фильтры
            var filters = filtersBar.findElements(By.tagName("button"));
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
            for (var filter : filters) {
                wait.until(ExpectedConditions.elementToBeClickable(filter)).click();
                try {
                    return getMarketOnTheFilter(driver, by);
                } catch (RuntimeException ignored) { }
            }
            throw new RuntimeException("[pinnacle]: Событие пропало со страницы");
        } catch (NoSuchElementException | StaleElementReferenceException | TimeoutException e) {
            throw new RuntimeException("[pinnacle] На странице отсутствуют элементы");
        }
    }

    private static void clickOnSeeMore(ChromeDriver driver, WebElement market) {
        try {
            WebDriverWait waitForSeeMore = new WebDriverWait(driver, Duration.ofSeconds(1));
            waitForSeeMore.pollingEvery(Duration.ofMillis(100));
            var seeMore = waitForSeeMore.until(driver1 -> market.findElement(By.xpath(".//span[contains(text(), 'See more')]")));
            waitForSeeMore.until(ExpectedConditions.elementToBeClickable(seeMore)).click();
        } catch (TimeoutException | StaleElementReferenceException ignored) {
            Context.log.info("[pinnacle] There isn`t 'See More'");
        }
    }
}