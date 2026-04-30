package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage extends BasePage {

    private final By cartButton = By.id("cart");
    private final By cartItemsInDropdown =
            By.cssSelector("ul.dropdown-menu-right table.table tr");
    private final By viewCartLink =
            By.cssSelector("ul.dropdown-menu-right a[href*='route=checkout/cart']");
    private final By checkoutLinkInDropdown =
            By.cssSelector("ul.dropdown-menu-right a[href*='route=checkout/checkout']");

    // On the cart page itself
    private final By cartProductRows =
            By.cssSelector("div#content table.table-bordered tbody tr");
    private final By totalAmount =
            By.xpath("//table[contains(@class,'table-bordered')]" +
                    "//tr[td[contains(.,'Total')]]/td[2]");
    private final By emptyCartMessage =
            By.xpath("//div[@id='content']//p[contains(.,'shopping cart is empty')]");
    private final By checkoutButton =
            By.xpath("//div[@id='content']//a[normalize-space()='Checkout']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public void openMiniCart() {
        click(cartButton);
    }

    public List<WebElement> getMiniCartItems() {
        openMiniCart();
        return findAll(cartItemsInDropdown);
    }

    public CartPage goToViewCart() {
        openMiniCart();
        click(viewCartLink);
        return this;
    }

    public CheckoutPage goToCheckoutFromMiniCart() {
        openMiniCart();
        click(checkoutLinkInDropdown);
        return new CheckoutPage(driver);
    }

    public CheckoutPage clickCheckoutOnCartPage() {
        click(checkoutButton);
        return new CheckoutPage(driver);
    }

    public int getCartItemCount() {
        return findAll(cartProductRows).size();
    }

    public String getCartTotal() {
        return getText(totalAmount);
    }

    public boolean isCartEmpty() {
        return isPresent(emptyCartMessage);
    }
}
