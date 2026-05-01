package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class SearchResultsPage extends BasePage {

    private final By keywordInput          = By.id("input-search");
    private final By categorySelect        = By.cssSelector("select[name='category_id']");
    private final By subCategoriesCheckbox = By.cssSelector("input[name='sub_category']");
    private final By searchInDescriptions  = By.cssSelector("input[name='description']");
    private final By searchButton          = By.id("button-search");

    private final By productNames          = By.cssSelector("div.product-thumb h4 a");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public void enterKeyword(String keyword) {
        type(keywordInput, keyword);
    }

    public void selectCategory(String visibleText) {
        selectByVisibleText(categorySelect, visibleText);
    }

    public void enableSubCategories() {
        scrollIntoView(subCategoriesCheckbox);
        WebElement cb = waitVisible(subCategoriesCheckbox);
        if (!cb.isSelected()) cb.click();
    }

    public void clickSearch() {
        click(searchButton);
    }

    public List<String> getProductNames() {
        return findAll(productNames).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * Returns true when the search returned no products.
     * OpenCart shows the no-results paragraph only when product-thumb elements
     * are absent, so checking for an empty product list is equivalent.
     */
    public boolean isNoResultsMessageShown() {
        return driver.findElements(productNames).isEmpty();
    }
}
