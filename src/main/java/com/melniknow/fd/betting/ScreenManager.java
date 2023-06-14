package com.melniknow.fd.betting;

import com.google.gson.JsonParser;
import com.melniknow.fd.App;
import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.core.Logger;
import com.melniknow.fd.domain.Bookmaker;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class ScreenManager {
    private final ConcurrentMap<Bookmaker, ChromeDriver> screenStorage = new ConcurrentHashMap<>();

    public ScreenManager() {
        System.setProperty("webdriver.chrome.driver", "C:\\build\\undetected_chromedriver.exe");
    }

    public synchronized void createScreenForBookmaker(Bookmaker bookmaker) {
        var params = Context.betsParams.get(bookmaker);
        var link = params.link();

        Context.parsingPool.execute(() -> {
            try {

                var profileId = params.dolphinAntiId();
                var url = "http://localhost:3001/v1.0/browser_profiles/%s/start?automation=1".formatted(profileId);

                var dolphinData = "";
                var timeout = 50;

                var config = RequestConfig.custom()
                    .setConnectTimeout(timeout * 1000)
                    .setConnectionRequestTimeout(timeout * 1000)
                    .setSocketTimeout(timeout * 1000).build();

                try (var httpClient = HttpClientBuilder.create()
                    .setDefaultRequestConfig(config)
                    .build()) {
                    var request = new HttpGet(url);
                    try (CloseableHttpResponse response = httpClient.execute(request)) {
                        var entity = response.getEntity();
                        if (entity != null) {
                            dolphinData = EntityUtils.toString(entity);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                var jsonParser = JsonParser.parseString(dolphinData);
                var port = jsonParser.getAsJsonObject().getAsJsonObject("automation")
                    .getAsJsonPrimitive("port").getAsLong();

                ChromeOptions options = new ChromeOptions();
//                    options.addArguments("--remote-allow-origins=*");
//                    options.addArguments("ignore-certificate-errors");
//                    options.addArguments("--disable-blink-features=AutomationControlled");
//                    options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
//                    options.setExperimentalOption("useAutomationExtension", false);
                options.addArguments("--remote-debugging-port=" + port);
                options.setBinary("C:\\build\\undetected_chromedriver.exe");
                var driver = new ChromeDriver(options);

                screenStorage.put(bookmaker, driver);
                SeleniumSupport.login(driver, bookmaker);

            } catch (Exception e) {
                Context.log.warning("Бот не смог открыть ссылку - " + link + " - " + e.getLocalizedMessage());
                Logger.writeToLogSession("Бот не смог открыть ссылку - " + link);

                if (e instanceof InterruptedException || e.getCause() instanceof InterruptedException) {
                    throw new RuntimeException("Поток прерван при попытке открыть ссылку - " + link);
                }
            }
        });
    }
    private void switchWindow(ChromeDriver driver) {
        var self = driver.getWindowHandle();
        for (String windowHandle : driver.getWindowHandles()) {
            if (!self.equals(windowHandle)) {
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