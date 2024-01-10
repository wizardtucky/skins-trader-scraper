package com.application.services;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class BrowserProvider {

    private BrowserProvider() {
    }

    public static ChromeDriver createDriver() {
        String path = System.getProperty("user.dir");

        System.setProperty("webdriver.chrome.driver", path + "/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(false);
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"disable-popup-blocking", "enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("window-size=1280,800");
        options.addArguments("--no-sandbox");
        options.addArguments("disable-infobars");
        options.addArguments("--single-process");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("log-level=3");
        options.addArguments("user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/37.0.2062.94 Chrome/37.0.2062.94 Safari/537.36");

        ChromeDriver driver = new ChromeDriver(options);
        driver.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        return driver;
    }

}
