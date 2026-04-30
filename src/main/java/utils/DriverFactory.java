package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;

/**
 * Creates WebDriver instances based on the browser specified in the config
 * file. Uses Selenium Manager (built into Selenium 4) so no manual
 * chromedriver download is needed.
 */
public class DriverFactory {

    private DriverFactory() {
        // utility class
    }

    public static WebDriver createDriver() {
        String browser = ConfigReader.get("browser").toLowerCase();
        WebDriver driver;

        switch (browser) {
            case "firefox":
                driver = new FirefoxDriver();
                break;
            case "edge":
                driver = new EdgeDriver();
                break;
            case "chrome":
            default:
                ChromeOptions opts = new ChromeOptions();
                opts.addArguments("--start-maximized");
                opts.addArguments("--remote-allow-origins=*");
                opts.addArguments("--disable-notifications");
                driver = new ChromeDriver(opts);
                break;
        }

        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigReader.getInt("implicit.wait")));
        driver.manage().window().maximize();
        driver.get(ConfigReader.get("url"));
        return driver;
    }
}
