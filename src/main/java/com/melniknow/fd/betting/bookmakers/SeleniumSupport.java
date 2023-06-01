package com.melniknow.fd.betting.bookmakers;

import com.melniknow.fd.Context;
import com.melniknow.fd.domain.Bookmaker;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SeleniumSupport {
    public static WebElement getParentByDeep(WebElement element, int deep) {
        for (var i = 0; i < deep; i++) element = element.findElement(By.xpath("./.."));
        return element;
    }

    public static By buildLocalSpanByText(String text) {
        return By.xpath(".//span[text()='" + text + "']");
    }

    public static By buildLocalDivByText(String text) {
        return By.xpath(".//div[text()='" + text + "']");
    }

    public static By buildLocalH4ByText(String text) {
        return By.xpath(".//h4[text()='" + text + "']");
    }

    public static By buildGlobalSpanByText(String text) {
        return By.xpath("//span[text()='" + text + "']");
    }

    public static By buildGlobalDivByText(String text) {
        return By.xpath("//div[text()='" + text + "']");
    }

    public static By buildGlobalH4ByText(String text) {
        return By.xpath("//h4[text()='" + text + "']");
    }

    /***
     * @return нужную кнопку, но если маркет "свернут", то функция нажмёт на него и ещё раз попытается забрать кнопку
     */
    public static WebElement findElementWithClicking(ChromeDriver driver, WebElement element, By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        WebElement res;
        try {
            res = wait.until(driver1 -> element.findElement(by));
            return res;
        } catch (TimeoutException e) {
            element.click();
            try {
                res = wait.until(driver1 -> element.findElement(by));
                return res;
            } catch (TimeoutException e1) {
                throw new RuntimeException("Коэффициенты события изменились [pinnacle]: " + by);
            }
        }
    }

    /***
     * @return лист нужных кнопок, но если маркет "свернут", то функция нажмёт на него и ещё раз попытается забрать кнопки
     */
    public static List<WebElement> findElementsWithClicking(ChromeDriver driver, WebElement element, By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        List<WebElement> res;
        try {
            res = wait.until(driver1 -> element.findElements(by));
            return res;
        } catch (TimeoutException e) {
            element.click();
            try {
                res = wait.until(driver1 -> element.findElements(by));
                return res;
            } catch (TimeoutException e1) {
                throw new RuntimeException("Button not found [pinnacle] with by: " + by);
            }
        }
    }

    public static void login(ChromeDriver driver, Bookmaker bookmaker) throws InterruptedException {
        driver.manage().deleteAllCookies();

        var login = Context.betsParams.get(bookmaker).login();
        var password = Context.betsParams.get(bookmaker).password();

        switch (bookmaker) {
            case _188BET -> {
                driver.manage().window().setSize(new Dimension(1400, 1000));

                Context.botPool.submit(() -> driver.get(bookmaker.link));
                var wait = new WebDriverWait(driver, Duration.ofSeconds(120));

                wait.until(driver1 -> driver1.findElement(By.xpath("//*[@id='s-app-bar']/div/nav/ul/li[1]/a")));

                var count = 0;

                TimeUnit.SECONDS.sleep(5);

                while (!clickIfIsClickable(driver, By.xpath("//*[@id='s-app-bar']/div/button")))
                    if (++count == 10) throw new RuntimeException();

                var loginInput = wait.until(driver1 -> driver1.findElement(By.id("UserIdOrEmail")));
                wait.until(ExpectedConditions.elementToBeClickable(loginInput));
                loginInput.click();
                loginInput.sendKeys(login);

                var passwordInput = wait.until(driver1 -> driver1.findElement(By.id("Password")));
                wait.until(ExpectedConditions.elementToBeClickable(passwordInput));
                passwordInput.click();
                passwordInput.sendKeys(password);

                var button = wait.until(driver1 -> driver1.findElement(By.xpath("/html/body/div[7]/aside/div/div[2]/div/div/form[1]/div[3]/button[2]")));
                wait.until(ExpectedConditions.elementToBeClickable(button));
                TimeUnit.SECONDS.sleep(3);
                button.click();

                wait.until(driver1 -> driver1.findElement(By.xpath("//*[@id='s-app-bar']/div/div[3]/div[1]/ul/li[2]")));

                count = 0;
                while (!clickIfIsClickable(driver, By.xpath("//*[@id='s-app-bar']/div/nav/ul/li[1]/a")))
                    if (++count == 10) throw new RuntimeException();
            } case PINNACLE -> {
                driver.get(bookmaker.link);
                var wait = new WebDriverWait(driver, Duration.ofSeconds(120));

                var loginInput = new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(driver1 -> driver1.findElement(By.xpath("//input[@id='username']")));
                wait.until(ExpectedConditions.elementToBeClickable(loginInput));
                loginInput.click();
                loginInput.sendKeys(login);

                var passwordInput = wait.until(driver1 -> driver1.findElement(By.xpath("//input[@id='password']")));
                wait.until(ExpectedConditions.elementToBeClickable(passwordInput));
                passwordInput.click();
                passwordInput.sendKeys(password);

                var sendButton = wait.until(driver1 -> driver1.findElement(By.xpath("//*[@id='root']/div/div[1]/div[1]/div[3]/div[2]/div/div/div[4]/button")));
                wait.until(ExpectedConditions.elementToBeClickable(sendButton));
                sendButton.click();

                var captchaButton = wait.until(driver1 -> driver1.findElement(By.xpath("//*[@id='loginRecaptcha']/div[2]/div[2]")));
                captchaButton.click();

                wait.until(driver1 -> driver1.findElement(By.xpath("//div[text()='Капча решена!']")));
                TimeUnit.SECONDS.sleep(5);

                var button = wait.until(driver1 -> driver1.findElement(By.xpath("//*[@id='modal']/div/div/div/div/div[2]/div/form/div/div[4]/button")));
                driver.executeScript("arguments[0].click();", button);
            }
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
}
