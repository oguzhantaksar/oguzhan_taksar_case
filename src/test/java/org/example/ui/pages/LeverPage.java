package org.example.ui.pages;

import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.ui.base.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Objects;
import java.util.Set;

// Page Object for the Lever Application Page
public class LeverPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(LeverPage.class);

    @Step("Verifying navigation to Lever application page")
    public Boolean verifyNavigatedToLever() {
        logger.info("Verifying navigation to Lever application page");
        
        // Wait for new window to appear
        logger.debug("Waiting for new window to appear");
        wait.until(driver -> driver.getWindowHandles().size() > 1);

        // Get all window handles
        Set<String> windowHandles = driver.getWindowHandles();
        logger.debug("Found {} window handles", windowHandles.size());

        // Switch to the new window (last opened)
        String newWindow = windowHandles.stream()
                .reduce((first, second) -> second)
                .orElse(null);

        if (newWindow != null) {
            logger.debug("Switching to window: {}", newWindow);
            driver.switchTo().window(newWindow);
            
            // Wait for URL to contain "jobs.lever"
            logger.debug("Waiting for URL to contain 'jobs.lever'");
            boolean navigated = wait.until(driver -> driver.getCurrentUrl().contains("jobs.lever"));
            logger.info("Navigation to Lever verified: {}", navigated);
            return navigated;
        }
        
        logger.warn("Could not find a new window to switch to");
        return false;
    }
} 
