package com.melniknow.fd.selenium;

import com.melniknow.fd.oddscorp.Bookmakers;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.HashMap;

public class ScreensManager {
    private final HashMap<Bookmakers, ChromeDriver> screenStorage = new HashMap<>();

    public ScreensManager() {
        WebDriverManager.chromedriver().setup();
    }

    public synchronized void createScreenForBookmaker(Bookmakers bookmaker) {
        screenStorage.put(bookmaker, new ChromeDriver());
    }

    public synchronized void removeScreenForBookmaker(Bookmakers bookmaker) {
        var driver = screenStorage.remove(bookmaker);
        if (driver != null) {
            driver.quit();
        }
    }

    public synchronized ChromeDriver getScreenForBookmaker(Bookmakers bookmaker) {
        return screenStorage.get(bookmaker);
    }
}
