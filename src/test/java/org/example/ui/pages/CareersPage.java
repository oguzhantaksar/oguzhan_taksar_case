package org.example.ui.pages;

import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.ui.base.BasePage;
import org.example.utils.ConfigReader;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

// Page Object for the Insider Careers Page
public class CareersPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(CareersPage.class);

    private static final String QA_CAREERS_URL = ConfigReader.getProperty("insiderQAUrl");
    
    @FindBy(xpath = "//h3[contains(text(), 'Our Locations')]")
    private WebElement locationsBlock;

    @FindBy(xpath = "//h3[contains(text(), 'Find your calling')]")
    private WebElement teamsBlock;

    @FindBy(xpath = "//h2[contains(text(), 'Life at Insider')]")
    private WebElement lifeAtInsiderBlock;

    @FindBy(css = "a[href='https://useinsider.com/careers/quality-assurance/']")
    private WebElement qaJobsLink;

    @Step("Checking if all content blocks are displayed on Careers page")
    public boolean areAllBlocksDisplayed() {
        logger.info("Checking if all content blocks are displayed");
        
        boolean isLocationsDisplayed = isElementDisplayed(locationsBlock);
        boolean isTeamsDisplayed = isElementDisplayed(teamsBlock);
        boolean isLifeAtInsiderDisplayed = isElementDisplayed(lifeAtInsiderBlock);
        
        logger.debug("Locations block displayed: {}, Teams block displayed: {}, Life at Insider block displayed: {}", 
                isLocationsDisplayed, isTeamsDisplayed, isLifeAtInsiderDisplayed);
        
        return isLocationsDisplayed && isTeamsDisplayed && isLifeAtInsiderDisplayed;
    }

    @Step("Navigating to QA Jobs page")
    public QAJobsPage navigateToQAJobs() {
        logger.info("Navigating to QA Jobs page: {}", QA_CAREERS_URL);
        driver.get(QA_CAREERS_URL);
        return new QAJobsPage();
    }
} 
