package com.melniknow.fd.betting.bookmakers._188bet;

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
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.melniknow.fd.betting.bookmakers._188bet.BetsSupport.*;

public class _188Bet implements IBookmaker {

    private WebElement curButton;

    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {
        curButton = null;

        Context.log.info("Call openLink _188Bet");
        try {
            var driver = Context.screenManager.getScreenForBookmaker(bookmaker);

            driver.manage().window().setSize(new Dimension(1000, 1000));
            driver.get(info.BK_href().replace("https://sports.188sbk.com", "https://sports.188bet-sports.com") + "?c=207&u=https://www.188bedt.com");
        } catch (TimeoutException ignored) {
            throw new RuntimeException("[188bet]: Страница не загружается!");
        }
    }

    @Override
    public BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sport sport, boolean isNeedToClick) throws InterruptedException {
        Context.log.info("Call clickOnBetTypeAndReturnBalanceAsRub _188Bet");

        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);

        var wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.pollingEvery(Duration.ofMillis(100));

        TimeUnit.SECONDS.sleep(5);
        var header = wait.until(driver1 -> driver1.findElement(By.xpath("//*[@id='app']/div/div[1]/div[2]/div/div[1]/div[2]/div/div[1]/div[2]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].remove() ", header);

        var marketData = getCorrectMarketData(info.BK_market_meta().get("marketName").getAsString());

        var marketName = marketData.get(0);
        var marketSubName = marketData.get(1);
        var selectionName = getCorrectSelectionName(info.BK_market_meta().get("selectionName").getAsString(), info.BK_game());

        Context.log.info("\n[188bet]\n" +
            "marketName = " + marketName + "\n" +
            "marketSubName = " + marketSubName + "\n" +
            "selectionName = " + selectionName + "\n");

        var market = BetsSupport.getMarketByMarketName(driver, marketName, marketSubName);

        var line = info.BK_market_meta().has("line") ?
            info.BK_market_meta().get("line").getAsString() :
            null;

        var handicap = info.BK_bet().contains("HANDICAP") ?
            extractContentInBrackets(info.BK_bet()).get(0) :
            null;

        Context.log.info("\n[188bet]\n" +
            "line = " + line + "\n" +
            "handicap = " + handicap + "\n\n");


        var buttons = BetsSupport.findElementsWithClicking(market,
                By.xpath(".//div[translate(text(),' ','') = '" + selectionName.replaceAll("\\s+", "") + "']"))
            .stream()
            .map(e -> {
                try {
                    return e.findElement(By.xpath("./.."));
                } catch (StaleElementReferenceException e1) {
                    throw new RuntimeException("[188bet]: Событие пропало со страницы");
                }
            })
            .toList();

        try {
            curButton = Objects.requireNonNull(buttons.stream().filter(
                b -> {
                    var cfText = line == null ?
                        SeleniumSupport.getParentByDeep(b, 1).getText().split("\n")[1] :
                        SeleniumSupport.getParentByDeep(b, 1).getText().split("\n")[2];

                    var inaccuracy = new BigDecimal("0.01");
                    var inaccuracy2 = new BigDecimal("0.05");
                    var curCf = new BigDecimal(cfText);

                    return !(curCf.add(inaccuracy).setScale(2, RoundingMode.DOWN).compareTo(info.BK_cf().setScale(2, RoundingMode.DOWN)) < 0 ||
                        curCf.subtract(inaccuracy2).setScale(2, RoundingMode.DOWN).compareTo(info.BK_cf().setScale(2, RoundingMode.DOWN)) > 0) ||
                        line == null ||
                        equalsForLine(BetsSupport.getTotalsByStr(b.getText()), line, handicap);
                }).findFirst().orElse(null));
        } catch (NullPointerException | StaleElementReferenceException |
                 ElementNotInteractableException | IndexOutOfBoundsException e) {
            throw new RuntimeException("[188bet]: Событие пропало со страницы");
        }

        return BetsSupport.betCorrectBalance(bookmaker, driver, Context.betsParams.get(bookmaker).currency());
    }

    public static List<String> extractContentInBrackets(String input) {
        var result = new ArrayList<String>();
        var pattern = Pattern.compile("\\((.*?)\\)");
        var matcher = pattern.matcher(input);

        while (matcher.find()) result.add(matcher.group(1));

        return result;
    }

    // это 4 состояния, в которых мы можем пребывать когда пытаемся поставить ставку
    private static final By byAccepChanges = By.xpath("//h4[text()='Accept Changes']");
    private static final By byPlaceBet = By.xpath("//h4[text()='Place Bet']");
    private static final By byClosedBet = By.xpath("//h4[text()='One or more of your selections are closed for betting.']");
    private static final By bySuccessBet = By.xpath("//h4[text()='Your bet has been successfully placed.']");

    @Override
    public BigDecimal placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info, ShoulderInfo shoulderInfo, BigDecimal sum) {
        Context.log.info("Call placeBetAndGetRealCf _188Bet");

        if (sum.compareTo(new BigDecimal("50")) < 0) {
            throw new RuntimeException("[188bet]: Минимальная ставка на 188bet - 50, а бот пытается поставить: " + sum);
        }

        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
        try {
            this.curButton.click();
            try {
                SeleniumSupport.enterSum(driver, By.cssSelector("[placeholder='Enter Stake']"), sum, "188bet");
            } catch (RuntimeException e) {
                BetsSupport.clearPreviousBets(driver);
                this.curButton.click();
                SeleniumSupport.enterSum(driver, By.cssSelector("[placeholder='Enter Stake']"), sum, "188bet");
            }
            // от сюда мы выёдем только если поставили
            waitLoop(driver, info.BK_name(), info.BK_cf(), shoulderInfo);

            // тут мы забираем коэффициент после успешной ставки
            var realCf = BetsSupport.getCurrentCf(driver);
            // чтобы закрыть окошко мы нажимаем на "ОК", тк крестик после успешной ставки пропадает
            BetsSupport.closeAfterSuccessfulBet(driver);
            Context.log.info("[188bet]: Final cf = " + realCf);
            return realCf;
        } catch (StaleElementReferenceException e) {
            throw new RuntimeException("[188bet]: событие пропало со страницы (не смогли нажать на кнопку)");
        } catch (RuntimeException e) {
            BetsSupport.closeBetWindow(driver);
            Context.log.info("[188bet]: Don`t Place Bet" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (InterruptedException e) {
            BetsSupport.closeBetWindow(driver);
            throw new RuntimeException("[188bet]: не смогли уснуть после AccepChanges");
        }
    }

    private void waitLoop(ChromeDriver driver, String bkName, BigDecimal oldCf, ShoulderInfo shoulderInfo) throws InterruptedException {
        // в цикле - жмём на кнопку - пытаемся подождать результата
        var isFirstClick = true;
        for (int i = 0; i < 25; ++i) {
            updateOdds(driver, bkName, oldCf, shoulderInfo, isFirstClick);
            isFirstClick = false;
            if (waitSuccess(driver)) {
                Context.log.info("188BET SUCCESS");
                return;
            }
        }
        Context.log.info("[188bet]: wait stop!");
        throw new RuntimeException("[188bet]: Плечо не может быть проставлено");
    }

    private boolean waitSuccess(ChromeDriver driver) {
        // вечно ждать нельзя!
        for (int i = 0; i < 30; ++i) {
            try {
                Context.log.info("[188bet]: Wait....");
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
                wait.pollingEvery(Duration.ofMillis(100));
                wait.until(driver1 -> driver1.findElement(bySuccessBet));
                return true;
            } catch (Exception e) {
                if (Thread.currentThread().isInterrupted() || e.getCause() instanceof InterruptedException)
                    throw new RuntimeException("[188bet]: Не можем дождаться постановки ставки");
                // Пока мы ждали ничего не произошло? (ставка могла закрыться или поменяться коэфы и тд)
                if (windowContains(driver, byAccepChanges) || windowContains(driver, byPlaceBet) || windowContains(driver, byClosedBet)) {
                    Context.log.info("[188bet]: Exit from wait");
                    // ждать нечего - выходим, чтобы снова нажимать на кнопку
                    return false;
                }
            }
        }
        throw new RuntimeException("[188bet]: Плечо не может быть проставлено");
    }

    private void updateOdds(ChromeDriver driver, String bkName, BigDecimal oldCf, ShoulderInfo shoulderInfo, boolean isFirstClick) throws InterruptedException {
        // чекаем, мейби коэфы поменялись
        if (!isFirstClick) {
            if (clickIfIsClickable(driver, byAccepChanges)) {
                Context.log.info("[188bet]: Click byAccepChanges");
                TimeUnit.MILLISECONDS.sleep(300);
            }
            // чекаем, мейби ставка вовсе закрыта
            if (windowContains(driver, byClosedBet)) {
                Context.log.info("[188bet]: Событие закрыто");
                if (!shoulderInfo.isFirst()) {
                    int i = 0;
                    while (true) {
                        if (windowContains(driver, byClosedBet)) {
                            try {
                                Context.log.info("[188bet]: Ждём открытия события");
                                if (++i == 30)
                                    throw new RuntimeException("[188bet]: Событие закрыто");
                                TimeUnit.SECONDS.sleep(2);
                            } catch (InterruptedException e) {
                                throw new RuntimeException("[188bet]: Событие закрыто");
                            }
                        } else {
                            Context.log.info("[188bet]: Дождались открытия!");
                            break;
                        }
                    }
                } else {
                    throw new RuntimeException("[188bet]: Событие закрыто");
                }
            }
        }
        // если всё ок, то получаем коэф
        var curCf = BetsSupport.getCurrentCf(driver);
        Context.log.info("[188bet]: curCf = " + curCf);
        var inaccuracy = new BigDecimal("0.01");
        if (curCf.add(inaccuracy).setScale(2, RoundingMode.DOWN).compareTo(oldCf.setScale(2, RoundingMode.DOWN)) >= 0) {
            Context.log.info("[188bet]: Click Place 1");
            // кликаем на PlaceBet
            clickIfIsClickable(driver, byPlaceBet);
        } else if (!shoulderInfo.isFirst()) { // Мы второе плечо - пересчитываем и пытаемся перекрыться если коэф упал
            var newIncome = MathUtils.calculateIncome(curCf, shoulderInfo.cf1());
            Context.log.info("[188bet] newIncome = " + newIncome);
            if (newIncome.compareTo(Context.parserParams.maxMinus()) < 0) {
                BetsSupport.closeBetWindow(driver);
                throw new RuntimeException("[188bet]: превышен максимальный минус: maxMinus = " + Context.parserParams.maxMinus() + ", а текущий минус = " + newIncome);
            } else {
                Context.log.info("[188bet]: Click Place 2");
                // забираем наши валюты
                var currencySecondShoulder = Context.currencyToRubCourse.get(Context.betsParams.get(BetUtils.getBookmakerByNameInApi(bkName)).currency());
                var currencyFirstShoulder = Context.currencyToRubCourse.get(Context.betsParams.get(BetUtils.getBookmakerByNameInApi(shoulderInfo.bk1Name())).currency());

                var scale = Context.betsParams.get(BetUtils.getBookmakerByNameInApi(bkName)).accuracy().intValue();

                // считаем новую сумму
                var newSum = shoulderInfo.cf1()
                    .multiply(shoulderInfo.sum1().multiply(currencyFirstShoulder))
                    .divide(curCf, 2, RoundingMode.DOWN)
                    .divide(currencySecondShoulder, scale, RoundingMode.DOWN);

                Context.log.info("[188bet]: newSum = " + newSum + " | with cf = " + curCf);

                SeleniumSupport.enterSum(driver, By.cssSelector("[placeholder='Enter Stake']"), newSum, "188bet");

                // кликаем на PlaceBet
                clickIfIsClickable(driver, byPlaceBet);
            }
        } else {
            throw new RuntimeException("[188bet]: Коэфициент на первом плече упал");
        }
    }

    private static boolean clickIfIsClickable(ChromeDriver driver, By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        try {
            var button = wait.until(driver_ -> driver_.findElement(by));
            driver.executeScript("arguments[0].click();", button);
            return true;
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted() || e.getCause() instanceof InterruptedException)
                throw new RuntimeException();
            return false;
        }
    }

    // Узнать содержится ли эелемент на текущей странице
    private static boolean windowContains(ChromeDriver driver, By by) {
        try {
            var wait = new WebDriverWait(driver, Duration.ofMillis(300));
            wait.until(
                driver1 -> driver1.findElement(by));
            return true;
        } catch (TimeoutException | NoSuchElementException ignored) {
            return false;
        }
    }
}
