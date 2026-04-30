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

public class RegistrationTests extends BaseTest {

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

    // ------------------ TC02: Registration with errors --------------------
    @DataProvider(name = "invalidRegistrationData")
    public Object[][] invalidRegistrationData() {
        return ExcelReader.getSheetData("InvalidRegistration");
    }

    @Test(dataProvider = "invalidRegistrationData",
          description = "TC02 - Registration with errors")
    @Severity(SeverityLevel.NORMAL)
    @Description("Submit registration with missing required fields, " +
            "then with a too-short password, and verify error messages.")
    public void registrationWithErrors(String firstName, String lastName,
                                       String shortPassword) {
        RegisterPage register = home.goToRegister();

        // Step 1: only FN & LN, click continue → required-field errors
        register.enterFirstName(firstName);
        register.enterLastName(lastName);
        register.clickContinue();

        Assert.assertTrue(register.isEmailErrorShown(),
                "Email field should show a 'required' error.");
        Assert.assertTrue(register.isTelephoneErrorShown(),
                "Telephone field should show a 'required' error.");
        Assert.assertTrue(register.isPasswordErrorShown(),
                "Password field should show a 'required' error.");

        // Step 2: fill valid data + a short password
        String email = "shortpw_" + System.currentTimeMillis() + "@test.com";
        register.enterEmail(email);
        register.enterTelephone("01000000000");
        register.enterPassword(shortPassword);
        register.enterConfirmPassword(shortPassword);
        register.acceptAgreement();
        register.clickContinue();

        Assert.assertEquals(register.getPasswordErrorText(),
                "Password must be between 4 and 20 characters!",
                "Expected password length validation message.");
    }
}
