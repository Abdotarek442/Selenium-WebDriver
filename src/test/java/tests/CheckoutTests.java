package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CategoryPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.OrderSuccessPage;
import pages.ProductPage;
import utils.ConfigReader;
import utils.ExcelReader;

import java.util.List;

public class CheckoutTests extends BaseTest {

    @DataProvider(name = "billingData")
    public Object[][] billingData() {
        return ExcelReader.getSheetData("Checkout");
    }

    @Test(dataProvider = "billingData", description = "TC11 - Normal checkout and confirm order")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Login → clear cart → add product → mini-cart check → View Cart → " +
            "Checkout → fill new billing address → verify address dropdown → " +
            "Delivery Details: choose the saved address → Delivery Method: add comment → " +
            "Payment: T&C warning, agree, continue → Confirm Order: prices match cart + " +
            "flat shipping row present → Place Order → success page + 0-item badge → Logout.")
    public void normalCheckout(String firstName, String lastName, String company,
            String address1, String city, String postcode,
            String country, String region, String comment,
            String productName, String productCategory, String deliveryDate) {

        // Step 1: Login
        loginWithDefaultUser();

        // Step 2: Clear leftover cart items from any previous run
        CartPage cart = new CartPage(driver);
        cart.clearCart();

        // Step 3: Navigate to the correct category and add the product
        CategoryPage categoryPage = home.openCategoryShowAll(productCategory);

        if (deliveryDate.isEmpty()) {
            // No required options — add directly from the category listing
            boolean added = categoryPage.addProductByName(productName);
            Assert.assertTrue(added,
                    productName + " should be available in the " + productCategory + " category.");
            Assert.assertTrue(categoryPage.isSuccessAlertVisible(),
                    "Success alert should appear after adding " + productName + ".");
            Assert.assertTrue(
                    categoryPage.getSuccessAlertText().toLowerCase().contains(productName.toLowerCase()),
                    "Success alert should mention " + productName
                            + ". Got: " + categoryPage.getSuccessAlertText());
        } else {
            // Product requires a delivery date — open the product page first
            ProductPage productPage = categoryPage.openProductByName(productName);
            Assert.assertTrue(productPage.getTitle().contains(productName),
                    "Product page title should contain '" + productName
                            + "'. Actual: " + productPage.getTitle());
            productPage.setDeliveryDate(deliveryDate);
            productPage.addToCart();
            Assert.assertTrue(productPage.isSuccessAlertVisible(),
                    "Success alert should appear after adding " + productName + ".");
            Assert.assertTrue(
                    productPage.getSuccessAlertText().toLowerCase().contains(productName.toLowerCase()),
                    "Success alert should mention " + productName
                            + ". Got: " + productPage.getSuccessAlertText());
        }

        // Step 4: Mini-cart badge and item name check
        Assert.assertTrue(cart.getMiniCartBadgeCount() >= 1,
                "Mini-cart badge should show at least 1 item after adding to cart.");
        List<String> miniNames = cart.getMiniCartItemNames();
        Assert.assertTrue(
                miniNames.stream().anyMatch(n -> n.toLowerCase().contains(productName.toLowerCase())),
                productName + " should appear in the mini-cart. Found: " + miniNames);
        List<String> miniPrices = cart.getMiniCartItemPrices();
        Assert.assertFalse(miniPrices.isEmpty(),
                "Mini-cart should show a price for the added item.");

        // Step 5: View Cart — verify product is listed and capture the cart total
        cart.clickViewCart();
        List<String> cartNames = cart.getCartProductNames();
        Assert.assertTrue(
                cartNames.stream().anyMatch(n -> n.toLowerCase().contains(productName.toLowerCase())),
                productName + " should appear on the cart page. Found: " + cartNames);
        String cartTotal = cart.getCartTotal();
        Assert.assertFalse(cartTotal.isEmpty(), "Cart total should be visible on the cart page.");

        // Step 6: Start checkout
        CheckoutPage checkout = cart.clickCheckoutOnCartPage();

        // Steps 7-8: Fill new billing address and click Continue
        checkout.selectNewBillingAddress();
        checkout.fillBillingDetails(firstName, lastName, company,
                address1, city, postcode, country, region);
        checkout.clickBillingContinue();

        // Step 9: Verify address dropdown shows the new address
        String billingSelected = checkout.getSelectedBillingAddressText();
        if (!billingSelected.isEmpty()) {
            Assert.assertTrue(
                    billingSelected.toLowerCase().contains(firstName.toLowerCase())
                            || billingSelected.toLowerCase().contains(address1.toLowerCase()),
                    "Billing dropdown should show the new address. Got: " + billingSelected);
        }

        // Step 10: Click Continue on billing (already done in clickBillingContinue)

        // Step 11: Delivery Details — choose the newly added address and click Continue
        checkout.selectExistingShippingAddress();
        String shippingSelected = checkout.getSelectedShippingAddressText(firstName, lastName,
                address1, city, region, country);
        if (!shippingSelected.isEmpty()) {
            Assert.assertTrue(
                    shippingSelected.toLowerCase().contains(firstName.toLowerCase())
                            || shippingSelected.toLowerCase().contains(address1.toLowerCase()),
                    "Delivery Details dropdown should reflect the billing address. Got: " + shippingSelected);
        }
        checkout.clickShippingContinue();

        // Steps 12-13: Delivery Method section — add comment and click Continue
        checkout.enterDeliveryComment(comment);
        checkout.clickDeliveryContinue();

        // Step 14: Payment Method — verify T&C warning appears, then agree and continue
        checkout.clickPaymentContinue();
        String warning = checkout.getTermsWarningText();
        Assert.assertFalse(warning.isEmpty(),
                "Warning should appear when clicking Continue without agreeing to T&C.");
        Assert.assertTrue(
                warning.toLowerCase().contains("terms") || warning.toLowerCase().contains("conditions"),
                "Warning should mention Terms & Conditions. Got: " + warning);
        checkout.agreeToTerms();
        checkout.clickPaymentContinueFinal();

        // Step 15: Confirm Order section — product sub-total must match the cart total
        Assert.assertTrue(checkout.isConfirmOrderSectionVisible(),
                "Confirm Order section should be visible after payment step.");

        String confirmTotal = checkout.getConfirmTotal();
        String headerCartPrice = checkout.getCartButtonPrice();

        Assert.assertEquals(confirmTotal, headerCartPrice,
                "The cart button price should match the final Confirm Order total.");

        // Step 16: Grand total must include the Flat Shipping Rate row
        String flatShipping = checkout.getConfirmFlatShipping();

        Assert.assertFalse(flatShipping.isEmpty(),
                "Confirm Order table should include a Flat Shipping Rate row.");

        // Step 17: Click Confirm Order
        OrderSuccessPage success = checkout.clickConfirmOrder();

        // Step 18: Order success page + empty mini-cart badge
        Assert.assertTrue(
                success.getHeading().toLowerCase().contains("your order has been placed"),
                "Order success heading mismatch. Got: " + success.getHeading());
        String badge = success.getMiniCartBadgeText();
        Assert.assertTrue(badge.contains("0"),
                "Mini-cart badge should show 0 items after placing the order. Got: " + badge);

        // Step 19: Logout
        home.logout();
    }

    private void loginWithDefaultUser() {
        LoginPage login = home.goToLogin();
        login.login(ConfigReader.get("valid.email"), ConfigReader.get("valid.password"));
    }
}
