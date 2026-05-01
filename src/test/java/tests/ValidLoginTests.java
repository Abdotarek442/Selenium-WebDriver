package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.AccountPage;
import pages.LoginPage;
import utils.ExcelReader;

public class ValidLoginTests extends BaseTest {
    // -------------------- TC03: Valid login ------------------------------
    @DataProvider(name = "validLogin")
    public Object[][] validLogin() {
        return ExcelReader.getSheetData("ValidLogin");
    }

    @Test(dataProvider = "validLogin",
            description = "TC03 - Valid login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Log in with valid credentials and verify the My Account page is displayed.")
    public void validLoginShouldOpenAccountPage(String email, String password) {
        LoginPage login = home.goToLogin();
        login.login(email, password);

        AccountPage account = new AccountPage(driver);
        Assert.assertTrue(account.isOpen(),
                "My Account page should open after a valid login.");
        home.logout();
    }
}
