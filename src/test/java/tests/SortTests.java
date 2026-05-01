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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortTests extends BaseTest {

    @DataProvider(name = "sortData")
    public Object[][] sortData() {
        return ExcelReader.getSheetData("SortByName");
    }

    // ------------------ TC07: Sort by name ------------------------------
    @Test(dataProvider = "sortData",
            description = "TC07 - Sort by name (A-Z and Z-A) on a category page")
    @Severity(SeverityLevel.NORMAL)
    @Description("Login → open specified category → sort A-Z and verify ascending; " +
            "sort Z-A and verify descending.")
    public void sortByNameAscAndDesc(String category) {

        loginWithDefaultUser();

        CategoryPage categoryPage = home.openCategoryByName(category);

        categoryPage.sortByNameAsc();
        List<String> asc = categoryPage.getProductNames();
        List<String> ascSorted = new ArrayList<>(asc);
        Collections.sort(ascSorted, String.CASE_INSENSITIVE_ORDER);
        Assert.assertEquals(asc, ascSorted,
                "Products should be sorted alphabetically ascending.");

        categoryPage.sortByNameDesc();
        List<String> desc = categoryPage.getProductNames();
        List<String> descSorted = new ArrayList<>(desc);
        descSorted.sort(String.CASE_INSENSITIVE_ORDER.reversed());
        Assert.assertEquals(desc, descSorted,
                "Products should be sorted alphabetically descending.");

        home.logout();
    }

    private void loginWithDefaultUser() {
        LoginPage login = home.goToLogin();
        login.login(ConfigReader.get("valid.email"), ConfigReader.get("valid.password"));
    }
}
