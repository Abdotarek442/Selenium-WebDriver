package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Handles the checkout flow on the demo site.
 * The flow has these accordion sections:
 * 1. Billing details
 * 2. Delivery details
 * 3. Delivery method
 * 4. Payment method
 * 5. Confirm order
 */
public class CheckoutPage extends BasePage {

    // ----- Step 1: Billing details (new address) -----
    private final By newAddressRadio = By.cssSelector("input[name='payment_address'][value='new']");
    private final By bFirstName = By.id("input-payment-firstname");
    private final By bLastName = By.id("input-payment-lastname");
    private final By bCompany = By.id("input-payment-company");
    private final By bAddress1 = By.id("input-payment-address-1");
    private final By bCity = By.id("input-payment-city");
    private final By bPostcode = By.id("input-payment-postcode");
    private final By bCountry = By.id("input-payment-country");
    private final By bRegion = By.id("input-payment-zone");
    private final By billingContinue = By.id("button-payment-address");
    private final By billingAddressDropdown = By.id("input-payment-address");

    // ----- Step 2: Delivery details -----
    private final By existingAddressRadio = By.cssSelector("input[name='shipping_address'][value='existing']");
    private final By newShippingAddressRadio = By.cssSelector("input[name='shipping_address'][value='new']");
    private final By shippingAddressDropdown = By.id("input-shipping-address");
    private final By shippingContinue = By.id("button-shipping-address");

    // ----- Step 3: Delivery method -----
    private final By deliveryComment = By.name("comment");
    private final By deliveryContinue = By.id("button-shipping-method");

    // ----- Step 4: Payment method -----
    private final By agreeTermsCheckbox = By.name("agree");
    private final By paymentContinue = By.id("button-payment-method");
    private final By termsWarningAlert = By.cssSelector("div.alert.alert-danger, div.alert.alert-warning");

    // ----- Step 5: Confirm -----
    private final By confirmTotal = By.xpath("//table[@id='checkout-cart']//tr[td[contains(.,'Total')]]/td[2]");
    private final By confirmFlatShipping = By.xpath("//table[@id='checkout-cart']//tr[td[contains(normalize-space(),'Flat Shipping')]]/td[last()]");
    private final By confirmOrderTable = By.id("checkout-cart");
    private final By confirmButton = By.id("button-confirm");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public void selectNewBillingAddress() {
        if (isPresent(newAddressRadio)) {
            click(newAddressRadio);
        }
        // Wait for the form to slide into view (accordion animation)
        waitVisible(bFirstName);
    }

    public void fillBillingDetails(String fn, String ln, String company,
            String addr1, String city, String postcode,
            String country, String region) {
        type(bFirstName, fn);
        type(bLastName, ln);
        if (!company.isEmpty())
            type(bCompany, company);
        type(bAddress1, addr1);
        type(bCity, city);
        type(bPostcode, postcode);
        selectByVisibleText(bCountry, country);
        // small wait for the regions dropdown to populate
        try {
            Thread.sleep(700);
        } catch (InterruptedException ignored) {
        }
        selectByVisibleText(bRegion, region);
    }

    public void clickBillingContinue() {
        click(billingContinue);
    }

    /** Returns the selected billing address option text (non-empty only when dropdown is shown). */
    public String getSelectedBillingAddressText() {
        if (isPresent(billingAddressDropdown)) {
            return new org.openqa.selenium.support.ui.Select(
                    waitVisible(billingAddressDropdown)).getFirstSelectedOption().getText().trim();
        }
        return "";
    }

    public void selectExistingShippingAddress() {
        if (isPresent(existingAddressRadio)) {
            click(existingAddressRadio);
        }
    }

    public void selectNewShippingAddress() {
        if (isPresent(newShippingAddressRadio)) {
            click(newShippingAddressRadio);
        }
    }

    /** Returns the selected shipping address option text (non-empty only when dropdown is shown). */
    public String getSelectedShippingAddressText() {
        if (isPresent(shippingAddressDropdown)) {
            return new org.openqa.selenium.support.ui.Select(
                    waitVisible(shippingAddressDropdown)).getFirstSelectedOption().getText().trim();
        }
        return "";
    }

    public void clickShippingContinue() {
        click(shippingContinue);
    }

    public void enterDeliveryComment(String comment) {
        if (!comment.isEmpty())
            type(deliveryComment, comment);
    }

    public void clickDeliveryContinue() {
        click(deliveryContinue);
    }

    public void agreeToTerms() {
        click(agreeTermsCheckbox);
    }

    public void clickPaymentContinue() {
        click(paymentContinue);
    }

    /** Returns the T&C warning/danger alert text, or empty string if none shown. */
    public String getTermsWarningText() {
        if (isPresent(termsWarningAlert)) return getText(termsWarningAlert);
        return "";
    }

    public String getConfirmTotal() {
        return getText(confirmTotal);
    }

    /** Returns the Flat Shipping Rate amount from the confirm table, or empty string. */
    public String getConfirmFlatShipping() {
        if (isPresent(confirmFlatShipping)) return getText(confirmFlatShipping);
        return "";
    }

    /** True when the confirm-order summary table is present on the page. */
    public boolean isConfirmOrderSectionVisible() {
        return isPresent(confirmOrderTable);
    }

    public OrderSuccessPage clickConfirmOrder() {
        click(confirmButton);
        return new OrderSuccessPage(driver);
    }
}
