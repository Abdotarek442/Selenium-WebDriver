package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class SearchResultsPage extends BasePage {

    private final By keywordInput        = By.id("input-search");
    private final By categorySelect      = By.id("input-category");
    private final By subCategoriesCheckbox =
            By.cssSelector("input[name='sub_category']");
    private final By searchInDescriptions =
            By.cssSelector("input[name='description']");
    private final By searchButton        = By.id("button-search");

    private final By productNames        =
            By.cssSelector("div.product-thumb h4 a");
    private final By noProductsMessage   =
            By.xpath("//*[@id='content']//p[contains(.,'no product')]");

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

    public boolean isNoResultsMessageShown() {
        return isPresent(noProductsMessage);
    }
}
