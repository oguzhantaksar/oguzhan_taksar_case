package org.example.ui.pages;

import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.ui.base.BasePage;
import org.example.utils.ConfigReader;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

// Page Object for the Insider Home Page
public class HomePage extends BasePage {
    private static final Logger logger = LogManager.getLogger(HomePage.class);
    private static final String HOME_URL = ConfigReader.getProperty("insiderBaseUrl");
    
    @FindBy(xpath = "//a[contains(text(), 'Company')]")
    private WebElement navBarCompany;

    @FindBy(css = "[href='https://useinsider.com/careers/']")
    private WebElement careersLink;

    @FindBy(css = "img[src='https://useinsider.com/assets/img/logo-old.png']")
    private WebElement insiderLogo;

    @FindBy(css = "a[href='https://inone.useinsider.com/login']")
    private WebElement loginBtn;

    public HomePage() {
        super();
        logger.info("Initializing HomePage");
    }

    @Step("Navigating to Insider home page")
    public void navigateToHomePage() {
        logger.info("Navigating to: {}", HOME_URL);
        driver.get(HOME_URL);
    }

    @Step("Validating basic elements on home page")
    public boolean validateBasicElements() {
        logger.info("Validating basic elements");
        boolean isLogoDisplayed = isElementDisplayed(insiderLogo);
        boolean isLoginBtnDisplayed = isElementDisplayed(loginBtn);
        boolean isCorrectUrl = driver.getCurrentUrl().equals(HOME_URL);
        
        return isLogoDisplayed && isLoginBtnDisplayed && isCorrectUrl;
    }

    @Step("Navigating to Careers page")
    public CareersPage navigateToCareers() {
        logger.info("Navigating to Careers");
        click(navBarCompany);
        click(careersLink);
        return new CareersPage();
    }
} 
