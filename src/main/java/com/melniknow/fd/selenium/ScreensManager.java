package com.melniknow.fd.selenium;

import com.melniknow.fd.oddscorp.Bookmakers;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.HashMap;

public class ScreensManager {
    private static final ChromeDriver driver;
    static {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    private final HashMap<Bookmakers, String> screenStorage = new HashMap<>();

    public synchronized void createScreenForBookmaker(Bookmakers bookmaker) {
        screenStorage.put(bookmaker, "");
    }

    public synchronized void removeScreenForBookmaker(Bookmakers bookmaker) {
        screenStorage.remove(bookmaker);
    }

    public synchronized String getScreenForBookmaker(Bookmakers bookmaker) {
        return screenStorage.get(bookmaker);
    }
}
