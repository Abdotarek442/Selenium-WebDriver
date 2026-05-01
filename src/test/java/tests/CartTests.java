package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CategoryPage;
import pages.LoginPage;
import pages.ProductPage;
import utils.ConfigReader;
import utils.ExcelReader;

import java.util.List;

public class CartTests extends BaseTest {

    @DataProvider(name = "cartData")
    public Object[][] cartData() {
        return ExcelReader.getSheetData("CartItems");
    }

    // ------------------TC10 – Full two-product cart scenario------------------
    @Test(dataProvider = "cartData",
            description = "TC10 - Add Tablet + Laptop (with delivery date) and verify cart total")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Login → add tablet from Tablets category → " +
            "add laptop from Laptops with a delivery date → verify both " +
            "items and the delivery date in the cart, plus a dollar total.")
    public void addItemsToCartAndVerifyTotal(String tabletProduct, String laptopProduct, String deliveryDate) {

        loginWithDefaultUser();

        // 1) Add tablet
        CategoryPage tablets = home.openTablets();
        boolean tabletAdded = tablets.addProductByName(tabletProduct);
        Assert.assertTrue(tabletAdded,
                tabletProduct + " should be present on the Tablets page.");
        Assert.assertTrue(tablets.isSuccessAlertVisible(),
                "A success alert should appear after adding the tablet.");
        String tabletAlert = tablets.getSuccessAlertText();
        Assert.assertTrue(tabletAlert.contains(tabletProduct),
                "Success alert must mention '" + tabletProduct + "'. Actual: " + tabletAlert);

        CartPage cart = new CartPage(driver);
        cart.goToViewCart();

        List<String> namesAfterTablet = cart.getCartProductNames();
        Assert.assertTrue(
                namesAfterTablet.stream()
                        .anyMatch(n -> n.toLowerCase().contains(tabletProduct.toLowerCase())),
                "Cart should contain '" + tabletProduct + "'. Found: " + namesAfterTablet);

        List<String> pricesAfterTablet = cart.getCartUnitPrices();
        Assert.assertFalse(pricesAfterTablet.isEmpty(),
                "Unit price(s) should be shown in the cart.");

        // 2) Open laptop product page and set delivery date
        CategoryPage laptops = home.openLaptopsShowAll();
        ProductPage laptop = laptops.openProductByName(laptopProduct);

        Assert.assertTrue(laptop.getTitle().contains(laptopProduct),
                "Product page title should contain '" + laptopProduct + "'. Actual: " + laptop.getTitle());

        laptop.setDeliveryDate(deliveryDate);
        laptop.addToCart();

        Assert.assertTrue(laptop.isSuccessAlertVisible(),
                "A success alert should appear after adding " + laptopProduct + ".");
        String laptopAlert = laptop.getSuccessAlertText();
        Assert.assertTrue(laptopAlert.contains(laptopProduct),
                "Success alert must mention '" + laptopProduct + "'. Actual: " + laptopAlert);

        cart.goToViewCart();

        List<String> namesAfterBoth = cart.getCartProductNames();
        Assert.assertTrue(
                namesAfterBoth.stream()
                        .anyMatch(n -> n.toLowerCase().contains(laptopProduct.toLowerCase())),
                "Cart should contain '" + laptopProduct + "'. Found: " + namesAfterBoth);

        String deliveryDateInCart = cart.getDeliveryDateForProduct(laptopProduct);
        Assert.assertFalse(deliveryDateInCart.isEmpty(),
                laptopProduct + " row in cart should show a 'Delivery Date' option.");
        Assert.assertTrue(deliveryDateInCart.contains(deliveryDate),
                "Delivery date in cart should be '" + deliveryDate
                        + "'. Actual: " + deliveryDateInCart);

        String grandTotal = cart.getCartTotal();
        Assert.assertNotNull(grandTotal, "Grand Total should be displayed in the cart.");
        Assert.assertFalse(grandTotal.isEmpty(), "Grand Total text should not be empty.");
        Assert.assertTrue(grandTotal.startsWith("$"),
                "Grand Total should be a dollar amount. Actual: " + grandTotal);

        home.logout();
    }

    private void loginWithDefaultUser() {
        LoginPage login = home.goToLogin();
        login.login(ConfigReader.get("valid.email"), ConfigReader.get("valid.password"));
    }
}
