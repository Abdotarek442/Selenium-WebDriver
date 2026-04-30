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

public class CartTests extends BaseTest {

    @Test(description = "TC10 - Add items to cart and verify mini-cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Login → add Samsung Galaxy Tab 10.1 from Tablets → " +
            "add HP LP3065 from Laptops with a delivery date → verify both " +
            "items are listed in the mini-cart with the correct total.")
    public void addItemsToCartAndVerify() {
        loginWithDefaultUser();

        // 1) Add Samsung Galaxy Tab 10.1 from Tablets
        CategoryPage tablets = home.openTablets();
        boolean tabAdded = tablets.addProductByName("Samsung Galaxy Tab 10.1");
        Assert.assertTrue(tabAdded, "Samsung Galaxy Tab 10.1 should exist in Tablets.");
        Assert.assertTrue(tablets.isSuccessAlertVisible(),
                "Success message should appear after adding the tablet.");
        Assert.assertTrue(tablets.getSuccessAlertText()
                        .contains("Samsung Galaxy Tab 10.1"),
                "Success alert should reference the added product.");

        // 2) Open HP LP3065 product page (so we can set the delivery date)
        CategoryPage laptops = home.openLaptopsShowAll();
        ProductPage hp = laptops.openProductByName("HP LP3065");
        hp.setDeliveryDate("2026-12-31");
        hp.addToCart();
        Assert.assertTrue(hp.isSuccessAlertVisible(),
                "Success message should appear after adding HP LP3065.");

        // 3) Open mini-cart and verify it has at least 2 rows
        CartPage cart = new CartPage(driver);
        Assert.assertTrue(cart.getMiniCartItems().size() >= 2,
                "Mini-cart should contain at least 2 product rows.");

        // 4) Open the full cart and confirm the same
        cart.goToViewCart();
        Assert.assertTrue(cart.getCartItemCount() >= 2,
                "Full cart page should show at least 2 product rows.");
        Assert.assertNotNull(cart.getCartTotal(),
                "Cart total should be displayed.");

        home.logout();
    }

    private void loginWithDefaultUser() {
        LoginPage login = home.goToLogin();
        login.login(ConfigReader.get("valid.email"),
                    ConfigReader.get("valid.password"));
    }
}
