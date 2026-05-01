package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoginPage;
import utils.ExcelReader;

public class InvalidLoginTests extends BaseTest {
    // -------------------- TC04: Invalid login ----------------------------
    @DataProvider(name = "invalidLogin")
    public Object[][] invalidLogin() {
        return ExcelReader.getSheetData("InvalidLogin");
    }

    @Test(dataProvider = "invalidLogin",
          description = "TC04 - Invalid login")
    @Severity(SeverityLevel.NORMAL)
    @Description("Log in with invalid credentials and verify the warning message.")
    public void invalidLoginShouldShowWarning(String email, String password) {
        LoginPage login = home.goToLogin();
        login.login(email, password);

        Assert.assertTrue(login.isWarningDisplayed(),
                "A warning alert should be displayed for invalid credentials.");
        Assert.assertTrue(login.getWarningText()
                        .contains("No match for E-Mail Address and/or Password."),
                "Expected the standard 'No match …' warning text.");
    }
}
