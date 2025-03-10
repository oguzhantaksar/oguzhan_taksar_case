package org.example.ui.pages;

import org.example.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

public class QAJobsPage extends BasePage {
    
    @FindBy(css = "[href='https://useinsider.com/careers/open-positions/?department=qualityassurance']")
    private WebElement allQAJobsBtn;


    public AllQAJobsPage navigateToAllQAJobsPage() {
        allQAJobsBtn.click();
        return new AllQAJobsPage();
    }

} 