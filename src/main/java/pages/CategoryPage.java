package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents any product listing / category page (Tablets, Desktops,
 * Laptops, Phones & PDAs, MP3 Players, etc.).
 */
public class CategoryPage extends BasePage {

    private final By breadcrumbActive =
            By.cssSelector("ul.breadcrumb li:last-child");
    private final By sidebarHighlighted =
            By.cssSelector("#column-left a.active");
    private final By sortBySelect = By.id("input-sort");

    private final By productNames =
            By.cssSelector("div.product-thumb h4 a");
    private final By productPrices =
            By.cssSelector("div.product-thumb p.price");
    private final By addToCartButtons =
            By.cssSelector("div.product-thumb button[onclick*='cart.add']");
    private final By successAlert =
            By.cssSelector("div.alert.alert-success");

    public CategoryPage(WebDriver driver) {
        super(driver);
    }

    public String getBreadcrumbLast() {
        return getText(breadcrumbActive);
    }

    public String getHighlightedSidebar() {
        // Text includes product count e.g. "Tablets (1)" — strip the parenthetical
        return getText(sidebarHighlighted).replaceAll("\\s*\\(.*?\\)", "").trim();
    }

    public void sortByNameAsc() {
        selectByVisibleText(sortBySelect, "Name (A - Z)");
    }

    public void sortByNameDesc() {
        selectByVisibleText(sortBySelect, "Name (Z - A)");
    }

    public List<String> getProductNames() {
        return findAll(productNames).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public List<String> getProductPrices() {
        return findAll(productPrices).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * Add the product whose visible name CONTAINS the given fragment.
     * Returns true if a matching product was found and clicked.
     */
    public boolean addProductByName(String fragment) {
        List<WebElement> names = findAll(productNames);
        List<WebElement> buttons = findAll(addToCartButtons);
        for (int i = 0; i < names.size() && i < buttons.size(); i++) {
            if (names.get(i).getText().toLowerCase()
                    .contains(fragment.toLowerCase())) {
                buttons.get(i).click();
                return true;
            }
        }
        return false;
    }

    public String getSuccessAlertText() {
        return getText(successAlert);
    }

    public boolean isSuccessAlertVisible() {
        return isPresent(successAlert);
    }

    /**
     * Click the product link whose visible name CONTAINS the given fragment.
     * Used when we need to open the product details page (for delivery date).
     */
    public ProductPage openProductByName(String fragment) {
        for (WebElement el : findAll(productNames)) {
            if (el.getText().toLowerCase().contains(fragment.toLowerCase())) {
                el.click();
                return new ProductPage(driver);
            }
        }
        throw new RuntimeException("Product not found: " + fragment);
    }
}
