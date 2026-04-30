package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private final By emailField    = By.id("input-email");
    private final By passwordField = By.id("input-password");
    private final By loginButton   = By.cssSelector("input[type='submit'][value='Login']");
    private final By warningAlert  = By.cssSelector("div.alert.alert-danger");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String email, String password) {
        type(emailField, email);
        type(passwordField, password);
        click(loginButton);
    }

    public boolean isWarningDisplayed() {
        return isPresent(warningAlert);
    }

    public String getWarningText() {
        return getText(warningAlert);
    }
}
