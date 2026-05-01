package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.ConfigReader;

import java.util.ArrayList;
import java.util.List;

public class CartPage extends BasePage {

    // ------------------------------------------------------------------ mini-cart
    // header button
    private final By cartButton = By.id("cart");
    // OpenCart renders item count inside the cart button as: <span class="hidden-xs
    // ...">1 item(s)</span>
    private final By cartBadge = By.cssSelector("#cart button span");
    // The Bootstrap dropdown menu that opens when the cart button is clicked
    private final By miniCartDropdown = By.cssSelector("#cart ul.dropdown-menu");
    private final By miniCartNames = By.cssSelector("#cart ul.dropdown-menu li table td.text-left a");
    private final By miniCartPrices = By.cssSelector("#cart ul.dropdown-menu li table td.text-right");
    private final By viewCartLink = By
            .cssSelector("#cart ul.dropdown-menu li:last-child a[href*='route=checkout/cart']");

    // ------------------------------------------------------------------ full cart
    // page selectors

    /**
     * Every product row in the cart table body.
     * Structure per row:
     * td.text-left → image
     * td.text-left → name (a) + options (small)
     * td.text-right → model
     * td.text-right → quantity input
     * td.text-right → unit price
     * td.text-right → line total
     */
    private final By cartProductRows = By.cssSelector("div#content table.table-bordered tbody tr");

    /**
     * Grand Total row – targets the
     * <td>sibling of the cell whose text is
     * exactly "Total:" in the totals table below the product rows.
     */
    private final By totalAmount = By.xpath("//div[@id='content']//table[not(.//thead)]"
            + "//tr[td[normalize-space()='Total:']]/td[last()]");

    private final By emptyCartMessage = By.xpath("//div[@id='content']//p[contains(.,'shopping cart is empty')]");

    private final By checkoutButton = By.xpath("//div[@id='content']//a[normalize-space()='Checkout']");

    /** The cart-page content landmark — used to confirm the page has loaded. */
    private final By cartContentLandmark = By.cssSelector("div#content");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    // ------------------------------------------------------------------ navigation

