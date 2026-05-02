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
    private final By cartButton       = By.id("cart");
    private final By cartBadge        = By.cssSelector("#cart button span");
    private final By miniCartDropdown = By.cssSelector("#cart ul.dropdown-menu");
    private final By miniCartNames    = By.cssSelector("#cart ul.dropdown-menu li table td.text-left a");
    private final By miniCartPrices   = By.cssSelector("#cart ul.dropdown-menu li table td.text-right");
    private final By viewCartLink     = By.cssSelector("#cart ul.dropdown-menu li:last-child a[href*='route=checkout/cart']");

    // ------------------------------------------------------------------ full cart
    /**
     * Every product row in the cart table body.
     * td.text-left → image | td.text-left → name + options | td.text-right → model
     * td.text-right → qty | td.text-right → unit price | td.text-right → line total
     */
    private final By cartProductRows = By.cssSelector("div#content table.table-bordered tbody tr");

    /**
     * The cart-page "Total:" row amount is in the SECOND table inside #content
     * (the one without a thead). Fetched programmatically in getCartTotal().
     */

    /** Checkout button on the full cart page. */
    private final By checkoutButton = By.cssSelector("#content a[href*='checkout/checkout']");

    /** The cart-page content landmark — used to confirm the page has loaded. */
    private final By cartContentLandmark = By.cssSelector("div#content");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    // ------------------------------------------------------------------ navigation

    public CartPage goToViewCart() {
        String baseUrl = ConfigReader.get("url");
        String cartUrl = baseUrl.replaceAll("route=[^&]+", "route=checkout/cart");
        driver.get(cartUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartContentLandmark));
        return this;
    }

    public void openMiniCart() {
        click(cartButton);
    }

    private void openMiniCartDropdown() {
        if (driver.findElements(miniCartDropdown).isEmpty() ||
                !driver.findElements(miniCartDropdown).get(0).isDisplayed()) {
            click(cartButton);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(miniCartDropdown));
    }

    public int getMiniCartBadgeCount() {
        try {
            wait.until(ExpectedConditions.textMatches(cartBadge, java.util.regex.Pattern.compile(".*[1-9].*")));
            String txt = getText(cartBadge);
            return Integer.parseInt(txt.replaceAll("[^0-9]", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public List<String> getMiniCartItemNames() {
        openMiniCartDropdown();
        wait.until(ExpectedConditions.visibilityOfElementLocated(miniCartNames));
        List<String> names = new ArrayList<>();
        for (WebElement el : findAll(miniCartNames))
            names.add(el.getText().trim());
        return names;
    }

    public List<String> getMiniCartItemPrices() {
        openMiniCartDropdown();
        List<String> prices = new ArrayList<>();
        for (WebElement el : findAll(miniCartPrices))
            prices.add(el.getText().trim());
        return prices;
    }

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

    // ------------------------------------------------------------------ cart page queries

    public int getCartItemCount() {
        return findAll(cartProductRows).size();
    }

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

    public List<String> getCartUnitPrices() {
        List<String> prices = new ArrayList<>();
        for (WebElement row : findAll(cartProductRows)) {
            List<WebElement> cells = row.findElements(By.cssSelector("td.text-right"));
            if (cells.size() >= 2) {
                prices.add(cells.get(cells.size() - 2).getText().trim());
            }
        }
        return prices;
    }

    public String getDeliveryDateForProduct(String productNameFragment) {
        for (WebElement row : findAll(cartProductRows)) {
            List<WebElement> links = row.findElements(By.cssSelector("td.text-left a"));
            if (links.isEmpty()) continue;
            if (!links.get(0).getText().toLowerCase().contains(productNameFragment.toLowerCase())) continue;
            for (WebElement small : row.findElements(By.tagName("small"))) {
                String txt = small.getText().trim();
                if (txt.toLowerCase().startsWith("delivery date")) return txt;
            }
        }
        return "";
    }

    /**
     * Checks if there's a stock warning alert on the cart page.
     */
    public boolean hasStockWarning() {
        By warning = By.cssSelector(".alert-danger");
        if (isPresent(warning)) {
            return getText(warning).contains("***");
        }
        return false;
    }

    /**
     * Grand-total string shown on the cart page, e.g. "$363.99".
     * OpenCart renders two tables inside #content: the product table (has thead)
     * and the totals table (no thead). We iterate to find the totals table, then
     * return the last cell of its last row ("Total:").
     */
    /**
     * Sub-Total string shown on the cart page, e.g. "$100.00".
     * Fetched from the first row of the totals table.
     */
    public String getCartSubTotal() {
        List<WebElement> tables = driver.findElements(
                By.cssSelector("#content table.table-bordered"));
        for (WebElement table : tables) {
            if (table.findElements(By.cssSelector("thead")).isEmpty()) {
                List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));
                if (!rows.isEmpty()) {
                    WebElement firstRow = rows.get(0);
                    List<WebElement> cells = firstRow.findElements(By.cssSelector("td"));
                    if (!cells.isEmpty()) {
                        return cells.get(cells.size() - 1).getText().trim();
                    }
                }
            }
        }
        return "";
    }

    public String getCartTotal() {
        List<WebElement> tables = driver.findElements(
                By.cssSelector("#content table.table-bordered"));
        for (WebElement table : tables) {
            if (table.findElements(By.cssSelector("thead")).isEmpty()) {
                List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));
                if (!rows.isEmpty()) {
                    WebElement lastRow = rows.get(rows.size() - 1);
                    List<WebElement> cells = lastRow.findElements(By.cssSelector("td"));
                    if (!cells.isEmpty()) {
                        return cells.get(cells.size() - 1).getText().trim();
                    }
                }
            }
        }
        return "";
    }

    /**
     * Returns true when the cart has no product rows (i.e. the shopping cart is empty).
     */
    public boolean isCartEmpty() {
        return driver.findElements(cartProductRows).isEmpty();
    }

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
        wait.until(ExpectedConditions.textToBe(By.cssSelector("#cart button span"), "0 item(s) - $0.00"));
    }

}
