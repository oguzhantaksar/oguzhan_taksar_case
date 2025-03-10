package org.example.ui.base;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.ui.helper.DriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

// Base test class for UI tests
@ExtendWith(BaseTest.TestResultWatcher.class)
public class BaseTest {
    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    @BeforeEach
    public void setUp() {
        logger.info("Setting up test environment");
        DriverManager.getDriver();
    }

    @AfterEach
    public void tearDown() {
        logger.info("Tearing down test environment");
    }


    public static class TestResultWatcher implements TestWatcher {
        private static final Logger watcherLogger = LogManager.getLogger(TestResultWatcher.class);

        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            watcherLogger.error("Test failed: {}", context.getDisplayName());
            
            WebDriver driver = DriverManager.getDriver();
            if (driver != null) {
                try {
                    watcherLogger.info("Capturing screenshot for failed test");
                    Allure.addAttachment(
                        "Screenshot on failure",
                        "image/png",
                        new ByteArrayInputStream(
                            ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)
                        ),
                        "png"
                    );
                } finally {
                    DriverManager.quitDriver();
                }
            }
        }

        @Override
        public void testSuccessful(ExtensionContext context) {
            watcherLogger.info("Test succeeded: {}", context.getDisplayName());
            DriverManager.quitDriver();
        }

        @Override
        public void testAborted(ExtensionContext context, Throwable cause) {
            watcherLogger.warn("Test aborted: {}", context.getDisplayName());
            DriverManager.quitDriver();
        }
    }
} 
