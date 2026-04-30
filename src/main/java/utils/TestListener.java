package utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;

/**
 * Listener that attaches a screenshot to the Allure report whenever a test
 * fails. The driver instance is found by reflection on the test class
 * (a `driver` field is expected in BaseTest).
 */
public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = getDriverFromTest(result);
        if (driver != null) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver)
                        .getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Failure screenshot",
                        "image/png", new ByteArrayInputStream(screenshot), "png");
                Allure.addAttachment("Failure URL", driver.getCurrentUrl());
            } catch (Exception e) {
                System.err.println("Could not attach screenshot: " + e.getMessage());
            }
        }
    }

    private WebDriver getDriverFromTest(ITestResult result) {
        try {
            Object testInstance = result.getInstance();
            Class<?> clazz = testInstance.getClass();
            while (clazz != null) {
                try {
                    Field field = clazz.getDeclaredField("driver");
                    field.setAccessible(true);
                    return (WebDriver) field.get(testInstance);
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
        } catch (Exception e) {
            System.err.println("Could not access driver: " + e.getMessage());
        }
        return null;
    }
}
