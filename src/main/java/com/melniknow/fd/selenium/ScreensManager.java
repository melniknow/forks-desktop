package com.melniknow.fd.selenium;

import com.melniknow.fd.oddscorp.Bookmakers;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;

public class ScreensManager {
    private static final ChromeDriver driver;
    static {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }
    public synchronized void createScreenForBookmaker(Bookmakers bookmaker) {

    }

    public synchronized void removeScreenForBookmaker(Bookmakers bookmaker) {

    }
}
