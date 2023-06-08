package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Currency;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BetsSupport {

    public static String curCashOutField;

    public static String getTotalsByStr(String str) {
        return str.split("\n")[1];
    }

    public static String getPartOfGameByMarketName(String marketName) {
        // После "-" идёт часть матча, если это ставка на весь матч, то вернём пустую строку
        if (marketName.contains(" - ")) {
            return marketName.split(" - ")[1];
        } else {
            return "";
        }
    }

    public static String getFirstNameForCashOut(String title) {
        // Мы до этого сохраняли в curCashOutField строку. При кешауте они к этой строке ещё одну добавляют
        // поэтому мы отрезаем ее
        if (!title.contains(" - ")) {
            return title;
        }
        if (title.split(" - ", -1).length - 1 == 1) {
            return title.substring(0, title.indexOf(" - "));
        }
        return null;
    }

    // using for Home
    public static String getTeamFirstNameByTitle(String title) {
        if (title.split("vs", -1).length - 1 == 1) {
            return title.substring(0, title.indexOf("vs") - 1);
        }
        return null;
    }

    // using for Away
    public static String getTeamSecondNameByTitle(String title) {
        if (title.split("vs", -1).length - 1 == 1) {
            return title.substring(title.indexOf("vs") + 3);
        }
        return null;
    }

    /***
     * @return лист нужных кнопок, но если маркет "свернут", то функция нажмёт на него и ещё раз попытается забрать кнопки
     */
    public static List<WebElement> findElementsWithClicking(WebElement element, By by) throws InterruptedException {
        List<WebElement> res;
        try {
            res = element.findElements(by);
            return res;
        } catch (NoSuchElementException e) {
            try {
                element.click();
                TimeUnit.MILLISECONDS.sleep(500);
                return element.findElements(by);
            } catch (StaleElementReferenceException | ElementNotInteractableException e1) {
                throw new RuntimeException("[188bet]: Блок с событиями пропал со страницы");
            }
        }
    }

    public static boolean isPureMarket(WebElement market) {
        if (market.getText().contains("\n") && market.getText().contains("Current Total"))
            return true;
        return !market.getText().contains("\n");
    }

    public static void closeBetWindow(ChromeDriver driver) {
        try {
            // Ищем '@', выходим на один уровень вверх(как при получении коэффициента)
            var wait = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                driver_ -> driver_.findElement(By.xpath("//span[text()='@']")));
            var tmp = SeleniumSupport.getParentByDeep(wait, 1);
            // Нужно нажать на крестик - он "Брат" нашей строки - таким образо получаем следующий элемент в иерархии
            tmp.findElement(By.xpath(".//following::div[1]")).click();
        } catch (NoSuchElementException | TimeoutException | StaleElementReferenceException |
                 ElementNotInteractableException e) {
            Context.log.info("[188bet]: Не закрыли окошко с купоном");
        }
    }

    public static void clearPreviousBets(ChromeDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            ((JavascriptExecutor) driver).executeScript("""
                try {
                    var el = document.querySelector('[data-btn-remove-all="true"]')
                    el.click()
                }
                catch(Exception) {}
                """);
            TimeUnit.MILLISECONDS.sleep(1000);
            wait.until((ExpectedConditions.elementToBeClickable(By.cssSelector("[data-btn-trash-can='true']")))).click();
            TimeUnit.MILLISECONDS.sleep(800);
            wait.until((ExpectedConditions.elementToBeClickable(By.cssSelector("[data-btn-remove-all='true']")))).click();
            TimeUnit.MILLISECONDS.sleep(800);
        } catch (NoSuchElementException | StaleElementReferenceException |
                 ElementNotInteractableException ignored) {
        } catch (InterruptedException | TimeoutException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static BigDecimal betCorrectBalance(Bookmaker bookmaker, ChromeDriver driver, Currency currency) throws InterruptedException {
        // в этом цикле ждём прогрузки баланса
        try {
            for (int i = 0; i < 20; ++i) {
                var balanceButton = new WebDriverWait(driver, Duration.ofSeconds(5)).until(driver1
                    -> driver1.findElement(By.className("print:text-black/80")).getText()); // "print:text-black/80" - принадлежит окошку с балансом
                if (balanceButton != null && !balanceButton.isEmpty()) {
                    balanceButton = balanceButton.substring(4); // откусываем название валюты и пробел
                    balanceButton = balanceButton.replace(",", "");
                    var balance = new BigDecimal(balanceButton);
                    if (!balance.equals(BigDecimal.ZERO)) {
                        return balance.multiply(Context.currencyToRubCourse.get(currency));
                    }
                }
                // спим, ждём прогрузки
                Context.log.info("[188bet]: Waiting balance...");
                TimeUnit.MILLISECONDS.sleep(500);
            }
        } catch (TimeoutException e) {
            SeleniumSupport.login(driver, bookmaker);
            throw new RuntimeException("[188bet]: Мы вошли в аккаунт");
        }
        throw new RuntimeException("[188bet] Мы вошли в аккаунт");
    }

    public static BigDecimal getCurrentCf(ChromeDriver driver) {
        try {
            // @ - разделяет название события и коэфициент
            var wait = new WebDriverWait(driver, Duration.ofSeconds(5)).pollingEvery(Duration.ofMillis(100));
            var tmpTitle = wait.until(driver1 -> driver1.findElement(SeleniumSupport.buildGlobalSpanByText("@")));
            // сама @ нам не нужна, выходим на один уровень вверх, чтобы взять всю строку(в конце которой коэффициент)
            var title = SeleniumSupport.getParentByDeep(tmpTitle, 1).getText();
            // берём всё, что после @ - наш коэффициент
            return new BigDecimal(title.substring(title.indexOf("@") + 1));
        } catch (TimeoutException | StaleElementReferenceException e) {
            throw new RuntimeException("[188bet]: коэффициент события не нейден");
        }
    }

    public static void closeAfterSuccessfulBet(ChromeDriver driver) {
        try {
            var wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            var tmpButton = wait.until(driver_ -> driver_.findElement(SeleniumSupport.buildGlobalSpanByText("@")));

            // Очент важно! На случай кешайта сохраняем строчки, с помощью которых мы будем искать блок
            var tmp = tmpButton;
            tmp = SeleniumSupport.getParentByDeep(tmp, 2);
            curCashOutField = tmp.getText();

            tmpButton = SeleniumSupport.getParentByDeep(tmpButton, 7);
            try {
                tmpButton.findElement(SeleniumSupport.buildLocalH4ByText("OK")).click();
            } catch (NoSuchElementException e) {
                tmpButton.findElement(SeleniumSupport.buildLocalH4ByText("Ok")).click();
            }
        } catch (TimeoutException | NoSuchElementException | StaleElementReferenceException |
                 ElementNotInteractableException e) {
            Context.log.info("Not Close mini-window after success betting!  [188bet]");
        }
    }

    public static boolean cashOut(ChromeDriver driver) throws InterruptedException {
        // забираем наши строчки, которые ранее сохранили
        var originalLines = curCashOutField.lines().toList();
        // их минимум 4
        if (originalLines.size() < 4) {
            return false;
        }
        // по этой строке будем искать наши блоки
        var findStr = getFirstNameForCashOut(originalLines.get(1));
        if (findStr == null) {
            return false;
        }

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            // ищем Bet Slip (он по центру)
            var button = wait.until(driver1 -> driver1.findElement(SeleniumSupport.buildGlobalH4ByText("Bet Slip")));
            var footer = SeleniumSupport.getParentByDeep(button, 2);
            // ищем My Bets в footer-е (на странице ещё бывают такие надписи, поэтому мы ищем обязательно в footer-е и юзаем buildLocalH4ByText, тк ищем локально)
            footer.findElement(SeleniumSupport.buildLocalH4ByText("My Bets")).click();
            // ждём ебаную анимацию после клика
            TimeUnit.SECONDS.sleep(2);
            // там пробелы есть где-то поэтому сравниваем строки без пробелов
            var blocks = wait.until(driver1 -> driver1.findElements(
                By.xpath("//span[contains(translate(text(),' ',''),'" + findStr.replaceAll("\\s+", "") + "')]")));

            // Теперь в цикле надо найти именно наш блок
            for (var t : blocks) {
                // В этой строке бы забрали строки, которые сейчас на экране в этом блоке(там название команды, тип ставки, кожффициент и тд)
                var curLines = SeleniumSupport.getParentByDeep(t, 2).getText().lines().toList();
                // Теперь строки, которые мы сохранили до этого сравниваем с тем, что видим на экране
                // Соответствие именно такое 1 == 2, 2 == 3
                if (curLines.get(1).equals(originalLines.get(2)) && curLines.get(2).equals(originalLines.get(3))) {
                    // нашли! теперь надо нажать на кнопку - выходим на 5 вверх
                    var finalBlock = SeleniumSupport.getParentByDeep(t, 5);
                    // Опять же ищем локально в этом блоке нужый текст с помощью buildLocalH4ByText
                    var finalButton = wait.until(driver1 -> finalBlock.findElement(SeleniumSupport.buildLocalH4ByText("Cash Out")));
                    // на всякий случай ждём elementToBeClickable
                    wait.until(ExpectedConditions.elementToBeClickable(finalButton));

                    driver.executeScript("arguments[0].click();", finalButton);
                    TimeUnit.MILLISECONDS.sleep(1000);
                    // Нужно поддтвердить Cash Out
                    var finalButton2 = finalBlock.findElement(SeleniumSupport.buildLocalH4ByText("Confirm Cash Out"));
                    driver.executeScript("arguments[0].click();", finalButton2);
                    // Кликнули! всё, выходим
                    return true;
                }
            }
            // Если блоков нет, то даже скроллить не будем, тк Cash Out мы делаем сразу после ставки и он обязан быть первый в списке вверху страницы
            return false;
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } catch (Exception e) {
            return false;
        }
    }

    public static WebElement getMarketByMarketName(ChromeDriver driver, String marketName, String marketSubName) {
        WebElement el;
        Actions actions = new Actions(driver);

        var yPos = 0L;
        for (var i = 0; i < 20; i++) {
            try {
                var els = driver.findElements(By.xpath("//h4[text()='%s']".formatted(marketName)));
                if (els.isEmpty() && marketSubName != null)
                    els = driver.findElements(By.xpath("//h4[text()='%s']".formatted(marketName + " - " + marketSubName)));
                for (var e : els) {
                    el = e;
                    var parent = (WebElement) ((JavascriptExecutor) driver).executeScript(
                        "return arguments[0].parentNode.parentNode;", el);

                    var flag = false;

                    if (isPureMarket(parent) && marketSubName == null) {
                        flag = true;
                    } else if (isPureMarket(parent) && marketSubName != null && !marketSubName.isEmpty()) {
                        flag = parent.getText().toLowerCase().split("\n")[0].equals(marketName.toLowerCase() + " - " + marketSubName.toLowerCase());
                    } else {
                        flag = parent.getText().split("\n")[1].equals(marketSubName);
                    }

                    if (flag) {
                        ((JavascriptExecutor) driver).executeScript("""
                            arguments[0].scrollIntoView()
                            window.scrollBy(0, -100)
                            """, parent);
                        return SeleniumSupport.getParentByDeep(el, 5);
                    }
                }
            } catch (Exception ignored) { }

            actions.scrollByAmount(0, 300).perform();

            var temp = (Long) ((JavascriptExecutor) driver).executeScript("return window.pageYOffset;");
            if (yPos == temp) break;
            yPos = temp;
        }
        throw new RuntimeException("[188bet]: Не найден маркет - " + marketName + " : " + marketSubName);
    }

    public static ArrayList<String> getCorrectMarketData(String marketName) {
        if (marketName.contains(" - ")) {
            var ind = marketName.lastIndexOf(" - ");
            return new ArrayList<>() {{
                add(marketName.substring(0, ind));
                add(marketName.substring(ind + 3));
            }};
        } else {
            return new ArrayList<>() {{
                add(marketName);
                add(null);
            }};
        }
    }
    public static String getCorrectSelectionName(String selectionName, String game) {
        if (selectionName.equals("Home")) return game.split(" vs ")[0];
        else if (selectionName.equals("Away")) return game.split(" vs ")[1];
        return selectionName;
    }

    public static boolean equalsForLine(String totalOrHandicap, String line, String handicap) {
        if (handicap == null) return totalOrHandicap.equals(line);
        else {
            if (line.equals("0")) return totalOrHandicap.equals("0");
            var sign = handicap.startsWith("-") ? "-" : "+";
            return totalOrHandicap.equals(sign + line);
        }
    }
}