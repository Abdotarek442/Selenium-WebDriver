package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ProductPage extends BasePage {

    private final By productTitle    = By.cssSelector("#content h1");

    /**
     * HP LP3065 (and some other products) expose a required date-picker whose
     * input name follows the pattern  option[NNN]  and type="text".
     */
    private final By deliveryDateInput =
            By.cssSelector("input[name^='option'][type='text']");

    private final By addToCartButton = By.id("button-cart");
    private final By successAlert    = By.cssSelector("div.alert.alert-success");

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public String getTitle() {
        return getText(productTitle);
    }

    /** Returns the raw price text shown on the product detail page. */
    public String getPrice() {
        // Price is in #content .price or a <h2> next to the title block
        By priceLocator = By.cssSelector("#content .price");
        if (isPresent(priceLocator)) {
            return waitVisible(priceLocator).getText()
                    .replaceAll("\\s+", " ").trim();
        }
        return "";
    }

    /**
     * Sets the delivery-date field via JavaScript (avoids datepicker popups)
     * and fires a 'change' event so OpenCart's JS registers the new value.
     *
     * @param yyyyMmDd  e.g. "2026-12-31"
     */
    public void setDeliveryDate(String yyyyMmDd) {
        if (!isPresent(deliveryDateInput)) {
            return; // product does not require a delivery date
        }
        WebElement el = waitVisible(deliveryDateInput);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = arguments[1];", el, yyyyMmDd);
        js.executeScript(
                "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", el);
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
