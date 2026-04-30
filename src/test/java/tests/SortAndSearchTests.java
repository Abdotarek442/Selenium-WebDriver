package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CategoryPage;
import pages.LoginPage;
import pages.SearchResultsPage;
import utils.ConfigReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortAndSearchTests extends BaseTest {

    // ------------------ TC07: Sort by name ------------------------------
    @Test(description = "TC07 - Sort by name (A-Z and Z-A) on Phones & PDAs")
    @Severity(SeverityLevel.NORMAL)
    @Description("Login → Phones & PDAs → sort A-Z and verify ascending; " +
            "sort Z-A and verify descending.")
    public void sortByNameAscAndDesc() {
        loginWithDefaultUser();

        CategoryPage phones = home.openPhones();

        phones.sortByNameAsc();
        List<String> asc = phones.getProductNames();
        List<String> ascSorted = new ArrayList<>(asc);
        Collections.sort(ascSorted);
        Assert.assertEquals(asc, ascSorted,
                "Products should be sorted alphabetically ascending.");

        phones.sortByNameDesc();
        List<String> desc = phones.getProductNames();
        List<String> descSorted = new ArrayList<>(desc);
        descSorted.sort(Collections.reverseOrder());
        Assert.assertEquals(desc, descSorted,
                "Products should be sorted alphabetically descending.");

        home.logout();
    }

    // ------------------ TC08: Search by name ----------------------------
    @Test(description = "TC08 - Search by name 'Mac'")
    @Severity(SeverityLevel.NORMAL)
    @Description("Search the keyword 'Mac' and verify every result name contains 'Mac'.")
    public void searchByName() {
        loginWithDefaultUser();

        SearchResultsPage results = home.searchFor("Mac");
        List<String> names = results.getProductNames();
        Assert.assertFalse(names.isEmpty(),
                "Search for 'Mac' should return at least one result.");
        for (String n : names) {
            Assert.assertTrue(n.toLowerCase().contains("mac"),
                    "Every result should contain 'Mac' but got: " + n);
        }
        home.logout();
    }

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
