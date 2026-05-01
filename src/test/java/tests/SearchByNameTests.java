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

import java.util.List;

public class SearchByNameTests extends BaseTest {

    @DataProvider(name = "searchData")
    public Object[][] searchData() {
        return ExcelReader.getSheetData("SearchByName");
    }

    // ------------------ TC08: Search by name ----------------------------
    @Test(dataProvider = "searchData",
            description = "TC08 - Search by keyword and verify all results contain it")
    @Severity(SeverityLevel.NORMAL)
    @Description("Search the given keyword and verify every result name contains that keyword.")
    public void searchByName(String keyword) {

        loginWithDefaultUser();

        SearchResultsPage results = home.searchFor(keyword);
        List<String> names = results.getProductNames();

        Assert.assertFalse(names.isEmpty(),
                "Search for '" + keyword + "' should return at least one result.");
        for (String name : names) {
            Assert.assertTrue(name.toLowerCase().contains(keyword.toLowerCase()),
                    "Every result should contain '" + keyword + "' but got: " + name);
        }

        home.logout();
    }

    private void loginWithDefaultUser() {
        LoginPage login = home.goToLogin();
        login.login(ConfigReader.get("valid.email"), ConfigReader.get("valid.password"));
    }
}
