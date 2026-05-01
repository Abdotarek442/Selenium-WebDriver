package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AccountPage extends BasePage {

    // h2 heading rendered on the My Account dashboard page
    private final By accountHeading   = By.cssSelector("#content h2");
    // "My Account" link in the right-hand sidebar navigation
    private final By myAccountSidebar = By.cssSelector("#column-right a[href*='account/account']");

    public AccountPage(WebDriver driver) {
        super(driver);
    }

    public boolean isOpen() {
        return isPresent(accountHeading) || isPresent(myAccountSidebar);
    }
}
