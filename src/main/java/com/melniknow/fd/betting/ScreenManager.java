package com.melniknow.fd.betting;

import com.melniknow.fd.App;
import com.melniknow.fd.Context;
import com.melniknow.fd.core.Logger;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.utils.BetUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ScreenManager {
    private final ConcurrentMap<Bookmaker, Object> screenStorage = new ConcurrentHashMap<>();

    public ScreenManager() {
        WebDriverManager.chromedriver().setup();
    }

    public synchronized void createScreenForBookmaker(Bookmaker bookmaker) {
        var params = Context.betsParams.get(bookmaker);
        var link = params.link();

        Context.parsingPool.execute(() -> {
            try {
                if (bookmaker.isApi) {
                    screenStorage.put(bookmaker,
                        new BetUtils.Proxy(params.proxyIp(),
                            String.valueOf(params.proxyPort()),
                            params.proxyLogin(),
                            params.proxyPassword()
                        )
                    );
                    return;
                }

                var options = new ChromeOptions();
                options.addArguments("--remote-allow-origins=*");
                options.addArguments("ignore-certificate-errors");

                if (!params.proxyIp().isEmpty()) {
                    try {
                        options.addExtensions(new File(Objects.requireNonNull(App.class.getResource("proxy.crx")).toURI()));
                    } catch (URISyntaxException e) {
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

                var dimension = new Dimension(800, 800);
                driver.manage().window().setSize(dimension);

                screenStorage.put(bookmaker, driver);

                driver.get(link);
            } catch (Exception e) {
                Logger.writeToLogSession("Бот не смог открыть ссылку - " + link);
            }
        });
    }

    public synchronized void removeScreenForBookmaker(Bookmaker bookmaker) {
        var driver = screenStorage.remove(bookmaker);
        if (driver != null && !bookmaker.isApi) {
            var driverImpl = (ChromeDriver) driver;
            driverImpl.quit();
        }
    }

    public synchronized BetUtils.Proxy getProxyForApiBookmaker(Bookmaker bookmaker) {
        if (!bookmaker.isApi) return null;
        return (BetUtils.Proxy) screenStorage.get(bookmaker);
    }

    public synchronized ChromeDriver getScreenForBookmaker(Bookmaker bookmaker) {
        if (bookmaker.isApi) return null;
        return (ChromeDriver) screenStorage.get(bookmaker);
    }

    public synchronized void clear() {
        for (Bookmaker bookmakers : screenStorage.keySet()) removeScreenForBookmaker(bookmakers);
    }
}
