package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class OrderSuccessPage extends BasePage {

    private final By heading   = By.cssSelector("#content h1");
    private final By cartBadge = By.cssSelector("#cart button span");

    public OrderSuccessPage(WebDriver driver) {
        super(driver);
    }

    public String getHeading() {
        return getText(heading);
    }

    /** Badge text of the mini-cart after order, e.g. "0 item(s)". */
    public String getMiniCartBadgeText() {
        if (isPresent(cartBadge)) return getText(cartBadge);
        return "";
    }
}
