package org.example.api.client;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.api.enums.PetEndpoints;
import org.example.api.models.request.*;
import org.example.api.models.response.*;
import org.example.api.utils.QueryParamUtil;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

// Controller for Pet API endpoints
public class PetClient extends BaseApiClient {

    private static final Logger logger = LogManager.getLogger(PetClient.class);

    // Creates a new pet
    public PetResponse createPet(PetRequest petRequest) {
        logger.info("Creating pet: {}", petRequest.getName());
        Response response = postRequest(petRequest, PetEndpoints.CREATE_PET.getPath());
        return response.as(PetResponse.class);
    }

    // Updates an existing pet's information
    public PetResponse updatePet(PetRequest petRequest) {
        logger.info("Updating pet ID: {}", petRequest.getId());
        Response response = putRequest(petRequest, PetEndpoints.UPDATE_PET.getPath());
        return response.as(PetResponse.class);
    }

    // Retrieves a list of pets based on their status (available, pending, sold)
    public PetResponse[] findPetsByStatus(String status) {
        logger.info("Finding pets by status: {}", status);
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("status", status);

        Response response = getRequest(queryParams, PetEndpoints.FIND_BY_STATUS.getPath());
        return response.as(PetResponse[].class);
    }

    // Looks up a specific pet using its ID
    public PetResponse getPetById(Long petId) {
        logger.info("Getting pet ID: {}", petId);
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("petId", petId);

        Response response = getRequestWithPath(pathParams, PetEndpoints.GET_PET_BY_ID.getPath());
        return response.as(PetResponse.class);
    }

    // Updates a pet's information using form data instead of JSON
    public ApiResponse updatePetWithForm(Long petId, UpdatePetFormRequest updateRequest) {
        logger.info("Updating pet ID: {} with form data", petId);

        String resolvedPath = PetEndpoints.UPDATE_PET_WITH_FORM.getPath()
                .replace("{petId}", String.valueOf(petId));

        Response response = postFormRequest(
                Arrays.asList(
                        new QueryParamUtil("name", updateRequest.getName()),
                        new QueryParamUtil("status", updateRequest.getStatus())
                ),
                resolvedPath
        );
        
        return response.as(ApiResponse.class);
    }

    // Updates a pet's information with retry capability if the first attempt fails
    public ApiResponse updatePetWithFormWithRetry(Long petId, UpdatePetFormRequest updateRequest, int maxRetries, long delayMs) {
        logger.info("Updating pet ID: {} with retry (max: {})", petId, maxRetries);

        String resolvedPath = PetEndpoints.UPDATE_PET_WITH_FORM.getPath()
                .replace("{petId}", String.valueOf(petId));

        final List<QueryParamUtil> formParams = Arrays.asList(
                new QueryParamUtil("name", updateRequest.getName()),
                new QueryParamUtil("status", updateRequest.getStatus())
        );
        
        return retryOperation(
                () -> postFormRequest(formParams, resolvedPath),
                ApiResponse.class,
                maxRetries,
                delayMs
        );
    }

    public ApiResponse uploadImage(Long petId) {

        File imageFile = new File("src/test/java/org/example/api/images/dogImage.png");

        Response response = given()
                .spec(requestSpec)
                .contentType(ContentType.MULTIPART)
                .pathParam("petId", petId)
                .multiPart("file", imageFile)
                .log().uri()
                .log().headers()
                .log().method()
                .log().body()
                .when()
                .post(PetEndpoints.UPLOAD_IMAGE.getPath())
                .then()
                .log().status()
                .log().body()
                .extract()
                .response();

        return response.as(ApiResponse.class);
    }

    // Removes a pet from the store
    public ApiResponse deletePet(Long petId) {
        logger.info("Deleting pet ID: {}", petId);
        
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("petId", petId);

        Response response = deleteRequest(pathParams, PetEndpoints.DELETE_PET.getPath());
        return response.as(ApiResponse.class);
    }

    // Attempts to delete a pet with retry capability if the first attempt fails
    public ApiResponse deletePetWithRetry(Long petId, int maxRetries, long delayMs) {
        logger.info("Deleting pet ID: {} with retry (max: {})", petId, maxRetries);
        
        final Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("petId", petId);
        
        return retryOperation(
                () -> deleteRequest(pathParams, PetEndpoints.DELETE_PET.getPath()),
                ApiResponse.class,
                maxRetries,
                delayMs
        );
    }

    // Repeatedly tries to delete a pet until it's confirmed gone or max attempts reached
    public Response deletePetUntilNotFound(Long petId, int maxRetries, long delayMs) throws InterruptedException {
        logger.info("Deleting pet ID: {} until not found (max attempts: {})", petId, maxRetries);
        
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("petId", petId);
        
        Response response = null;
        int attempts = 0;
        
        // Keep trying to delete until we get a non-200 response (pet not found)
        while (attempts < maxRetries) {
            response = deleteRequest(pathParams, PetEndpoints.DELETE_PET.getPath());
            
            if (response.getStatusCode() == 200) {
                attempts++;
                Thread.sleep(delayMs);
            } else {
                break;
            }
        }
        
        return response;
    }

    // Retrieves a pet by ID with retry capability if the first attempt fails
    public PetResponse getPetByIdWithRetry(Long petId, int maxRetries, long delayMs) {
        logger.info("Getting pet ID: {} with retry (max: {})", petId, maxRetries);
        
        final Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("petId", petId);
        
        return retryOperation(
                () -> getRequestWithPath(pathParams, PetEndpoints.GET_PET_BY_ID.getPath()),
                PetResponse.class,
                maxRetries,
                delayMs
        );
    }
} 
