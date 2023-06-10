package com.melniknow.fd.betting.bookmakers.bet365;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.betting.bookmakers.ShoulderInfo;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
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
import java.util.Arrays;

public class Bet365 implements IBookmaker {
    private WebElement curButton;
    private ChromeDriver driver;

    public Bet365() {
        this.curButton = null;
        this.driver = null;
    }

    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {
        this.driver = Context.screenManager.getScreenForBookmaker(bookmaker);

//        Context.log.info("Call openLink Bet365");
//        try {
//
//            driver.switchTo().window(driver.getWindowHandles().stream().findFirst().orElse(null));
//            driver.manage().window().setSize(new Dimension(1300, 1000));
//
//            if (!driver.getCurrentUrl().equals(info.BK_href())) driver.get(info.BK_href());
//        } catch (TimeoutException e) {
//            throw new RuntimeException("[Bet365]: Страница не загружается!");
//        }
    }
    @Override
    public BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sport sport, boolean isNeedToClick) {
        var name = info.BK_market_meta().get("name").getAsString();
        var names = Arrays.stream(name.split("\\|")).toList();
        if (names.isEmpty())
            throw new RuntimeException("[bet365] пустые метаданные");
        if (names.size() == 2) {
            return findButtonInSimpleTable(getMarketByMarketName(names.get(0)), names.get(1));
        } else if (names.size() == 3) {
            return findButtonInTable(getMarketByMarketName(names.get(0)), names.get(1), names.get(2));
        } else {
            throw new RuntimeException("[bet365]: неподдерживаемая ставка: " + names);
        }
    }

    private static final By byAccepChanges = By.xpath("//div[text()='Accept Change']");
    private static final By byPlaceBet = By.xpath("//div[text()='Place Bet']");
    private static final By byAccepChangeAndPlaceBet = By.xpath("//div[text()='Accept Change and']");
    private static final By byEnterSum = By.cssSelector("[placeholder='Set Stake']");
    private static final By byBetSuccess = By.className("bss-ReceiptContent_Done");
    private static final By byCloseBet = By.className("bss-RemoveButton");
    private static final By byBetClosed = By.className("TODO");

    @Override
    public BigDecimal placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info, ShoulderInfo shoulderInfo, BigDecimal sum) {

//        Context.log.info("Call placeBetAndGetRealCf Bet365");
//
//        if (sum.compareTo(new BigDecimal("1")) < 0) {
//            throw new RuntimeException("[bet365]: Не ставим ставки меньше 1, sum = " + sum);
//        }
//
//        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
//        try {
//            this.curButton.click();
//            enterSum(driver, sum);
//            // TODO rm prev windows
//            var realCf = waitLoop(driver, info.BK_name(), info.BK_cf(), shoulderInfo);
//            closeAfterSuccess(driver);
//            return realCf;
//        } catch (StaleElementReferenceException e) {
//            throw new RuntimeException("[bet365]: событие пропало со страницы (не смогли нажать на кнопку)");
//        } catch (RuntimeException e) {
//            throw new RuntimeException(e.getMessage());
//        }
        return null;
    }

    private BigDecimal waitLoop(String bkName, BigDecimal oldCf, ShoulderInfo shoulderInfo) {
        var isFirstClick = true;
        for (int i = 0; i < 15; ++i) {
            updateOdds(bkName, oldCf, shoulderInfo, isFirstClick);
            isFirstClick = false;
            if (waitSuccess()) {
                return getCurrentCf(driver);
            }
        }
        throw new RuntimeException("[bet365]: Плечо не может быть проставлено - не можем дождаться обработки ставки(1)");
    }

    private boolean waitSuccess() {
        for (int i = 0; i < 30; ++i) {
            try {
                Context.log.info("[bet365]: Wait....");
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
                wait.pollingEvery(Duration.ofMillis(100));
                wait.until(driver1 -> driver1.findElement(byBetSuccess));
                return true;
            } catch (Exception e) {
                // После попытки подождать чекаем ничего ли не произошло пока мы ждали?
                if (SeleniumSupport.windowContains(driver, byAccepChanges) || SeleniumSupport.windowContains(driver, byPlaceBet)) { // TODO add closed bet handle
                    Context.log.info("[bet365]: Exit from wait");
                    // что-то появилось, ждать бесполезно идём нажимать на новую кнопку
                    return false;
                }
            }
        }
        Context.log.info("[bet365]: wait stop!");
        throw new RuntimeException("[bet365]: Плечо не может быть проставлено - не можем дождаться обработки ставки(2)");
    }

    private void updateOdds(String bkName, BigDecimal oldCf, ShoulderInfo shoulderInfo, boolean isFirstClick) {
        if (!isFirstClick) {
            // Ставка закрыта?
            if (SeleniumSupport.windowContains(driver, byBetClosed)) {
                throw new RuntimeException("[bet365]: Ставка закрыта");
            }
        }
        var curCf = getCurrentCf(driver);
        var inaccuracy = new BigDecimal("0.01");

        if (curCf.add(inaccuracy).setScale(2, RoundingMode.DOWN).compareTo(oldCf.setScale(2, RoundingMode.DOWN)) >= 0) {
            Context.log.info("[bet365]: Click Place 1");
            for (int i = 0; i < 10; ++i) {
                if (clickIfIsClickable()) {
                    return;
                }
            }
        } else if (!shoulderInfo.isFirst()) { // Мы второе плечо - пересчитываем и пытаемся перекрыться если коэф упал
            var newIncome = MathUtils.calculateIncome(curCf, shoulderInfo.cf1());
            Context.log.info("[bet365]: newIncome = " + newIncome);
            if (newIncome.compareTo(Context.parserParams.maxMinus()) < 0) { // если превысили максимальный минус
                Context.log.info("[bet365]: Max minus: newIncome = " + newIncome);
                throw new RuntimeException("[bet365]: превышен максимальный минус: maxMinus = " + Context.parserParams.maxMinus() + ", а текущий минус = " + newIncome);
            } else {
                // забираем наши валюты
                var currencySecondShoulder = Context.currencyToRubCourse.get(Context.betsParams.get(BetUtils.getBookmakerByNameInApi(bkName)).currency());
                var currencyFirstShoulder = Context.currencyToRubCourse.get(Context.betsParams.get(BetUtils.getBookmakerByNameInApi(shoulderInfo.bk1Name())).currency());

                var scale = Context.betsParams.get(BetUtils.getBookmakerByNameInApi(bkName)).accuracy().intValue();

                // считаем новую сумму
                var newSum = shoulderInfo.cf1()
                    .multiply(shoulderInfo.sum1().multiply(currencyFirstShoulder))
                    .divide(curCf, 2, RoundingMode.DOWN)
                    .divide(currencySecondShoulder, scale, RoundingMode.DOWN);

                Context.log.info("[bet365]: newSum = " + newSum + " | with cf = " + curCf);
                enterSum(driver, newSum);

                for (int i = 0; i < 10; ++i) {
                    Context.log.info("[bet365]: Click Place 2");
                    if (clickIfIsClickable()) {
                        return;
                    }
                }
            }
        } else {
            throw new RuntimeException("[bet365]: Коэфициент на первом плече упал. Было - " + oldCf + " стало - " + curCf);
        }
    }

    private boolean clickIfIsClickable() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        wait.pollingEvery(Duration.ofMillis(100));
        try {
            var button = wait.until(driver_ -> driver_.findElement(byPlaceBet));
            wait.until(ExpectedConditions.elementToBeClickable(button));
            driver.executeScript("arguments[0].click();", button);
            return true;
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted() || e.getCause() instanceof InterruptedException)
                throw new RuntimeException();
            return false;
        }
    }

    private BigDecimal getCurrentCf(ChromeDriver driver) {
        try {
            return new BigDecimal(driver.findElement(By.className("bss-NormalBetItem_OddsContainer")).getText());
        } catch (TimeoutException | StaleElementReferenceException | NullPointerException e) {
            throw new RuntimeException("[bet365]: событие пропало со страницы - не получилось получить коэффициент");
        }
    }

    private void enterSum(ChromeDriver driver, BigDecimal sum) {
        try {
            for (int trying = 0; trying < 3; ++trying) {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
                var enterSumButton = wait.until(driver_ -> driver_.findElement(byEnterSum));
                for (int i = 0; i < 15; ++i) {
                    var betSum = enterSumButton.getText();
                    Context.log.info("[bet365]: Перевводим сумму с " + betSum + " НА " + sum);
                    if (betSum.isEmpty()) {
                        break;
                    }
                    SeleniumSupport.clearBetSum(enterSumButton, betSum);
                }
                enterSumButton.sendKeys(sum.toPlainString());
                if (enterSumButton.getText().equals(sum.toPlainString())) {
                    return;
                }
            }
            throw new RuntimeException("[bet365]: бот не смог очистить поле ввода");
        } catch (TimeoutException e) {
            throw new RuntimeException("[bet365]: Ошибка при вводе суммы в купон - Не найдено поле ввода");
        }
    }

    private void closeAfterSuccess(ChromeDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.pollingEvery(Duration.ofMillis(100));
            var close = wait.until(driver1 -> driver1.findElement(byCloseBet));
            wait.until(ExpectedConditions.elementToBeClickable(close)).click();
        } catch (Exception e) {
            Context.log.info("[bet365]: Не смогли закрыть купон после успешной ставки. Ошибка: " + e.getMessage());
        }
    }

    private WebElement getMarketByMarketName(String marketName) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.pollingEvery(Duration.ofMillis(100));
            var elem = wait.until(driver1 -> driver1.findElement(By.xpath("//div[text()='%s']".formatted(marketName))));
            Context.log.info("[bet365]: Find market");
            ((JavascriptExecutor) driver).executeScript("""
                arguments[0].scrollIntoView()
                window.scrollBy(0, -100)
                """, elem);
            return SeleniumSupport.getParentByDeep(elem, 2);
        } catch (Exception e) {
            var message = "[bet365]: Не найден маркет " + marketName;
            Context.log.info(message);
            throw new RuntimeException(message);
        }
    }

    private BigDecimal findButtonInSimpleTable(WebElement market, String buttonName) {
        return null;
    }

    private BigDecimal findButtonInTable(WebElement market, String rowName, String columnName) {
        var subMarket = market.findElements(By.xpath("./child::*")).get(1);
        var table = subMarket.findElements(By.xpath("./child::*")).get(0);
        var column = getColumnByName(table, columnName);
        var firstColumn = table.findElements(By.xpath("./child::*")).get(0);
        this.curButton = getButtonInColumnByLineIndex(column, getIndexOfCorrectLineInColumn(firstColumn, rowName));
        return getCfFromButton(curButton);
    }

    private WebElement getColumnByName(WebElement table, String columnName) {
        var columns = table.findElements(By.xpath("./child::*"));
        for (var col : columns) {
            if (containsName(col, By.xpath(".//div[text()='%s']".formatted(columnName))))
                return col;
        }
        throw new RuntimeException("[bet365] не найден нужный столбик в таблице событий");
    }

    private int getIndexOfCorrectLineInColumn(WebElement column, String lineName) {
        var rows = column.findElements(By.xpath("./child::*"));
        int index = 0;
        for (var row : rows) {
            if (containsName(row, By.xpath(".//div[text()='%s']".formatted(lineName))))
                return index;
            index++;
        }
        throw new RuntimeException("[bet365] не найдена нужная строка в таблице событий");
    }

    private WebElement getButtonInColumnByLineIndex(WebElement column, int lineIndex) {
        var rowsInColumn = column.findElements(By.xpath("./child::*"));
        return rowsInColumn.get(lineIndex);
    }

    private BigDecimal getCfFromButton(WebElement button) {
        if (button.getText().contains("\n"))
            return new BigDecimal(button.getText().split("\n")[1]);
        return new BigDecimal(button.getText());
    }

    private boolean containsName(WebElement elem, By by) {
        try {
            elem.findElement(by);
            return true;
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted() || e.getCause() instanceof InterruptedException)
                throw new RuntimeException();
            return false;
        }
    }

    private BigDecimal findButtonInAsianHandicapTable(WebElement market, String columnName, String rowName) {
        var subMarket = market.findElements(By.xpath("./child::*")).get(1);
        var table = subMarket.findElements(By.xpath("./child::*")).get(0);
        var column = getColumnByName(table, columnName);
        this.curButton = getButtonInColumnByName(column, rowName);
        return getCfFromButton(curButton);
    }

    private WebElement getButtonInColumnByName(WebElement column, String lineName) {
        var rows = column.findElements(By.xpath("./child::*"));
        for (var row : rows) {
            if (containsName(row, By.xpath(".//span[text()='%s']".formatted(lineName))))
                return row;
        }
        throw new RuntimeException("[bet365] не найдена нужная строка в таблице событий");
    }
}























