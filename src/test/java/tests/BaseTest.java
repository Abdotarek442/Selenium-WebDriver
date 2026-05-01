package tests;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import pages.HomePage;
import utils.DriverFactory;
import utils.TestListener;

@Listeners(TestListener.class)
public class BaseTest {

    protected WebDriver driver;
    protected HomePage home;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.createDriver();
        home = new HomePage(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (Exception e) {
            System.out.println("Driver quit issue ignored");
        }
    }
}
