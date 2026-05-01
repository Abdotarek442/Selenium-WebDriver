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

import java.util.List;

public class ChangeCurrencyTest extends BaseTest {

    @DataProvider(name = "currencyData")
    public Object[][] currencyData() {
        return ExcelReader.getSheetData("CurrencyChange");
    }

    // ------------------ TC05: Change currency ----------------------------
    @Test(dataProvider = "currencyData",
            description = "TC05 - Change currency on the given category page")
    @Severity(SeverityLevel.NORMAL)
    @Description("Login → open specified category → verify default currency symbol → " +
            "switch currency → verify all prices show the new symbol.")
    public void changeCurrencyToEuro(String category, String fromSymbol,
            String toCurrencyCode, String toSymbol) {

        loginWithDefaultUser();

        CategoryPage categoryPage = home.openCategoryShowAll(category);

        List<String> beforePrices = categoryPage.getProductPrices();
        Assert.assertFalse(beforePrices.isEmpty(),
                "No products listed on " + category + ".");
        Assert.assertTrue(beforePrices.get(0).contains(fromSymbol),
                "Prices should default to '" + fromSymbol + "'.");

        home.changeCurrency(toCurrencyCode);

        // Re-wrap CategoryPage after page reload triggered by currency switch
        CategoryPage afterSwitch = new CategoryPage(driver);
        List<String> afterPrices = afterSwitch.getProductPrices();
        Assert.assertTrue(afterPrices.stream().allMatch(p -> p.contains(toSymbol)),
                "All prices should show '" + toSymbol + "' after switching currency.");

        home.logout();
    }

    private void loginWithDefaultUser() {
        LoginPage login = home.goToLogin();
        login.login(ConfigReader.get("valid.email"), ConfigReader.get("valid.password"));
    }
}
