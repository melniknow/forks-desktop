package com.melniknow.fd.betting;

import com.melniknow.fd.Context;
import com.melniknow.fd.core.Logger;
import com.melniknow.fd.domain.Bookmaker;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ScreenManager {
    private final ConcurrentMap<Bookmaker, Object> screenStorage = new ConcurrentHashMap<>();

    public ScreenManager() {
        WebDriverManager.chromedriver().setup();
    }

    public synchronized void createScreenForBookmaker(Bookmaker bookmaker) {
        if (bookmaker.isApi) {
            screenStorage.put(bookmaker, new Object());
            return;
        }

        var params = Context.betsParams.get(bookmaker);
        var link = params.link();

//        var proxy = new Proxy();
//        proxy.setHttpProxy("102.165.51.172:3128");

        var dimension = new Dimension(800, 800);

        var options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
//        options.setCapability("proxy", proxy);

        var driver = new ChromeDriver(options);
        driver.manage().window().setSize(dimension);

        screenStorage.put(bookmaker, driver);
        try {
            driver.get("https://google.com"); // link
        } catch (Exception e) {
            Logger.writeToLogSession("Бот не смог открыть ссылку - " + link);
        }
    }

    public synchronized void removeScreenForBookmaker(Bookmaker bookmaker) {
        var driver = screenStorage.remove(bookmaker);
        if (driver != null && !bookmaker.isApi) {
            var driverImpl = (ChromeDriver) driver;
            driverImpl.quit();
        }
    }

    public synchronized ChromeDriver getScreenForBookmaker(Bookmaker bookmaker) {
        if (bookmaker.isApi) return null;
        return (ChromeDriver) screenStorage.get(bookmaker);
    }

    public synchronized void clear() {
        for (Bookmaker bookmakers : screenStorage.keySet()) removeScreenForBookmaker(bookmakers);
    }
}
