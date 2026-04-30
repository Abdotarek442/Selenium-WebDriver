package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductPage extends BasePage {

    private final By productTitle = By.cssSelector("#content h1");
    // Some products on the demo site have date pickers (e.g. HP LP3065 laptop)
    private final By deliveryDateInput =
            By.cssSelector("input[name^='option'][type='text']");
    private final By addToCartButton = By.id("button-cart");
    private final By successAlert = By.cssSelector("div.alert.alert-success");

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public String getTitle() {
        return getText(productTitle);
    }

    /**
     * Sets the value of the delivery-date input directly via JS, since the
     * built-in datepicker can be flaky on CI machines.
     */
    public void setDeliveryDate(String yyyyMmDd) {
        if (!isPresent(deliveryDateInput)) {
            return; // product does not require a delivery date
        }
        var element = waitVisible(deliveryDateInput);
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].value=arguments[1];", element, yyyyMmDd);
    }

    public void addToCart() {
        click(addToCartButton);
    }

    public boolean isSuccessAlertVisible() {
        return isPresent(successAlert);
    }

    public String getSuccessAlertText() {
        return getText(successAlert);
    }
}
