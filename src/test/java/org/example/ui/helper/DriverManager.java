package org.example.ui.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import java.time.Duration;

// Manages WebDriver instances
public class DriverManager {
    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            initializeDriver();
        }
        return driver.get();
    }

    private static void initializeDriver() {
        String browser = ConfigReader.getProperty("browser");
        logger.info("Initializing {} driver", browser);
        WebDriver webDriver;

        switch (browser.toLowerCase()) {
            case "firefox":
                webDriver = new FirefoxDriver();
                break;
            case "chrome":
            default:
                webDriver = new ChromeDriver();
                break;
        }

        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(
            Duration.ofSeconds(Long.parseLong(ConfigReader.getProperty("implicitWait")))
        );
        driver.set(webDriver);
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            logger.info("Quitting WebDriver");
            driver.get().quit();
            driver.remove();
        }
    }
} 
