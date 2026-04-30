# Assignment 2 – Part A: Selenium Automation Framework
**Application Under Test:** http://tutorialsninja.com/demo/index.php?route=common/home

A complete Java + Selenium WebDriver + TestNG framework that follows
the **Page Object Model (POM)** design pattern, reads test data from
an **Excel** sheet, externalises configuration through a **properties
file**, and produces an **Allure** report with screenshots on failure.

---

## 1. Tech Stack
| Layer            | Tool                       |
|------------------|----------------------------|
| Language         | Java 17                    |
| Build tool       | Maven                      |
| Browser driver   | Selenium WebDriver 4.21    |
| Test runner      | TestNG 7.10                |
| Excel reader     | Apache POI 5.2.5           |
| Reporting        | Allure 2.27                |
| Driver mgmt.     | Selenium Manager (built in)|

---

## 2. Project Structure
```
A2_PartA/
├── pom.xml
├── testng.xml
├── testdata/
│   └── TestData.xlsx          ← all test data (one sheet per test type)
├── src/
│   ├── main/java/
│   │   ├── pages/             ← Page Object classes
│   │   │   ├── BasePage.java
│   │   │   ├── HomePage.java
│   │   │   ├── RegisterPage.java
│   │   │   ├── RegisterSuccessPage.java
│   │   │   ├── LoginPage.java
│   │   │   ├── AccountPage.java
│   │   │   ├── CategoryPage.java
│   │   │   ├── ProductPage.java
│   │   │   ├── SearchResultsPage.java
│   │   │   ├── CartPage.java
│   │   │   ├── CheckoutPage.java
│   │   │   └── OrderSuccessPage.java
│   │   └── utils/
│   │       ├── ConfigReader.java
│   │       ├── DriverFactory.java
│   │       ├── ExcelReader.java
│   │       └── TestListener.java
│   └── test/
│       ├── java/tests/        ← TestNG test classes
│       │   ├── BaseTest.java
│       │   ├── RegistrationTests.java
│       │   ├── LoginTests.java
│       │   ├── CurrencyAndNavigationTests.java
│       │   ├── SortAndSearchTests.java
│       │   ├── CartTests.java
│       │   └── CheckoutTests.java
│       └── resources/
│           └── config.properties
```

### Test cases coverage (one row in the Excel sheet → one test case)
| ID  | Excel sheet            | Test class                    | Test method                       |
|-----|------------------------|-------------------------------|------------------------------------|
| TC01| ValidRegistration      | RegistrationTests             | registrationWithoutErrors          |
| TC02| InvalidRegistration    | RegistrationTests             | registrationWithErrors             |
| TC03| ValidLogin             | LoginTests                    | validLoginShouldOpenAccountPage    |
| TC04| InvalidLogin           | LoginTests                    | invalidLoginShouldShowWarning      |
| TC05| –                      | CurrencyAndNavigationTests    | changeCurrencyToEuro               |
| TC06| –                      | CurrencyAndNavigationTests    | breadcrumbAndSidebarOnTablets      |
| TC07| –                      | SortAndSearchTests            | sortByNameAscAndDesc               |
| TC08| –                      | SortAndSearchTests            | searchByName                       |
| TC09| –                      | SortAndSearchTests            | searchInSubcategories              |
| TC10| –                      | CartTests                     | addItemsToCartAndVerify            |
| TC11| Checkout               | CheckoutTests                 | normalCheckout                     |

---

## 3. Prerequisites

1. **Java 17** (or higher) — `java -version`
2. **Maven 3.9+** — `mvn -v`
3. **Google Chrome** installed (any recent version – Selenium Manager
   downloads the matching driver automatically; no manual ChromeDriver
   setup is required).
4. **Allure CLI** (only needed to view the report):
   * Windows (Scoop):  `scoop install allure`
   * macOS (Homebrew): `brew install allure`
   * Manual:           https://github.com/allure-framework/allure2/releases

---

## 4. One-time Setup

### 4.1 Create a real user on the demo site
The valid-login and most subsequent tests need a working account.
Open the site in your browser, register an account manually, then
update both files below with the same email/password:

* `src/test/resources/config.properties`
  ```
  valid.email=YOUR_REAL_EMAIL@test.com
  valid.password=YOUR_REAL_PASSWORD
  ```
* `testdata/TestData.xlsx` → sheet **ValidLogin** → first data row

### 4.2 (Optional) change browser
Edit `config.properties`:
```
browser=chrome      # chrome | firefox | edge
```

---

## 5. How to Run

### 5.1 Run the full suite
From the project root:
```bash
mvn clean test
```
This will:
1. Compile the project
2. Run **all** tests defined in `testng.xml`
3. Write Allure raw results to `allure-results/`

### 5.2 Run a single test class
```bash
mvn -Dtest=LoginTests test
```

### 5.3 Open the Allure report
After the run finishes:
```bash
allure serve allure-results
```
This launches an interactive HTML report in your default browser.
Failed tests will include a **screenshot attachment** taken at the
moment of failure (handled by `utils.TestListener`).

To generate a static report instead:
```bash
allure generate allure-results --clean -o allure-report
allure open allure-report
```

---

## 6. How the Requirements Are Met

| Requirement (PDF)                                      | Where it lives                                  |
|--------------------------------------------------------|------------------------------------------------|
| Java + Selenium WebDriver + TestNG                     | `pom.xml`                                      |
| Page Object Model                                      | `src/main/java/pages/`                         |
| All scenarios from Excel automated                     | `tests/*Tests.java` + `testdata/TestData.xlsx` |
| Same test executed with multiple data sets (Excel)     | `ExcelReader` + `@DataProvider`                |
| No hard-coded test data                                | All data lives in `TestData.xlsx`              |
| Externalised config (URL, browser, …)                  | `config.properties` + `ConfigReader`           |
| Strong locators (id / name / clean CSS)                | All page classes (no absolute XPath)           |
| Allure report with screenshots on failure              | `TestListener` + `allure-testng` dependency    |
| Modular, reusable code                                 | `BasePage`, `BaseTest`, `DriverFactory`        |

---

## 7. Troubleshooting

| Symptom                                          | Fix                                                                       |
|--------------------------------------------------|---------------------------------------------------------------------------|
| `SessionNotCreatedException`                     | Update Chrome to the latest stable version. Selenium Manager will refresh the matching driver automatically. |
| `valid login` test fails                         | The email/password in `config.properties` and `ValidLogin` sheet must point to a real, working account on the demo site. |
| `Apple Cinema 30` not found in TC09              | The demo store's catalog occasionally changes. Adjust the search keyword in the test if the product is renamed. |
| `iPod Shuffle` out of stock for TC10/TC11        | Pick another available product and update the string in the test.        |
| `allure: command not found`                      | Install the Allure CLI (see Prerequisites).                              |

---

## 8. Cleaning the workspace
```bash
mvn clean
rm -rf allure-results allure-report
```
