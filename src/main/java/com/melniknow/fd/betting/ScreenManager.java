package com.melniknow.fd.betting;

import com.melniknow.fd.App;
import com.melniknow.fd.Context;
import com.melniknow.fd.core.Logger;
import com.melniknow.fd.domain.Bookmaker;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
                if (bookmaker.isApi) return;

                var options = new ChromeOptions();
                options.addArguments("--remote-allow-origins=*");
                options.addArguments("ignore-certificate-errors");
                options.addArguments("--disable-blink-features=AutomationControlled");
                options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
                options.setExperimentalOption("useAutomationExtension", false);

                if (!params.userAgent().isEmpty())
                    options.addArguments("user-agent=" + params.userAgent());

                if (!params.lang().isEmpty()) {
                    var chromePrefs = new HashMap<String, Object>();
                    chromePrefs.put("intl.accept_languages", "en");

                    options.setExperimentalOption("prefs", chromePrefs);
                }

                if (!params.proxyIp().isEmpty()) {
                    try {
                        var stream = Objects.requireNonNull(App.class.getResourceAsStream("proxy.crx"));
                        var file = new File(UUID.randomUUID().toString());
                        if (!file.createNewFile())
                            throw new RuntimeException("Не удалось создать файл");

                        Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        options.addExtensions(file);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }

                var driver = new ChromeDriver(options);

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

                if (params.screenSize() != null) {
                    var screenSizes = params.screenSize().split("/");
                    screenX = Integer.parseInt(screenSizes[0]);
                    screenY = Integer.parseInt(screenSizes[1]);
                }

                var dimension = new Dimension(screenX, screenY);

                driver.manage().window().setSize(dimension);
                driver.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

                screenStorage.put(bookmaker, driver);

                driver.get(link);
            } catch (Exception e) {
                Logger.writeToLogSession(e.getMessage() + " Бот не смог открыть ссылку - " + link);
            }
        });
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