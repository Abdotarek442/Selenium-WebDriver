package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.RegisterPage;
import pages.RegisterSuccessPage;
import utils.ExcelReader;

public class ValidRegistrationTests extends BaseTest {
    // ------------------ TC01: Registration without errors -----------------
    @DataProvider(name = "validRegistrationData")
    public Object[][] validRegistrationData() {
        return ExcelReader.getSheetData("ValidRegistration");
    }

    @Test(dataProvider = "validRegistrationData",
          description = "TC01 - Registration without errors")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Register a new user with all required fields, " +
            "accept agreement and verify success message + Logout option.")
    public void registrationWithoutErrors(String firstName, String lastName,
                                          String emailPrefix, String telephone,
                                          String password) {
        // Make the email unique per run
        String email = emailPrefix + System.currentTimeMillis() + "@test.com";

        RegisterPage register = home.goToRegister();
        register.fillAll(firstName, lastName, email, telephone, password);
        register.acceptAgreement();
        register.clickContinue();

        RegisterSuccessPage success = new RegisterSuccessPage(driver);
        Assert.assertEquals(success.getHeadingText(),
                "Your Account Has Been Created!",
                "Account-created heading was not displayed.");

        Assert.assertTrue(home.isLogoutVisibleInMyAccount(),
                "Logout option should appear under My Account after registration.");
        home.logout();
    }

}
