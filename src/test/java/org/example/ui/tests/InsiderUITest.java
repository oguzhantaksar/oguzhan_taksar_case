package org.example.ui.tests;

import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.ui.base.BaseTest;
import org.example.ui.pages.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
@Execution(ExecutionMode.CONCURRENT)
public class InsiderUITest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(InsiderUITest.class);

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Insider QA Career Flow Test")
    public void testInsiderQACareerFlow() {
        logger.info("Starting Insider QA Career Flow Test");
        
        logger.info("Step 1: Navigate to home page");
        HomePage homePage = new HomePage();
        homePage.navigateToHomePage();
        assertTrue(homePage.validateBasicElements(), "Home page not loaded correctly");

        logger.info("Step 2: Navigate to careers page");
        CareersPage careersPage = homePage.navigateToCareers();
        assertTrue(careersPage.areAllBlocksDisplayed(), "Careers page not loaded correctly");

        logger.info("Step 3: Navigate to QA Jobs and filter");
        AllQAJobsPage allQaJobsPage = careersPage.navigateToQAJobs().navigateToAllQAJobsPage();
        allQaJobsPage.selectFilters();
        assertTrue(allQaJobsPage.verifyJobExist(), "There is no available QA job");
        assertTrue(allQaJobsPage.verifyAllJobsDetails(), "Job title or department or location doesn't match");
        
        logger.info("Step 4: Navigate to Lever application page");
        LeverPage leverPage = allQaJobsPage.navigateToLeverPage();
        assertTrue(leverPage.verifyNavigatedToLever(), "Not navigated to lever");
        
        logger.info("Test completed successfully");
    }
} 