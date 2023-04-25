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

        var options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        if (!params.proxyIp().isEmpty()) {
            options.addArguments("--proxy-server=socks5://" + "62.113.105.132" + ":" + "40171");
            options.addArguments("--proxy-auth=" + "5lfnqi" + ":" + "dsxozk");
//            var proxy = new Proxy();
//            proxy.setSocksProxy("5lfnqi:dsxozk@62.113.105.132:40171");
//            proxy.setSocksVersion(5);
//            proxy.setAutodetect(false);
//            options.setProxy(proxy);
//            options.addArguments("ignore-certificate-errors");
        }

        var driver = new ChromeDriver(options);

        var dimension = new Dimension(800, 800);
        driver.manage().window().setSize(dimension);

        screenStorage.put(bookmaker, driver);

        Context.parsingPool.execute(() -> {
            try {
                driver.get("https://2ip.ru"); // link
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

    public synchronized ChromeDriver getScreenForBookmaker(Bookmaker bookmaker) {
        if (bookmaker.isApi) return null;
        return (ChromeDriver) screenStorage.get(bookmaker);
    }

    public synchronized void clear() {
        for (Bookmaker bookmakers : screenStorage.keySet()) removeScreenForBookmaker(bookmakers);
    }
}
