package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class CheckoutPage extends BasePage {

    // ----- Step 1: Billing details -----
    private final By newAddressRadio    = By.cssSelector("input[name='payment_address'][value='new']");
    private final By bFirstName         = By.id("input-payment-firstname");
    private final By bLastName          = By.id("input-payment-lastname");
    private final By bCompany           = By.id("input-payment-company");
    private final By bAddress1          = By.id("input-payment-address-1");
    private final By bCity              = By.id("input-payment-city");
    private final By bPostcode          = By.id("input-payment-postcode");
    private final By bCountry           = By.id("input-payment-country");
    private final By bRegion            = By.id("input-payment-zone");
    private final By billingContinue    = By.id("button-payment-address");
    private final By billingAddressDropdown = By.id("input-payment-address");

    // ----- Step 2: Delivery details -----
    private final By existingAddressRadio  = By.cssSelector("input[name='shipping_address'][value='existing']");
    private final By newShippingAddressRadio = By.cssSelector("input[name='shipping_address'][value='new']");
    private final By shippingAddressDropdown = By.id("input-shipping-address");
    private final By shippingContinue      = By.id("button-shipping-address");

    // ----- Step 3: Delivery method -----
    private final By deliveryComment    = By.name("comment");
    private final By deliveryContinue   = By.id("button-shipping-method");

    // ----- Step 4: Payment method -----
    private final By agreeTermsCheckbox = By.name("agree");
    private final By paymentContinue    = By.id("button-payment-method");
    private final By termsWarningAlert  = By.cssSelector("div.alert.alert-danger, div.alert.alert-warning");

    // ----- Step 5: Confirm -----
    private final By confirmTotal       = By.xpath("//table[@id='checkout-cart']//tr[td[contains(.,'Total')]]/td[2]");
    private final By confirmFlatShipping = By.xpath("//table[@id='checkout-cart']//tr[td[contains(normalize-space(),'Flat Shipping')]]/td[last()]");
    private final By confirmOrderTable  = By.id("checkout-cart");
    private final By confirmButton      = By.id("button-confirm");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    // ---- Step 1 ----

    public void selectNewBillingAddress() {
        // Wait for the step to load and find the radio button
        wait.until(ExpectedConditions.visibilityOfElementLocated(newAddressRadio));

        // Explicitly click 'New Address' to show the hidden form
        click(newAddressRadio);

        // Wait until the 'First Name' field is actually visible before typing
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
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//select[@id='input-payment-zone']/option[text()='" + region + "']")));

        selectByVisibleText(bRegion, region);
    }

    private void waitForRegionOptions() {
        wait.until(driver -> {
            List<WebElement> options = driver.findElements(
                    By.cssSelector("#input-payment-zone option[value!='']"));
            return !options.isEmpty();
        });
    }

    public void clickBillingContinue() {
        scrollIntoView(billingContinue);
        click(billingContinue);
        // Explicitly wait for the next section's button to be CLICKABLE, not just visible
        wait.until(ExpectedConditions.elementToBeClickable(shippingContinue));
    }

    public String getSelectedBillingAddressText() {
        if (isPresent(billingAddressDropdown)) {
            return new Select(waitVisible(billingAddressDropdown))
                    .getFirstSelectedOption().getText().trim();
        }
        return "";
    }

    // ---- Step 2 ----

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

    public String getSelectedShippingAddressText() {
        if (isPresent(shippingAddressDropdown)) {
            return new Select(waitVisible(shippingAddressDropdown))
                    .getFirstSelectedOption().getText().trim();
        }
        return "";
    }

    public void clickShippingContinue() {
        click(shippingContinue);
        // Wait for the delivery-method section to open
        waitVisible(deliveryContinue);
    }

    // ---- Step 3 ----

    public void enterDeliveryComment(String comment) {
        if (!comment.isEmpty()) type(deliveryComment, comment);
    }

    public void clickDeliveryContinue() {
        click(deliveryContinue);
        // Wait for the payment-method section to open
        waitVisible(paymentContinue);
    }

    // ---- Step 4 ----

    public void agreeToTerms() {
        click(agreeTermsCheckbox);
    }

    public void clickPaymentContinue() {
        click(paymentContinue);
        // Wait for either the T&C warning or the confirm-order table
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(termsWarningAlert),
                ExpectedConditions.presenceOfElementLocated(confirmOrderTable)
        ));
    }

    public String getTermsWarningText() {
        if (isPresent(termsWarningAlert)) return getText(termsWarningAlert);
        return "";
    }

    // ---- Step 5 ----

    public String getConfirmTotal() {
        return getText(confirmTotal);
    }

    public String getConfirmFlatShipping() {
        if (isPresent(confirmFlatShipping)) return getText(confirmFlatShipping);
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
