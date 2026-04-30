package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RegisterPage extends BasePage {

    private final By firstName       = By.id("input-firstname");
    private final By lastName        = By.id("input-lastname");
    private final By email           = By.id("input-email");
    private final By telephone       = By.id("input-telephone");
    private final By password        = By.id("input-password");
    private final By confirmPassword = By.id("input-confirm");
    private final By agreeCheckbox   = By.name("agree");
    private final By continueButton  = By.cssSelector("input[type='submit'][value='Continue']");

    // Inline error messages
    private final By firstNameError = By.cssSelector("#input-firstname + div.text-danger");
    private final By lastNameError  = By.cssSelector("#input-lastname + div.text-danger");
    private final By emailError     = By.cssSelector("#input-email + div.text-danger");
    private final By telephoneError = By.cssSelector("#input-telephone + div.text-danger");
    private final By passwordError  = By.cssSelector("#input-password + div.text-danger");

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    public RegisterPage enterFirstName(String value) { type(firstName, value); return this; }
    public RegisterPage enterLastName(String value)  { type(lastName, value);  return this; }
    public RegisterPage enterEmail(String value)     { type(email, value);     return this; }
    public RegisterPage enterTelephone(String value) { type(telephone, value); return this; }
    public RegisterPage enterPassword(String value)  { type(password, value);  return this; }
    public RegisterPage enterConfirmPassword(String value) {
        type(confirmPassword, value);
        return this;
    }

    public RegisterPage acceptAgreement() {
        click(agreeCheckbox);
        return this;
    }

    public void clickContinue() {
        click(continueButton);
    }

    public void fillAll(String fn, String ln, String em, String tel, String pw) {
        enterFirstName(fn);
        enterLastName(ln);
        enterEmail(em);
        enterTelephone(tel);
        enterPassword(pw);
        enterConfirmPassword(pw);
    }

    public boolean isFirstNameErrorShown() { return isPresent(firstNameError); }
    public boolean isLastNameErrorShown()  { return isPresent(lastNameError);  }
    public boolean isEmailErrorShown()     { return isPresent(emailError);     }
    public boolean isTelephoneErrorShown() { return isPresent(telephoneError); }
    public boolean isPasswordErrorShown()  { return isPresent(passwordError);  }

    public String getPasswordErrorText() {
        return getText(passwordError);
    }
}
