package org.example.api.client;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.api.utils.QueryParamUtil;
import org.example.utils.ConfigReader;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static io.restassured.RestAssured.given;

// Base class for making API requests
public class BaseApiClient {
    private static final Logger logger = LogManager.getLogger(BaseApiClient.class);
    protected RequestSpecification requestSpec;

    public BaseApiClient() {
        logger.info("Setting up API client for {}", ConfigReader.getProperty("petStoreBaseUrl"));
        // Set the base URL for all requests
        RestAssured.baseURI = ConfigReader.getProperty("petStoreBaseUrl");
        requestSpec = getRequestSpecification();
    }

    protected RequestSpecification getRequestSpecification() {
        return RestAssured.given();
    }

    // Makes a GET request with optional query parameters
    protected Response getRequest(Map<String, Object> queryParams, String endpoint) {
        logger.info("Sending GET request to {}", endpoint);

        RequestSpecification spec = given()
                .contentType(ContentType.JSON);

        if (queryParams != null) {
            queryParams.forEach((key, value) -> 
                spec.queryParam(key, value));
        }

        Response response = spec
                .log().uri()
                .log().headers()
                .log().method()
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();

        logger.info("Received response with status {}", response.getStatusCode());

        response.then().log().status().log().body();
        return response;
    }

    // Makes a GET request with path parameters (like IDs in the URL)
    protected Response getRequestWithPath(Map<String, Object> pathParams, String endpoint) {
        logger.info("Sending GET request to {} with path parameters", endpoint);

        RequestSpecification spec = given()
                .contentType(ContentType.JSON);

        if (pathParams != null) {
            pathParams.forEach((key, value) -> 
                spec.pathParam(key, value));
        }

        Response response = spec
                .log().uri()
                .log().headers()
                .log().method()
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();

        logger.info("Received response with status {}", response.getStatusCode());

        response.then().log().status().log().body();
        return response;
    }

    // Makes a POST request with a JSON body
    protected Response postRequest(Object body, String endpoint) {
        logger.info("Sending POST request to {}", endpoint);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .log().uri()
                .log().headers()
                .log().method()
                .log().body()
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();

        logger.info("Received response with status {}", response.getStatusCode());

        response.then().log().status().log().body();
        return response;
    }

    // Makes a POST request with form data (application/x-www-form-urlencoded)
    protected Response postFormRequest(List<QueryParamUtil> formParams, String endpoint) {
        logger.info("Sending POST form request to {}", endpoint);

        RequestSpecification spec = given()
                .contentType(ContentType.URLENC);

        if (formParams != null) {
            formParams.forEach(param -> 
                spec.formParam(param.getKey(), param.getValue()));
        }

        // Execute the request and capture the response
        Response response = spec
                .log().uri()
                .log().headers()
                .log().method()
                .log().params()
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();

        logger.info("Received response with status {}", response.getStatusCode());

        response.then().log().status().log().body();
        return response;
    }

    // Makes a PUT request with a JSON body
    protected Response putRequest(Object body, String endpoint) {
        logger.info("Sending PUT request to {}", endpoint);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .log().uri()
                .log().headers()
                .log().method()
                .log().body()
                .when()
                .put(endpoint)
                .then()
                .extract()
                .response();

        logger.info("Received response with status {}", response.getStatusCode());

        response.then().log().status().log().body();
        return response;
    }

    // Makes a DELETE request with path parameters
    protected Response deleteRequest(Map<String, Object> pathParams, String endpoint) {
        logger.info("Sending DELETE request to {}", endpoint);

        RequestSpecification spec = given()
                .contentType(ContentType.JSON);

        if (pathParams != null) {
            pathParams.forEach((key, value) -> 
                spec.pathParam(key, value));
        }

        Response response = spec
                .log().uri()
                .log().headers()
                .log().method()
                .when()
                .delete(endpoint)
                .then()
                .extract()
                .response();

        logger.info("Received response with status {}", response.getStatusCode());

        response.then().log().status().log().body();
        return response;
    }
    
    // Retries an operation with exponential backoff until success or max attempts reached
    protected <T> T retryOperation(Supplier<Response> operation, Class<T> responseType, int maxAttempts, long delayBetweenAttempts) {
        logger.info("Starting operation with {} retry attempts", maxAttempts);
        
        Exception lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                logger.info("Making attempt {} of {}", attempt, maxAttempts);
                Response response = operation.get();

                if (response.getStatusCode() == 200) {
                    return response.as(responseType);
                }
                
                logger.warn("Attempt {} failed with status code {}", attempt, response.getStatusCode());
            } catch (Exception error) {
                lastError = error;
                logger.error("Error during attempt {}: {}", attempt, error.getMessage());
            }

            try {
                Thread.sleep(delayBetweenAttempts);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (lastError != null) {
            throw new RuntimeException("Operation failed after " + maxAttempts + " attempts", lastError);
        }
        
        throw new RuntimeException("Operation failed after " + maxAttempts + " attempts");
    }
    
    private RequestSpecification given() {
        return RestAssured.given().spec(requestSpec);
    }
} 
