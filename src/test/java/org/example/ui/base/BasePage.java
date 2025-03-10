package org.example.ui.base;

import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.ui.helper.DriverManager;
import org.example.utils.ConfigReader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.interactions.Actions;

// Base page class for all page objects
public class BasePage {
    protected static final Logger logger = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    
    private static final int DEFAULT_TIMEOUT_SECONDS = Integer.parseInt(ConfigReader.getProperty("explicitWait"));

    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
        PageFactory.initElements(driver, this);
        logger.debug("Initialized page: {}", this.getClass().getSimpleName());
    }

    protected void waitForElementVisible(By by) {
        logger.debug("Waiting for element: {}", by);
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    protected void waitForElementVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected void waitForElementClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected void waitForElementsVisible(List<WebElement> elements) {
        wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    protected void waitForElementPresent(By by) {
        wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    protected void waitForElementsPresent(By by) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    protected void waitForTextPresent(WebElement element, String text) {
        wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    protected void scrollToElement(WebElement element) {
        logger.debug("Scrolling to element");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        wait.until(webDriver -> {
            String readyState = ((JavascriptExecutor) webDriver).executeScript("return document.readyState").toString();
            return readyState.equals("complete");
        });
    }

    protected void hoverAndClick(WebElement elementToHover, WebElement elementToClick) {
        logger.debug("Hover and click action");
        Actions actions = new Actions(driver);
        scrollToElement(elementToHover);
        
        wait.until(driver -> {
            try {
                actions.moveToElement(elementToHover).perform();
                return true;
            } catch (Exception e) {
                logger.error("Failed to hover: {}", e.getMessage());
                return false;
            }
        });
        
        waitForElementVisible(elementToClick);
        waitForElementClickable(elementToClick);
        elementToClick.click();
    }

    protected void click(WebElement element) {
        waitForElementClickable(element);
        element.click();
    }

    protected void setText(WebElement element, String text) {
        logger.debug("Setting text: '{}'", text);
        waitForElementVisible(element);
        element.clear();
        element.sendKeys(text);
    }

    protected boolean isElementDisplayed(WebElement element) {
        try {
            waitForElementVisible(element);
            return element.isDisplayed();
        } catch (TimeoutException | NoSuchElementException | StaleElementReferenceException e) {
            logger.warn("Element not displayed: {}", e.getMessage());
            return false;
        }
    }

    @Attachment(value = "Screenshot on failure", type = "image/png")
    public byte[] takeScreenshot(String name) {
        logger.info("Taking screenshot: {}", name);
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
} 
