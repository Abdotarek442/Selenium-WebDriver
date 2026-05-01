package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CategoryPage;
import pages.LoginPage;
import utils.ConfigReader;

public class NavigationTests extends BaseTest {

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
