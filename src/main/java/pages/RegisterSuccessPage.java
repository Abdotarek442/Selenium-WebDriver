package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RegisterSuccessPage extends BasePage {

    private final By heading = By.cssSelector("#content h1");

    public RegisterSuccessPage(WebDriver driver) {
        super(driver);
    }

    public String getHeadingText() {
        return getText(heading);
    }
}
