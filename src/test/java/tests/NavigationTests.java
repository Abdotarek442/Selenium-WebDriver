package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CategoryPage;
import pages.LoginPage;
import utils.ConfigReader;
import utils.ExcelReader;

public class NavigationTests extends BaseTest {

    @DataProvider(name = "navigationData")
    public Object[][] navigationData() {
        return ExcelReader.getSheetData("Navigation");
    }

    // -------- TC06: Breadcrumb & left side menu --------------------------
    @Test(dataProvider = "navigationData",
            description = "TC06 - Breadcrumb and side-menu on a category page")
    @Severity(SeverityLevel.MINOR)
    @Description("Login → open category → verify breadcrumb last item and " +
            "highlighted side-menu item match the expected values.")
    public void breadcrumbAndSidebarOnTablets(String category,
            String expectedBreadcrumb, String expectedSidebar) {

        loginWithDefaultUser();

        CategoryPage categoryPage = home.openCategoryByName(category);

        Assert.assertEquals(categoryPage.getBreadcrumbLast(), expectedBreadcrumb,
                "Breadcrumb's last link should be '" + expectedBreadcrumb + "'.");
        Assert.assertEquals(categoryPage.getHighlightedSidebar(), expectedSidebar,
                "Highlighted item in the sidebar should be '" + expectedSidebar + "'.");

        home.logout();
    }

    private void loginWithDefaultUser() {
        LoginPage login = home.goToLogin();
        login.login(ConfigReader.get("valid.email"), ConfigReader.get("valid.password"));
    }
}
