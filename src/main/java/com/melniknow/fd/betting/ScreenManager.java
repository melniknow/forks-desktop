package com.melniknow.fd.betting;

import com.melniknow.fd.Context;
import com.melniknow.fd.core.Logger;
import com.melniknow.fd.domain.Bookmaker;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

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

//        var proxy = new Proxy();
//        proxy.setHttpProxy("102.165.51.172:3128");

        var options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
//        options.setCapability("proxy", proxy);

        var driver = new ChromeDriver(options);
        screenStorage.put(bookmaker, driver);

        try {
            driver.get("https://google.com"); // link
        } catch (Exception e) {
            Logger.writeToLogSession("Бот не смог открыть ссылку - " + link);
        }
    }

    public synchronized void removeScreenForBookmaker(Bookmaker bookmaker) {
        var driver = screenStorage.remove(bookmaker);
        if (driver != null) {
            driver.quit();
        }
    }

    public synchronized ChromeDriver getScreenForBookmaker(Bookmaker bookmaker) {
        return screenStorage.get(bookmaker);
    }

    public synchronized void clear() {
        for (Bookmaker bookmakers : screenStorage.keySet()) removeScreenForBookmaker(bookmakers);
    }
}
