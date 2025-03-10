package org.example.ui.pages;

import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.TimeoutException;

import java.util.List;

// Page Object for the All QA Jobs Page
public class AllQAJobsPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(AllQAJobsPage.class);
    
    By locationDropdown = By.id("select2-filter-by-location-container");
    By departmentDropdown = By.cssSelector("#select2-filter-by-department-container");
    By istanbulLocationOption = By.xpath("//li[contains(@id, 'Istanbul')]");
    By jobListings = By.cssSelector("[data-location='istanbul-turkiye']");
    By jobTitle = By.cssSelector(".position-title");
    By jobDepartment = By.cssSelector(".position-department");
    By jobLocation = By.cssSelector(".position-location");
    By jobListingContainer = By.cssSelector(".position-list-item-wrapper");
    By applyButton = By.cssSelector(".position-list-item-wrapper a[href*='jobs.lever.co']");

    public AllQAJobsPage(){
        super();
        logger.info("Initializing AllQAJobsPage");
    }

    // Selects filters to narrow down QA job listings
    @Step("Selecting job filters: Department=QA, Location=Istanbul")
    public void selectFilters() {
        logger.info("Selecting job filters");
        
        wait.until(ExpectedConditions.textToBePresentInElementLocated(departmentDropdown, "Quality Assurance"));
        
        click(driver.findElement(locationDropdown));
        waitForElementVisible(driver.findElement(istanbulLocationOption));
        click(driver.findElement(istanbulLocationOption));
        
        waitForElementsPresent(jobListings);
    }

    // Waits for QA jobs to load
    private void waitForQAJobsToLoad() {
        logger.info("Waiting for QA jobs to load");
        // Scroll to the job listings area to ensure it's visible
        try {
            List<WebElement> listings = driver.findElements(jobListings);
            if (!listings.isEmpty()) {
                scrollToElement(listings.get(0));
                logger.info("Scrolled to job listings area");
            }
        } catch (Exception e) {
            logger.warn("Could not scroll to job listings area: {}", e.getMessage());
        }
        
        wait.until(driver -> {
            List<WebElement> jobs = driver.findElements(jobListings);
            return jobs.stream().anyMatch(job -> {
                try {
                    String title = job.findElement(jobTitle).getText();
                    return title.contains("Quality Assurance") || title.contains("QA");
                } catch (Exception e) {
                    logger.error("Error checking job title: {}", e.getMessage());
                    return false;
                }
            });
        });
    }

    // Verifies that QA jobs exist
    @Step("Verifying QA jobs exist")
    public Boolean verifyJobExist() {
        logger.info("Verifying QA jobs exist");
        try {
            // Scroll to job listings before verification
            List<WebElement> listings = driver.findElements(jobListings);
            if (!listings.isEmpty()) {
                scrollToElement(listings.get(0));
                logger.info("Scrolled to job listings");
            }
            
            waitForQAJobsToLoad();
            List<WebElement> allJobs = driver.findElements(jobListings);
            logger.info("Found {} QA jobs", allJobs.size());
            return !allJobs.isEmpty();
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for QA jobs");
            return false;
        }
    }

    // Verifies all job listings have correct details
    @Step("Verifying all job details match filter criteria")
    public Boolean verifyAllJobsDetails() {
        logger.info("Verifying job details");
        // Scroll to job listings before verification
        List<WebElement> listings = driver.findElements(jobListings);
        if (!listings.isEmpty()) {
            scrollToElement(listings.get(0));
            logger.info("Scrolled to job listings");
        }
        
        waitForQAJobsToLoad();
        List<WebElement> allJobs = driver.findElements(jobListings);

        return allJobs.stream().allMatch(job -> {
            String positionTitleText = job.findElement(jobTitle).getText();
            String positionDepartmentText = job.findElement(jobDepartment).getText();
            String positionLocationText = job.findElement(jobLocation).getText();
            
            boolean isTitleValid = positionTitleText.contains("Quality Assurance") || positionTitleText.contains("QA");
            boolean isDepartmentValid = positionDepartmentText.contains("Quality Assurance") || positionDepartmentText.contains("QA");
            boolean isLocationValid = positionLocationText.contains("Istanbul, Turkiye");
            
            return isTitleValid && isDepartmentValid && isLocationValid;
        });
    }

    // Special hover and click implementation with retry mechanism
    protected void clickWithScrollAndRetry(By containerLocator, By buttonLocator) {
        logger.info("Attempting to click the button");
        wait.until(ExpectedConditions.presenceOfElementLocated(containerLocator));
        wait.until(ExpectedConditions.presenceOfElementLocated(buttonLocator));

        WebElement button = driver.findElement(buttonLocator);
        scrollToElement(button);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
    }

    // Navigates to the Lever application page
    @Step("Navigating to Lever application page")
    public LeverPage navigateToLeverPage() {
        logger.info("Opening the job application page");
        clickWithScrollAndRetry(jobListingContainer, applyButton);
        return new LeverPage();
    }
} 