    /**
     * Navigates directly to the cart page URL.
     * This is more reliable than clicking through the mini-cart dropdown,
     * which is hidden behind a CSS animation and often causes TimeoutExceptions.
     */
    public CartPage goToViewCart() {
        String baseUrl = ConfigReader.get("url"); // e.g. ...index.php?route=common/home
        String cartUrl = baseUrl.replaceAll("route=[^&]+", "route=checkout/cart");
        driver.get(cartUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartContentLandmark));
        return this;
    }

    /**
     * Opens the mini-cart dropdown (top-right cart icon).
     * Kept for cases where you only need to read the mini-cart badge / count.
     */
    public void openMiniCart() {
        click(cartButton);
    }

    /**
     * Opens the mini-cart dropdown only if it is not already visible,
     * then waits for the dropdown menu to fully appear.
     */
    private void openMiniCartDropdown() {
        // Only click if the dropdown isn't already open
        if (driver.findElements(miniCartDropdown).isEmpty() ||
                !driver.findElements(miniCartDropdown).get(0).isDisplayed()) {
            click(cartButton);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(miniCartDropdown));
    }

    /**
     * Number shown in the cart button span, e.g. "1 item(s)" → 1.
     * Waits for the AJAX update to change the count away from 0.
     */
    public int getMiniCartBadgeCount() {
        try {
            wait.until(ExpectedConditions.textMatches(cartBadge, java.util.regex.Pattern.compile(".*[1-9].*")));
            String txt = getText(cartBadge);
            return Integer.parseInt(txt.replaceAll("[^0-9]", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }

    /** Opens the mini-cart dropdown and returns every visible product name. */
    public List<String> getMiniCartItemNames() {
        openMiniCartDropdown();
        wait.until(ExpectedConditions.visibilityOfElementLocated(miniCartNames));
        List<String> names = new ArrayList<>();
        for (WebElement el : findAll(miniCartNames))
            names.add(el.getText().trim());
        return names;
    }

    /** Opens the mini-cart dropdown and returns every visible unit price. */
    public List<String> getMiniCartItemPrices() {
        openMiniCartDropdown();
        List<String> prices = new ArrayList<>();
        for (WebElement el : findAll(miniCartPrices))
            prices.add(el.getText().trim());
        return prices;
    }

    /** Opens the mini-cart dropdown and clicks the "View Cart" link. */
    public CartPage clickViewCart() {
        openMiniCartDropdown();
        wait.until(ExpectedConditions.elementToBeClickable(viewCartLink));
        click(viewCartLink);
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartContentLandmark));
        return this;
    }

    public CheckoutPage goToCheckoutFromCart() {
        goToViewCart();
        click(checkoutButton);
        return new CheckoutPage(driver);
    }

    public CheckoutPage clickCheckoutOnCartPage() {
        click(checkoutButton);
        return new CheckoutPage(driver);
    }

    // ------------------------------------------------------------------ cart page
    // queries

    /** Number of product rows on the full cart page. */
    public int getCartItemCount() {
        return findAll(cartProductRows).size();
    }

    /**
     * Product names from the full cart table, in row order.
     * Name is the first anchor inside a td.text-left cell.
     */
    public List<String> getCartProductNames() {
        List<String> names = new ArrayList<>();
        for (WebElement row : findAll(cartProductRows)) {
            List<WebElement> links = row.findElements(By.cssSelector("td.text-left a"));
            if (!links.isEmpty()) {
                names.add(links.get(0).getText().trim());
            }
        }
        return names;
    }

    /**
     * Unit price of every product row (second-to-last td.text-right per row).
     */
    public List<String> getCartUnitPrices() {
        List<String> prices = new ArrayList<>();
        for (WebElement row : findAll(cartProductRows)) {
            List<WebElement> cells = row.findElements(By.cssSelector("td.text-right"));
            if (cells.size() >= 2) {
                // second-to-last = unit price, last = line total
                prices.add(cells.get(cells.size() - 2).getText().trim());
            }
        }
        return prices;
    }

    /**
     * Returns the "Delivery Date: YYYY-MM-DD" text from the cart row whose
     * product name CONTAINS the given fragment.
     * Returns an empty string if the row has no delivery-date option.
     */
    public String getDeliveryDateForProduct(String productNameFragment) {
        for (WebElement row : findAll(cartProductRows)) {
            List<WebElement> links = row.findElements(By.cssSelector("td.text-left a"));
            if (links.isEmpty())
                continue;
            if (!links.get(0).getText().toLowerCase()
                    .contains(productNameFragment.toLowerCase()))
                continue;

            for (WebElement small : row.findElements(By.tagName("small"))) {
                String txt = small.getText().trim();
                if (txt.toLowerCase().startsWith("delivery date")) {
                    return txt; // e.g. "Delivery Date: 2026-12-31"
                }
            }
        }
        return "";
    }

    /** Grand-total string shown on the cart page, e.g. "$363.99". */
    public String getCartTotal() {
        return getText(totalAmount);
    }

    public boolean isCartEmpty() {
        return isPresent(emptyCartMessage);
    }

    /**
     * Removes every item from the cart.
     * Navigates to the cart page first, then repeatedly clicks the first
     * Remove button until none remain. Safe to call on an already-empty cart.
     */
    public void clearCart() {
        goToViewCart();
        By removeBtn = By.cssSelector("button[onclick*='cart.remove']");

        List<WebElement> buttons = driver.findElements(removeBtn);
        while (!buttons.isEmpty()) {
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", buttons.get(0));
            int countBefore = buttons.size();
            wait.until(d -> d.findElements(removeBtn).size() < countBefore);
            buttons = driver.findElements(removeBtn);
        }
    }
}
