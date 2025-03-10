
## Prerequisites

- Java 8 or later
- Maven 3.6 or later
- Chrome or Firefox browser
- Allure

## Running the Tests

The tests are configured to run in parallel classes.

### Running all tests:

```bash
mvn clean test
```

### Running only UI tests:

```bash
mvn clean test -Dtest=InsiderUITest
```

### Running only API tests:

```bash
mvn clean test -Dtest=PetApiTest
```

### Running both UI and API tests in parallel:

```bash
mvn clean test -Dgroups="ui | api"
```

## Generating Reports

The project uses Allure for reporting: ( also screenshot after fails, can be seen in allure report)

```bash
# Generate the report
allure serve -p 8080 target/allure-results 
```

## Configuration

You can modify test configuration in `src/test/resources/config.properties`:

- Change browser: Set `browser=firefox` or `browser=chrome`
- Change timeouts: Modify `implicitWait` and `explicitWait` values
- Update URLs: Modify the base URLs for testing different environments

## Logs

Logs are stored in `target/test-automation.log` and also displayed in the console.

## Notes about development

Retry mechanism in API test is used because of API responses being not consistent, for specific requests sometimes it is responding with 200, sometimes it is 404. Since there are no clear requiriments, assertions made with assumptions.

Test data created before every test for isolated testing.

For demonstration purposes some logs can be unnecessary, they can be removed it desired.



