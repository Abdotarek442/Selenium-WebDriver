package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.SearchResultsPage;
import utils.ConfigReader;


import java.util.List;

public class SearchByNameTests extends BaseTest {

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
    private void loginWithDefaultUser() {
        LoginPage login = home.goToLogin();
        login.login(ConfigReader.get("valid.email"),
                ConfigReader.get("valid.password"));
    }
}
