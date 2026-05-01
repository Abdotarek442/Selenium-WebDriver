package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.SearchResultsPage;
import utils.ConfigReader;

public class SearchSubcategoriesTests extends BaseTest {
    // -------- TC09: Search in subcategories (Apple Cinema 30) -----------
    @Test(description = "TC09 - Search in subcategories (Apple in Components)")
    @Severity(SeverityLevel.NORMAL)
    @Description("Search 'Apple' inside 'Components' → no results. " +
            "Then enable 'Search in subcategories' → 'Apple Cinema 30' is shown.")
    public void searchInSubcategories() {
        loginWithDefaultUser();

        // First, do an empty search to land on the search page
        SearchResultsPage results = home.searchFor("Apple");

        // Re-enter keyword and choose Components category
        results.enterKeyword("Apple");
        results.selectCategory("Components");
        results.clickSearch();

        Assert.assertTrue(results.isNoResultsMessageShown() ||
                        results.getProductNames().isEmpty(),
                "No products should be found for 'Apple' in 'Components'.");

        // Re-search WITH subcategory checkbox enabled
        results.enterKeyword("Apple");
        results.selectCategory("Components");
        results.enableSubCategories();
        results.clickSearch();

        boolean appleCinemaFound = results.getProductNames().stream()
                .anyMatch(n -> n.toLowerCase().contains("apple cinema 30"));
        Assert.assertTrue(appleCinemaFound,
                "'Apple Cinema 30' should be in the results when " +
                        "'Search in subcategories' is enabled.");
        home.logout();
    }
    private void loginWithDefaultUser() {
        LoginPage login = home.goToLogin();
        login.login(ConfigReader.get("valid.email"),
                ConfigReader.get("valid.password"));
    }
}