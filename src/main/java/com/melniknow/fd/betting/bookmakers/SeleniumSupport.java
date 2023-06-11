package com.melniknow.fd.betting.bookmakers;

import com.melniknow.fd.Context;
import com.melniknow.fd.domain.Bookmaker;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class SeleniumSupport {
    public static WebElement getParentByDeep(WebElement element, int deep) {
        for (var i = 0; i < deep; i++) element = element.findElement(By.xpath("./.."));
        return element;
    }

    public static By buildLocalSpanByText(String text) {
        return By.xpath(".//span[text()='" + text + "']");
    }

    public static By buildLocalH4ByText(String text) {
        return By.xpath(".//h4[text()='" + text + "']");
    }

    public static By buildGlobalSpanByText(String text) {
        return By.xpath("//span[text()='" + text + "']");
    }

    public static By buildGlobalH4ByText(String text) {
        return By.xpath("//h4[text()='" + text + "']");
    }

    public static void login(ChromeDriver driver, Bookmaker bookmaker) throws InterruptedException {
        var login = Context.betsParams.get(bookmaker).login();
        var password = Context.betsParams.get(bookmaker).password();

        switch (bookmaker) {
            case _188BET -> {
                driver.manage().window().setSize(new Dimension(1400, 1000));

                Context.botPool.submit(() -> driver.get(bookmaker.link));
                var wait = new WebDriverWait(driver, Duration.ofSeconds(120));

                var startButton = wait.until(driver_ -> driver_.findElement(By.xpath("//button/span[text()='Log in']/parent::button")));
                TimeUnit.SECONDS.sleep(10);
                wait.until(ExpectedConditions.elementToBeClickable(startButton));
                startButton.click();

                var loginInput = wait.until(driver1 -> driver1.findElement(By.id("UserIdOrEmail")));
                wait.until(ExpectedConditions.elementToBeClickable(loginInput));
                loginInput.click();
                loginInput.clear();
                loginInput.sendKeys(login);

                var passwordInput = wait.until(driver1 -> driver1.findElement(By.id("Password")));
                wait.until(ExpectedConditions.elementToBeClickable(passwordInput));
                passwordInput.click();
                passwordInput.clear();
                passwordInput.sendKeys(password);

                var button = wait.until(driver1 -> driver1.findElement(By.xpath("//button/span[text()='Log in ']/parent::button")));
                wait.until(ExpectedConditions.elementToBeClickable(button));
                TimeUnit.SECONDS.sleep(3);
                button.click();

                wait.until(driver1 -> driver1.findElement(By.xpath("//*[@id='s-app-bar']/div/div[3]/div[1]/ul/li[2]")));

                for (int i = 0; i < 5; i++) {
                    if (clickIfIsClickable(driver, By.xpath("//*[@id='s-app-bar']/div/nav/ul/li[1]/a")))
                        break;
                    Context.log.info("[188bet autoLogin] Пытаемся нажать на кнопку Sport");
                }
            } case PINNACLE -> {
                driver.get(bookmaker.link);
                var wait = new WebDriverWait(driver, Duration.ofSeconds(120));

                var loginInput = wait.until(driver1 -> driver1.findElement(By.xpath("//input[@id='username']")));
                wait.until(ExpectedConditions.elementToBeClickable(loginInput));
                loginInput.click();
                loginInput.clear();
                loginInput.sendKeys(login);

                var passwordInput = wait.until(driver1 -> driver1.findElement(By.xpath("//input[@id='password']")));
                wait.until(ExpectedConditions.elementToBeClickable(passwordInput));
                passwordInput.click();
                passwordInput.clear();
                passwordInput.sendKeys(password);

                var sendButton = wait.until(driver1 -> driver1.findElement(By.xpath("//button[text()='Log in']")));
                wait.until(ExpectedConditions.elementToBeClickable(sendButton));
                sendButton.click();

                var captchaButton = wait.until(driver1 -> driver1.findElement(By.xpath("//*[@id='loginRecaptcha']/div[2]/div[2]")));
                captchaButton.click();

                wait.until(driver1 -> driver1.findElement(By.xpath("//div[text()='Капча решена!' or text()='Captcha solved!']")));

                var button = wait.until(driver1 -> driver1.findElement(By.xpath("//button/span[text()='Log in']/parent::button")));

                wait.until(ExpectedConditions.elementToBeClickable(button));
                TimeUnit.SECONDS.sleep(3);

                button.click();
            }
            case BET365 -> driver.get(bookmaker.link);
        }
    }

    private static boolean clickIfIsClickable(ChromeDriver driver, By xpath) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        try {
            var button = wait.until(driver_ -> driver_.findElement(xpath));
            button.click();
            return true;
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted() || e.getCause() instanceof InterruptedException)
                throw new RuntimeException();
            return false;
        }
    }

    public static void clearBetSum(WebElement enterSumButton, String betSum) {
        for (int i = 0; i < betSum.length(); ++i) {
            enterSumButton.sendKeys(Keys.BACK_SPACE);
        }
    }

    public static void enterSum(ChromeDriver driver, By by, BigDecimal sum, String bkName) {
        try {
            for (int trying = 0; trying < 3; ++trying) {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                var enterSumButton = wait.until(driver_ -> driver_.findElement(by));
                for (int i = 0; i < 15; ++i) {
                    var betSum = enterSumButton.getAttribute("value");
                    Context.log.info("[" + bkName + "]: Перевводим сумму с " + betSum + " НА " + sum);
                    if (betSum.isEmpty()) {
                        break;
                    }
                    clearBetSum(enterSumButton, betSum);
                }
                enterSumButton.sendKeys(sum.toPlainString());
                var expectedSum = sum.toPlainString();
                String expectedSumWithComma = formatNumber(expectedSum);
                var realSum = enterSumButton.getAttribute("value");
                if (realSum.equals(expectedSum) || realSum.equals(expectedSumWithComma)) {
                    return;
                }
            }
            throw new RuntimeException(new AccountCuttingException("[%s]: Аккаунт порезан".formatted(bkName)));
        } catch (TimeoutException e) {
            throw new RuntimeException("[%s]: Ошибка при вводе суммы в купон - Не найдено поле ввода".formatted(bkName));
        }
    }

    public static String formatNumber(String number) {
        // Check if the number contains a decimal point
        int dotIndex = number.indexOf('.');
        String integerPart = number;
        String decimalPart = "";
        if (dotIndex >= 0) {
            integerPart = number.substring(0, dotIndex);
            decimalPart = number.substring(dotIndex + 1);
        }
        // Insert commas every three digits from the right
        StringBuilder sb = new StringBuilder();
        int len = integerPart.length();
        for (int i = 0; i < len; i++) {
            char c = integerPart.charAt(i);
            sb.append(c);
            if ((len - i - 1) % 3 == 0 && i < len - 1) {
                sb.append(',');
            }
        }
        // Append the decimal part if present
        if (!decimalPart.isEmpty()) {
            sb.append('.').append(decimalPart);
        }
        return sb.toString();
    }

    public static boolean fastContains(ChromeDriver driver, By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted() || e.getCause() instanceof InterruptedException)
                throw new RuntimeException();
            return false;
        }
    }
}
