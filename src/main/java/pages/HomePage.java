package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Home page — handles the top-right "My Account" dropdown and the top
 * navigation menu (Desktops, Laptops, Tablets, …).
 */
public class HomePage extends BasePage {

    // Top header
    private final By myAccountToggle =
            By.cssSelector("a[title='My Account']");
    private final By registerLink =
            By.cssSelector("ul.dropdown-menu-right a[href*='route=account/register']");
    private final By loginLink =
            By.cssSelector("ul.dropdown-menu-right a[href*='route=account/login']");
    private final By logoutLinkInDropdown =
            By.cssSelector("ul.dropdown-menu-right a[href*='route=account/logout']");

    // Currency switcher
    private final By currencyToggle =
            By.cssSelector("#form-currency .dropdown-toggle");
    private final By euroOption =
            By.name("EUR");
    private final By dollarOption =
            By.name("USD");

    // Search
    private final By searchBox =
            By.name("search");
    private final By searchButton =
            By.cssSelector("#search button");

    // Top navigation
    private final By desktopsMenu = By.linkText("Desktops");
    private final By laptopsMenu = By.linkText("Laptops & Notebooks");
    private final By tabletsMenu = By.linkText("Tablets");
    private final By mp3Menu     = By.linkText("MP3 Players");
    private final By phonesMenu  = By.linkText("Phones & PDAs");

    private final By showAllDesktops = By.linkText("Show All Desktops");
    private final By showAllLaptops  = By.linkText("Show All Laptops & Notebooks");
    private final By showAllMp3      = By.linkText("Show All MP3 Players");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public RegisterPage goToRegister() {
        click(myAccountToggle);
        click(registerLink);
        return new RegisterPage(driver);
    }

    public LoginPage goToLogin() {
        click(myAccountToggle);
        click(loginLink);
        return new LoginPage(driver);
    }

    public void logout() {
        click(myAccountToggle);
        click(logoutLinkInDropdown);
    }

    public boolean isLogoutVisibleInMyAccount() {
        click(myAccountToggle);
        boolean visible = isPresent(logoutLinkInDropdown);
        // close dropdown by clicking again
        click(myAccountToggle);
        return visible;
    }

    public void changeCurrencyToEuro() {
        click(currencyToggle);
        // Wait for the currency dropdown to be visible before selecting
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(euroOption));
        click(euroOption);
    }

    public void changeCurrencyToDollar() {
        click(currencyToggle);
        // Wait for the currency dropdown to be visible before selecting
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(dollarOption));
        click(dollarOption);
    }

    public CategoryPage openDesktopsShowAll() {
        navigateViaSubmenuLink("Desktops");
        return new CategoryPage(driver);
    }

    public CategoryPage openLaptopsShowAll() {
        navigateViaSubmenuLink("Laptops");
        return new CategoryPage(driver);
    }

    public CategoryPage openMp3ShowAll() {
        navigateViaSubmenuLink("MP3");
        return new CategoryPage(driver);
    }

    public CategoryPage openTablets() {
        click(tabletsMenu);
        return new CategoryPage(driver);
    }

    public CategoryPage openPhones() {
        click(phonesMenu);
        return new CategoryPage(driver);
    }

    public SearchResultsPage searchFor(String keyword) {
        type(searchBox, keyword);
        click(searchButton);
        return new SearchResultsPage(driver);
    }

    private void navigateViaSubmenuLink(String keyword) {
        // .see-all links are always in the DOM; CSS just hides them until hover.
        // Normalise all whitespace before comparing so "Show AllDesktops" still
        // matches the keyword "Desktops".
        String href = (String) ((JavascriptExecutor) driver).executeScript(
                "var links = document.querySelectorAll('#menu a.see-all');" +
                "for (var i = 0; i < links.length; i++) {" +
                "  var txt = links[i].textContent.replace(/\\s+/g, ' ').trim();" +
                "  if (txt.indexOf(arguments[0]) !== -1) {" +
                "    return links[i].href;" +
                "  }" +
                "}" +
                "return null;",
                keyword);

        if (href == null || href.isEmpty()) {
            throw new RuntimeException(
                    "Submenu 'see-all' link not found in DOM for keyword: '" + keyword + "'");
        }
        driver.get(href);
        // Wait for category page to finish loading
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div.product-thumb, #content h1")));
    }
}
