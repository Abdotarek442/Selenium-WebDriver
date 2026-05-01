package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class CheckoutPage extends BasePage {

    // ----- Step 1: Billing details -----
    private final By newAddressRadio        = By.cssSelector("input[name='payment_address'][value='new']");
    private final By bFirstName             = By.id("input-payment-firstname");
    private final By bLastName              = By.id("input-payment-lastname");
    private final By bCompany               = By.id("input-payment-company");
    private final By bAddress1              = By.id("input-payment-address-1");
    private final By bCity                  = By.id("input-payment-city");
    private final By bPostcode              = By.id("input-payment-postcode");
    private final By bCountry               = By.id("input-payment-country");
    private final By bRegion                = By.id("input-payment-zone");
    private final By billingContinue        = By.id("button-payment-address");
    private final By billingAddressDropdown = By.id("input-payment-address");

    // ----- Step 2: Delivery details -----
    private final By existingAddressRadio    = By.cssSelector("input[name='shipping_address'][value='existing']");
    private final By newShippingAddressRadio = By.cssSelector("input[name='shipping_address'][value='new']");
    private final By shippingAddressDropdown = By.id("input-shipping-address");
    private final By shippingContinue        = By.id("button-shipping-address");

    // ----- Step 3: Delivery method -----
    private final By deliveryComment  = By.name("comment");
    private final By deliveryContinue = By.id("button-shipping-method");

    // ----- Step 4: Payment method -----
    private final By agreeTermsCheckbox = By.name("agree");
    private final By paymentContinue    = By.id("button-payment-method");
    private final By termsWarningAlert  = By.cssSelector("div.alert.alert-danger, div.alert.alert-warning");

    // ----- Step 5: Confirm -----
    // OpenCart renders checkout-cart totals in <tfoot>.
    // Sub-Total is the first tfoot row; Total is the last tfoot row.
    // Flat Shipping is identified programmatically in getConfirmFlatShipping().
    private final By confirmSubTotal   = By.cssSelector("#checkout-cart tfoot tr:first-child td:last-child");
    private final By confirmTotal      = By.cssSelector("#checkout-cart tfoot tr:last-child td:last-child");
    private final By confirmOrderTable = By.id("checkout-cart");
    private final By confirmButton     = By.id("button-confirm");

    // Region options that have a real value (populated after country AJAX)
    private final By zoneOptions = By.cssSelector("#input-payment-zone option:not([value=''])");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    // ---- Step 1 ----

    public void selectNewBillingAddress() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(newAddressRadio));
        click(newAddressRadio);
        wait.until(ExpectedConditions.visibilityOfElementLocated(bFirstName));
    }

    public void fillBillingDetails(String fn, String ln, String company,
                                   String addr1, String city, String postcode,
                                   String country, String region) {
        type(bFirstName, fn);
        type(bLastName, ln);
        if (!company.isEmpty()) type(bCompany, company);
        type(bAddress1, addr1);
        type(bCity, city);
        type(bPostcode, postcode);
        selectByVisibleText(bCountry, country);
        // Wait for the zone/region dropdown to be populated via AJAX
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(zoneOptions, 0));
        selectByVisibleText(bRegion, region);
    }

    public void clickBillingContinue() {
        scrollIntoView(billingContinue);
        click(billingContinue);
        wait.until(ExpectedConditions.elementToBeClickable(shippingContinue));
    }

    public String getSelectedBillingAddressText() {
        if (isPresent(billingAddressDropdown)) {
            return new Select(waitVisible(billingAddressDropdown))
                    .getFirstSelectedOption().getText().trim();
        }
        return "";
    }

    // ---- Step 2: Delivery Details ----

    /**
     * Selects the "existing address" radio in the Delivery Details section.
     * Waits for the radio to become clickable after the accordion opens.
     * Falls back silently if the site shows only a new-address form.
     */
    public void selectExistingShippingAddress() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(existingAddressRadio));
            click(existingAddressRadio);
        } catch (Exception ignored) {
            // No existing-address radio — new-address form already visible, just continue
        }
    }

    public void selectNewShippingAddress() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(newShippingAddressRadio));
            click(newShippingAddressRadio);
        } catch (Exception ignored) {
            // Already showing the new-address form
        }
    }

    public String getSelectedShippingAddressText() {
        if (isPresent(shippingAddressDropdown)) {
            return new Select(waitVisible(shippingAddressDropdown))
                    .getFirstSelectedOption().getText().trim();
        }
        return "";
    }

    public void clickShippingContinue() {
        scrollIntoView(shippingContinue);
        click(shippingContinue);
        waitVisible(deliveryContinue);
    }

    // ---- Step 3: Delivery Method ----

    public void enterDeliveryComment(String comment) {
        if (!comment.isEmpty()) type(deliveryComment, comment);
    }

    public void clickDeliveryContinue() {
        scrollIntoView(deliveryContinue);
        click(deliveryContinue);
        waitVisible(paymentContinue);
    }

    // ---- Step 4: Payment Method ----

    public void agreeToTerms() {
        WebElement cb = waitVisible(agreeTermsCheckbox);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].checked = true; arguments[0].dispatchEvent(new Event('change'));", cb);
    }

    /** First click — expects either a T&C warning OR the confirm table. */
    public void clickPaymentContinue() {
        scrollIntoView(paymentContinue);
        click(paymentContinue);
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(termsWarningAlert),
                ExpectedConditions.presenceOfElementLocated(confirmOrderTable)
        ));
    }

    /** Second click (after agreeing to T&C) — waits only for the confirm table. */
    public void clickPaymentContinueFinal() {
        scrollIntoView(paymentContinue);
        click(paymentContinue);
        wait.until(ExpectedConditions.visibilityOfElementLocated(confirmOrderTable));
    }

    public String getTermsWarningText() {
        if (isPresent(termsWarningAlert)) return getText(termsWarningAlert);
        return "";
    }

    // ---- Step 5: Confirm Order ----

    /**
     * Product sub-total from the confirm table (no shipping).
     * This is the first row of the tfoot section of #checkout-cart.
     */
    public String getConfirmSubTotal() {
        if (isPresent(confirmSubTotal)) return getText(confirmSubTotal);
        return "";
    }

    /**
     * Grand total from the confirm table (Sub-Total + Flat Shipping).
     * This is the last row of the tfoot section of #checkout-cart.
     */
    public String getConfirmTotal() {
        if (isPresent(confirmTotal)) return getText(confirmTotal);
        return "";
    }

    /**
     * Returns the flat shipping amount from the confirm table.
     * Iterates tfoot rows and returns the last cell of whichever row's
     * first cell contains "flat" or "shipping" (case-insensitive).
     */
    public String getConfirmFlatShipping() {
        List<WebElement> rows = driver.findElements(
                By.cssSelector("#checkout-cart tfoot tr"));
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.cssSelector("td"));
            if (cells.size() >= 2) {
                String label = cells.get(0).getText().trim().toLowerCase();
                if (label.contains("flat") || label.contains("shipping")) {
                    return cells.get(cells.size() - 1).getText().trim();
                }
            }
        }
        return "";
    }

    public boolean isConfirmOrderSectionVisible() {
        return isPresent(confirmOrderTable);
    }

    public OrderSuccessPage clickConfirmOrder() {
        click(confirmButton);
        return new OrderSuccessPage(driver);
    }
}
