package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CategoryPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.OrderSuccessPage;
import utils.ConfigReader;
import utils.ExcelReader;
import org.testng.annotations.DataProvider;

public class CheckoutTests extends BaseTest {

    @DataProvider(name = "billingData")
    public Object[][] billingData() {
        return ExcelReader.getSheetData("Checkout");
    }

    @Test(dataProvider = "billingData",
          description = "TC11 - Normal checkout and confirm order")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Login → add an MP3 player → checkout with a new billing " +
            "address → agree to T&C → confirm order → verify the success page.")
    public void normalCheckout(String firstName, String lastName, String company,
                               String address1, String city, String postcode,
                               String country, String region, String comment) {
        loginWithDefaultUser();

        // Add an MP3 player – the iPod Shuffle is the example from the sheet.
        CategoryPage mp3 = home.openMp3ShowAll();
        boolean added = mp3.addProductByName("iPod Shuffle");
        Assert.assertTrue(added, "iPod Shuffle should be available in MP3 Players.");
        Assert.assertTrue(mp3.isSuccessAlertVisible(),
                "Success alert should appear after adding the MP3 player.");

        // Open the cart, then go to checkout
        CartPage cart = new CartPage(driver);
        cart.goToViewCart();
        CheckoutPage checkout = cart.clickCheckoutOnCartPage();

        // Step 1: Billing details — new address
        checkout.selectNewBillingAddress();
        checkout.fillBillingDetails(firstName, lastName, company, address1,
                city, postcode, country, region);
        checkout.clickBillingContinue();

        // Step 2: Delivery details — same as billing (existing)
        checkout.selectExistingShippingAddress();
        checkout.clickShippingContinue();

        // Step 3: Delivery method — comment + continue
        checkout.enterDeliveryComment(comment);
        checkout.clickDeliveryContinue();

        // Step 4: Payment method — agree T&C + continue
        checkout.agreeToTerms();
        checkout.clickPaymentContinue();

        // Step 5: Confirm
        Assert.assertNotNull(checkout.getConfirmTotal(),
                "Confirm-order total should be visible.");
        OrderSuccessPage success = checkout.clickConfirmOrder();

        Assert.assertTrue(success.getHeading()
                        .toLowerCase().contains("your order has been placed"),
                "Order should be placed successfully.");
        home.logout();
    }

    private void loginWithDefaultUser() {
        LoginPage login = home.goToLogin();
        login.login(ConfigReader.get("valid.email"),
                    ConfigReader.get("valid.password"));
    }
}
