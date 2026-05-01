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

import java.util.List;

public class CheckoutTests extends BaseTest {

        @DataProvider(name = "billingData")
        public Object[][] billingData() {
                return ExcelReader.getSheetData("Checkout");
        }

        @Test(dataProvider = "billingData", description = "TC11 - Normal checkout and confirm order")
        @Severity(SeverityLevel.BLOCKER)
        @Description("Login → MP3 Players → Show All → Add iPod Shuffle → success alert → " +
                        "mini-cart check → View Cart → Checkout → billing new address → " +
                        "shipping new address → delivery comment → T&C warning → confirm → success.")
        public void normalCheckout(String firstName, String lastName, String company,
                        String address1, String city, String postcode,
                        String country, String region, String comment) {

                // Step 1: Login
                loginWithDefaultUser();

                // Step 2: Navigate to MP3 Players → Show All MP3 Players
                CategoryPage mp3 = home.openMp3ShowAll();

                // Step 3: Add iPod Shuffle to cart
                boolean added = mp3.addProductByName("iPod Shuffle");
                Assert.assertTrue(added, "iPod Shuffle should be available in the MP3 Players category.");

                // Step 4: Success info message should mention "iPod Shuffle"
                Assert.assertTrue(mp3.isSuccessAlertVisible(),
                                "Success alert should appear after adding iPod Shuffle.");
                Assert.assertTrue(mp3.getSuccessAlertText().toLowerCase().contains("ipod shuffle"),
                                "Success alert should mention iPod Shuffle. Got: " + mp3.getSuccessAlertText());

                // Step 5: Open shopping cart header — check item name and price
                CartPage cart = new CartPage(driver);
                Assert.assertTrue(cart.getMiniCartBadgeCount() >= 1,
                                "Mini-cart badge should show at least 1 item after adding to cart.");
                List<String> miniNames = cart.getMiniCartItemNames();
                Assert.assertTrue(miniNames.stream().anyMatch(n -> n.toLowerCase().contains("ipod shuffle")),
                                "iPod Shuffle should appear in the mini-cart dropdown. Found: " + miniNames);
                List<String> miniPrices = cart.getMiniCartItemPrices();
                Assert.assertFalse(miniPrices.isEmpty(),
                                "Mini-cart should show a price for the added item.");

                // Step 6: Click "View Cart" — verify iPod Shuffle is listed in the full cart
                cart.clickViewCart();
                List<String> cartNames = cart.getCartProductNames();
                Assert.assertTrue(cartNames.stream().anyMatch(n -> n.toLowerCase().contains("ipod shuffle")),
                                "iPod Shuffle should appear on the cart page. Found: " + cartNames);
                String cartTotal = cart.getCartTotal();
                Assert.assertFalse(cartTotal.isEmpty(), "Cart total should be visible on the cart page.");

                // Step 7: Click Checkout button
                CheckoutPage checkout = cart.clickCheckoutOnCartPage();

                // Step 8: Fill billing details with a new address
                checkout.selectNewBillingAddress();
                checkout.fillBillingDetails(firstName, lastName, company, address1,
                                city, postcode, country, region);

                // Step 9 & 10: Click Continue — verify address dropdown reflects the new address
                checkout.clickBillingContinue();
                String billingSelected = checkout.getSelectedBillingAddressText();
                if (!billingSelected.isEmpty()) {
                        Assert.assertTrue(
                                        billingSelected.toLowerCase().contains(firstName.toLowerCase())
                                        || billingSelected.toLowerCase().contains(address1.toLowerCase()),
                                        "Billing dropdown should show the new address. Got: " + billingSelected);
                }

                // Step 11: Shipping details — choose the new address and click Continue
                checkout.selectNewShippingAddress();
                String shippingSelected = checkout.getSelectedShippingAddressText();
                if (!shippingSelected.isEmpty()) {
                        Assert.assertTrue(
                                        shippingSelected.toLowerCase().contains(firstName.toLowerCase())
                                        || shippingSelected.toLowerCase().contains(address1.toLowerCase()),
                                        "Shipping dropdown should show the new address. Got: " + shippingSelected);
                }
                checkout.clickShippingContinue();

                // Step 12 & 13: Delivery method section — add comment and click Continue
                checkout.enterDeliveryComment(comment);
                checkout.clickDeliveryContinue();

                // Step 14: Payment method — verify T&C warning, then agree and click Continue
                checkout.clickPaymentContinue();
                String warning = checkout.getTermsWarningText();
                Assert.assertFalse(warning.isEmpty(),
                                "Warning should appear when clicking Continue without agreeing to T&C.");
                Assert.assertTrue(warning.toLowerCase().contains("terms") || warning.toLowerCase().contains("conditions"),
                                "Warning should mention Terms & Conditions. Got: " + warning);
                checkout.agreeToTerms();
                checkout.clickPaymentContinue();

                // Step 15: Confirm Order section — prices should match the cart
                Assert.assertTrue(checkout.isConfirmOrderSectionVisible(),
                                "Confirm Order section should be visible after payment step.");
                String confirmTotal = checkout.getConfirmTotal();
                Assert.assertFalse(confirmTotal.isEmpty(), "Confirm-order total should be visible.");
                Assert.assertEquals(confirmTotal, cartTotal,
                                "Confirm-order total should match the cart-page total.");

                // Step 16: Total price includes the Flat Shipping Rate
                Assert.assertFalse(checkout.getConfirmFlatShipping().isEmpty(),
                                "Confirm Order table should include a Flat Shipping Rate row.");

                // Step 17: Click Confirm Order
                OrderSuccessPage success = checkout.clickConfirmOrder();

                // Step 18: "Your order has been placed!" and mini-cart shows 0 items
                Assert.assertTrue(success.getHeading().toLowerCase().contains("your order has been placed"),
                                "Order success heading mismatch. Got: " + success.getHeading());
                String badge = success.getMiniCartBadgeText();
                Assert.assertTrue(badge.contains("0"),
                                "Mini-cart badge should show 0 items after placing the order. Got: " + badge);

                // Step 19: Logout
                home.logout();
        }

        private void loginWithDefaultUser() {
                LoginPage login = home.goToLogin();
                login.login(ConfigReader.get("valid.email"),
                                ConfigReader.get("valid.password"));
        }
}
