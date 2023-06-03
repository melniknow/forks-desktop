package com.melniknow.fd.betting;

import com.melniknow.fd.App;
import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.core.Logger;
import com.melniknow.fd.domain.Bookmaker;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class ScreenManager {
    private final ConcurrentMap<Bookmaker, ChromeDriver> screenStorage = new ConcurrentHashMap<>();

    public ScreenManager() {
        WebDriverManager.chromedriver().setup();
    }

    public synchronized void createScreenForBookmaker(Bookmaker bookmaker) {
        var params = Context.betsParams.get(bookmaker);
        var link = params.link();

        Context.parsingPool.execute(() -> {
            try {
                var options = new ChromeOptions();
                options.addArguments("--remote-allow-origins=*");
                options.addArguments("ignore-certificate-errors");
                options.addArguments("--disable-blink-features=AutomationControlled");
                options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
                options.setExperimentalOption("useAutomationExtension", false);

                if (!params.userAgent().isEmpty())
                    options.addArguments("user-agent=" + params.userAgent());

                var chromePrefs = new HashMap<String, Object>();
                chromePrefs.put("intl.accept_languages", "en");

                options.setExperimentalOption("prefs", chromePrefs);


                if (!params.proxyIp().isEmpty()) {
                    try {
                        // Финт ушами с созданием файла и чтением из него (а мы блять не умеем из jar архива читать)
                        var stream = Objects.requireNonNull(App.class.getResourceAsStream("proxy.crx"));
                        var file = new File(UUID.randomUUID().toString());
                        if (!file.createNewFile())
                            throw new RuntimeException("Не удалось создать файл");

                        Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        var stream2 = Objects.requireNonNull(App.class.getResourceAsStream("captcha.crx"));
                        var file2 = new File(UUID.randomUUID().toString());
                        if (!file2.createNewFile())
                            throw new RuntimeException("Не удалось создать файл");

                        Files.copy(stream2, file2.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        options.addExtensions(file, file2);
                        Context.deleteTempFiles.add(file);
                        Context.deleteTempFiles.add(file2);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                var driver = new ChromeDriver(options);
                screenStorage.put(bookmaker, driver);

                switchWindow(driver);
                System.out.println("SWITCH");
                TimeUnit.SECONDS.sleep(3);

                var wait = new WebDriverWait(driver, Duration.ofSeconds(30));

                var el = wait
                    .until(driver_ -> driver_.findElement(By.cssSelector("input[name=apiKey]")));
                wait.until(ExpectedConditions.elementToBeClickable(el));

                el.click();
                el.sendKeys(Context.CAPTCHA_API);

                System.out.println("EXEC");
                driver.findElement(By.id("connect")).click();

                new WebDriverWait(driver, Duration.ofSeconds(10))
                    .ignoring(NoAlertPresentException.class)
                    .until(ExpectedConditions.alertIsPresent());

                var alert = driver.switchTo().alert();
                alert.accept();

                switchWindow(driver);

                if (!params.proxyIp().isEmpty()) {
                    driver.get("chrome-extension://hjocpjdeacglfchomobaagbmipeggnjg/options.html");

                    driver.findElement(By.id("proxyEntry")).sendKeys(params.proxyIp());
                    driver.findElement(By.id("portEntry")).sendKeys(String.valueOf(params.proxyPort()));
                    driver.findElement(By.id("loginEntry")).sendKeys(params.proxyLogin());
                    driver.findElement(By.id("passwordEntry")).sendKeys(params.proxyPassword());
                    driver.findElement(By.id("manualSetProxyButton")).click();
                }

                var screenX = 1600;
                var screenY = 900;

                if (params.screenSize() != null && !params.screenSize().isEmpty()) {
                    var screenSizes = params.screenSize().split("/");
                    screenX = Integer.parseInt(screenSizes[0]);
                    screenY = Integer.parseInt(screenSizes[1]);
                }

                var dimension = new Dimension(screenX, screenY);

                driver.manage().window().setSize(dimension);
                driver.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

                SeleniumSupport.login(driver, bookmaker);
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
                Logger.writeToLogSession("Бот не смог открыть ссылку - " + link);
            }
        });
    }
    private void switchWindow(ChromeDriver driver) {
        for (String windowHandle : driver.getWindowHandles()) {
            if (!driver.getWindowHandle().equals(windowHandle)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
    }

    public synchronized void removeScreenForBookmaker(Bookmaker bookmaker) {
        var driver = screenStorage.remove(bookmaker);
        if (driver != null) driver.quit();
    }

    public synchronized ChromeDriver getScreenForBookmaker(Bookmaker bookmaker) {
        return screenStorage.get(bookmaker);
    }

    public synchronized void clear() {
        for (Bookmaker bookmakers : screenStorage.keySet()) removeScreenForBookmaker(bookmakers);
    }
}