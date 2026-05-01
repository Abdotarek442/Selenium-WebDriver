package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.SearchResultsPage;
import utils.ConfigReader;
import utils.ExcelReader;

public class SearchSubcategoriesTests extends BaseTest {

    @DataProvider(name = "subcategoryData")
    public Object[][] subcategoryData() {
        return ExcelReader.getSheetData("SearchSubcategories");
    }

    // -------- TC09: Search in subcategories ------------------------------
    @Test(dataProvider = "subcategoryData",
            description = "TC09 - Search in subcategories")
    @Severity(SeverityLevel.NORMAL)
    @Description("Search keyword inside category → no results. " +
            "Then enable 'Search in subcategories' → expected product appears.")
    public void searchInSubcategories(String keyword, String category, String expectedProduct) {

        loginWithDefaultUser();

        // Land on the search results page with an initial search
        SearchResultsPage results = home.searchFor(keyword);

        // Re-run with category selected — no results expected at top level
        results.enterKeyword(keyword);
        results.selectCategory(category);
        results.clickSearch();

        Assert.assertTrue(results.isNoResultsMessageShown() || results.getProductNames().isEmpty(),
                "No products should be found for '" + keyword + "' in '" + category + "'.");

        // Re-run with subcategory search enabled — product should appear
        results.enterKeyword(keyword);
        results.selectCategory(category);
        results.enableSubCategories();
        results.clickSearch();

        boolean found = results.getProductNames().stream()
                .anyMatch(n -> n.toLowerCase().contains(expectedProduct.toLowerCase()));
        Assert.assertTrue(found,
                "'" + expectedProduct + "' should appear when 'Search in subcategories' is enabled.");

        home.logout();
    }

    private void loginWithDefaultUser() {
        LoginPage login = home.goToLogin();
        login.login(ConfigReader.get("valid.email"), ConfigReader.get("valid.password"));
    }
}
