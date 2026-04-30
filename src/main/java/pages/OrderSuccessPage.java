package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class OrderSuccessPage extends BasePage {

    private final By heading = By.cssSelector("#content h1");

    public OrderSuccessPage(WebDriver driver) {
        super(driver);
    }

    public String getHeading() {
        return getText(heading);
    }
}
