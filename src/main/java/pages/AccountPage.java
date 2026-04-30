package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AccountPage extends BasePage {

    private final By accountHeading = By.xpath("//h2[normalize-space()='My Account']");
    private final By myAccountSidebar = By.xpath("//aside[@id='column-right']//a[normalize-space()='My Account']");

    public AccountPage(WebDriver driver) {
        super(driver);
    }

    public boolean isOpen() {
        return isPresent(accountHeading) || isPresent(myAccountSidebar);
    }
}
