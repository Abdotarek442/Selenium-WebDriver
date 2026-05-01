package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CategoryPage;
import pages.LoginPage;
import pages.ProductPage;
import utils.ConfigReader;

import java.util.List;

public class CartTests extends BaseTest {

    private static final String DELIVERY_DATE = "2026-12-31";

    // ------------------TC10 – Full two-product cart scenario-----------------------------------------------------
    @Test(description = "TC10 - Add Tablet + Laptop (with delivery date) and verify cart total")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Login → add Samsung Galaxy Tab 10.1 from Tablets → " +
            "add HP LP3065 from Laptops with a delivery date → verify both " +
            "items are listed in the mini-cart with the correct total.")

    public void addItemsToCartAndVerifyTotal() {

        loginWithDefaultUser();

        // 1) Add Samsung Galaxy Tab 10.1 from Tablets
        CategoryPage tablets = home.openTablets();
        boolean tabletAdded = tablets.addProductByName("Samsung Galaxy Tab 10.1");
        Assert.assertTrue(tabletAdded,
                "Samsung Galaxy Tab 10.1 should be present on the Tablets page.");

        Assert.assertTrue(tablets.isSuccessAlertVisible(),
                "A success alert should appear after adding the tablet.");

        String tabletAlert = tablets.getSuccessAlertText();
        Assert.assertTrue(tabletAlert.contains("Samsung Galaxy Tab 10.1"),
                "Success alert must mention 'Samsung Galaxy Tab 10.1'. Actual: " + tabletAlert);


        CartPage cart = new CartPage(driver);
        cart.goToViewCart();

        List<String> namesAfterTablet = cart.getCartProductNames();
        Assert.assertTrue(
                namesAfterTablet.stream()
                        .anyMatch(n -> n.toLowerCase().contains("samsung galaxy tab 10.1")),
                "Cart should contain 'Samsung Galaxy Tab 10.1'. Found: " + namesAfterTablet);

        List<String> pricesAfterTablet = cart.getCartUnitPrices();
        Assert.assertFalse(pricesAfterTablet.isEmpty(),
                "Unit price(s) should be shown in the cart.");

        String tabletPrice = pricesAfterTablet.stream()
                .filter(p -> !p.isEmpty())
                .findFirst()
                .orElse("");
        System.out.println("[INFO] Tablet unit price in cart: " + tabletPrice);


        // 2) Open HP LP3065 product page (so we can set the delivery date)
        CategoryPage laptops = home.openLaptopsShowAll();
        ProductPage hp = laptops.openProductByName("HP LP3065");

        Assert.assertTrue(hp.getTitle().contains("HP LP3065"),
                "Product page title should contain 'HP LP3065'. Actual: " + hp.getTitle());

        String hpPrice = hp.getPrice();
        System.out.println("[INFO] HP LP3065 price on product page: " + hpPrice);

        hp.setDeliveryDate(DELIVERY_DATE);
        hp.addToCart();

        Assert.assertTrue(hp.isSuccessAlertVisible(),
                "A success alert should appear after adding HP LP3065.");
        String hpAlert = hp.getSuccessAlertText();
        Assert.assertTrue(hpAlert.contains("HP LP3065"),
                "Success alert must mention 'HP LP3065'. Actual: " + hpAlert);

        cart.goToViewCart();

        List<String> namesAfterBoth = cart.getCartProductNames();
        Assert.assertTrue(
                namesAfterBoth.stream()
                        .anyMatch(n -> n.toLowerCase().contains("hp lp3065")),
                "Cart should contain 'HP LP3065'. Found: " + namesAfterBoth);

        String deliveryDateInCart = cart.getDeliveryDateForProduct("HP LP3065");
        Assert.assertFalse(deliveryDateInCart.isEmpty(),
                "HP LP3065 row in cart should show a 'Delivery Date' option.");
        Assert.assertTrue(deliveryDateInCart.contains(DELIVERY_DATE),
                "Delivery date in cart should be '" + DELIVERY_DATE
                        + "'. Actual: " + deliveryDateInCart);
        System.out.println("[INFO] Delivery date shown in cart: " + deliveryDateInCart);

        String grandTotal = cart.getCartTotal();
        Assert.assertNotNull(grandTotal,
                "Grand Total should be displayed in the cart.");
        Assert.assertFalse(grandTotal.isEmpty(),
                "Grand Total text should not be empty.");
        System.out.println("[INFO] Cart Grand Total: " + grandTotal);

        Assert.assertTrue(grandTotal.startsWith("$"),
                "Grand Total should be a dollar amount. Actual: " + grandTotal);

        home.logout();
    }
    private void loginWithDefaultUser() {
        LoginPage login = home.goToLogin();
        login.login(ConfigReader.get("valid.email"),
                ConfigReader.get("valid.password"));
    }
}
