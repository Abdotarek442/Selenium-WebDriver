package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CategoryPage;
import pages.LoginPage;
import utils.ConfigReader;

import java.util.List;

public class CurrencyAndNavigationTests extends BaseTest {

    // ------------------ TC05: Change currency ----------------------------
    @Test(description = "TC05 - Change currency from $ to € on Desktops")
    @Severity(SeverityLevel.NORMAL)
    @Description("Login → open all Desktops → switch currency to € → " +
            "all displayed prices should start with the € symbol.")
    public void changeCurrencyToEuro() {
        loginWithDefaultUser();

        CategoryPage desktops = home.openDesktopsShowAll();

        // Verify default ($) prices first
        List<String> dollarPrices = desktops.getProductPrices();
        Assert.assertFalse(dollarPrices.isEmpty(), "No products listed on Desktops.");
        Assert.assertTrue(dollarPrices.get(0).contains("$"),
                "Prices should default to $.");

        // Switch to Euro
        home.changeCurrencyToEuro();
        List<String> euroPrices = desktops.getProductPrices();
        Assert.assertTrue(euroPrices.stream().allMatch(p -> p.contains("€")),
                "All prices should be in € after switching currency.");

        home.logout();
    }

    // -------- TC06: Breadcrumb & left side menu on Tablets --------------
    @Test(description = "TC06 - Breadcrumb and side-menu on Tablets")
    @Severity(SeverityLevel.MINOR)
    @Description("Login → open Tablets → verify breadcrumb 'Tablets' and " +
            "highlighted side-menu 'Tablets'.")
    public void breadcrumbAndSidebarOnTablets() {
        loginWithDefaultUser();
        CategoryPage tablets = home.openTablets();

        Assert.assertEquals(tablets.getBreadcrumbLast(), "Tablets",
                "Breadcrumb's last link should be 'Tablets'.");
        Assert.assertEquals(tablets.getHighlightedSidebar(), "Tablets",
                "Highlighted item in the right sidebar should be 'Tablets'.");
        home.logout();
    }

    private void loginWithDefaultUser() {
        LoginPage login = home.goToLogin();
        login.login(ConfigReader.get("valid.email"),
                    ConfigReader.get("valid.password"));
    }
}
