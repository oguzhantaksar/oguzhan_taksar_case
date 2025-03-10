package org.example.api.tests;

import io.restassured.response.Response;
import org.example.api.client.PetClient;
import org.example.api.data.PetTestDataBuilder;
import org.example.api.enums.Constants;
import org.example.api.models.request.*;
import org.example.api.models.response.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("api")
public class PetApiTest {
    private PetClient petClient;
    private PetRequest testPetRequest;
    private Long testPetId;

    // Helper method for setting up a test pet
    private void setupTestPet() {
        petClient = new PetClient();
        testPetRequest = PetTestDataBuilder.buildCreatePetRequest();
        PetResponse response = petClient.createPet(testPetRequest);
        testPetId = response.getId();
        
        System.out.println("Created test pet with ID: " + testPetId);
        
        // Verify with retry
        PetResponse verifyResponse = petClient.getPetByIdWithRetry(testPetId, 10, 1000);
        assertNotNull(verifyResponse, "Pet should exist after creation");
        assertEquals(verifyResponse.getId(), testPetId, "Pet ID should match");
    }

    // Helper method for cleaning up a test pet
    private void cleanupTestPet() {
        if (testPetId != null) {
            try {
                // Delete the test pet and verify it's gone
                ApiResponse deleteResponse = petClient.deletePetWithRetry(testPetId, 10, 1000);
                assertThat("Delete response code should be 200", deleteResponse.getCode(), is(200));
                
                // Verify pet is deleted by checking it returns 404
                Response verifyResponse = petClient.deletePetUntilNotFound(testPetId, 10, 500);
                assertThat("Pet should not exist after deletion", verifyResponse.getStatusCode(), is(404));
                
                System.out.println("Successfully cleaned up test pet with ID: " + testPetId);
            } catch (Exception e) {
                System.err.println("Failed to clean up test pet with ID: " + testPetId);
                e.printStackTrace();
            }
        }
    }

    @BeforeEach
    void initPetClient() {
        petClient = new PetClient();
    }

    @Test
    public void testCreatePet() {
        PetRequest newPetRequest = PetTestDataBuilder.buildCreatePetRequest();
        PetResponse response = petClient.createPet(newPetRequest);
        
        Long createdPetId = response.getId();
        
        assertAll("Pet Creation Assertions",
            () -> assertThat("Pet ID should exist", response.getId(), is(notNullValue())),
            () -> assertThat("Pet name should match", response.getName(), is(newPetRequest.getName())),
            () -> assertThat("Pet status should match", response.getStatus(), is(newPetRequest.getStatus())),
            () -> assertThat("Pet category ID should match", response.getCategory().getId(), is(newPetRequest.getCategory().getId())),
            () -> assertThat("Pet category name should match", response.getCategory().getName(), is(newPetRequest.getCategory().getName())),
            () -> assertThat("Pet photo URLs should match", response.getPhotoUrls(), is(newPetRequest.getPhotoUrls())),
            () -> assertThat("First tag ID should match", response.getTags().get(0).getId(), is(newPetRequest.getTags().get(0).getId())),
            () -> assertThat("First tag name should match", response.getTags().get(0).getName(), is(newPetRequest.getTags().get(0).getName()))
        );

        // Cleanup the created pet
        testPetId = createdPetId;
        cleanupTestPet();
    }

    @Test
    public void testImageUpload() {
        setupTestPet();
        
        ApiResponse apiResponse = petClient.uploadImage(testPetId);

        assertAll("Upload Image Assertions",
                () -> assertThat("Response code should be 200", apiResponse.getCode(), is(200)),
                () -> assertThat("Response type should be 'unknown'", apiResponse.getType(), is("unknown")),
                () -> assertThat("Response message should match", apiResponse.getMessage(), notNullValue())
        );

        cleanupTestPet();
    }

    @Test
    public void testUpdatePet() {
        setupTestPet();
        
        PetRequest updateRequest = PetTestDataBuilder.buildUpdatePetRequest(testPetId);
        PetResponse response = petClient.updatePet(updateRequest);
        
        assertAll("Pet Update Assertions",
            () -> assertThat("Pet ID should match", response.getId(), is(testPetId)),
            () -> assertThat("Updated pet name should match", response.getName(), is(updateRequest.getName())),
            () -> assertThat("Pet status should match", response.getStatus(), is(updateRequest.getStatus())),
            () -> assertThat("Pet category ID should match", response.getCategory().getId(), is(updateRequest.getCategory().getId())),
            () -> assertThat("Pet category name should match", response.getCategory().getName(), is(updateRequest.getCategory().getName())),
            () -> assertThat("Pet photo URLs should not be null", response.getPhotoUrls(), is(updateRequest.getPhotoUrls())),
            () -> assertThat("First tag ID should match", response.getTags().get(0).getId(), is(updateRequest.getTags().get(0).getId())),
            () -> assertThat("First tag name should match", response.getTags().get(0).getName(), is(updateRequest.getTags().get(0).getName()))
        );

        cleanupTestPet();
    }

    @Test
    public void testGetPetsByStatus() {
        PetResponse[] response = petClient.findPetsByStatus("available");
        //Since too many result is returned we only check that array length is not zero
        assertTrue(response.length > 0);
    }

    @Test
    public void testGetPetById() {
        setupTestPet();

        PetResponse response = petClient.getPetByIdWithRetry(testPetId, 10,1000);

        assertAll("Pet Response Assertions",
                () -> assertThat("Response should not be null", response, is(notNullValue())),
                () -> assertThat("Pet ID should match", response.getId(), is(testPetId)),
                () -> assertThat("Pet name should match", response.getName(), is(testPetRequest.getName())),
                () -> assertThat("Pet status should match", response.getStatus(), is(testPetRequest.getStatus())),
                () -> assertThat("Pet category should not be null", response.getCategory(), is(notNullValue())),
                () -> assertThat("Pet category ID should match", response.getCategory().getId(), is(testPetRequest.getCategory().getId())),
                () -> assertThat("Pet category name should match", response.getCategory().getName(), is(testPetRequest.getCategory().getName())),
                () -> assertThat("Pet photo URLs should not be null", response.getPhotoUrls(), is(notNullValue())),
                () -> assertThat("Pet photo URLs should not be empty", response.getPhotoUrls(), is(not(emptyIterable()))),
                () -> assertThat("Pet tags should not be null", response.getTags(), is(notNullValue())),
                () -> assertThat("Pet tags should not be empty", response.getTags(), is(not(emptyIterable()))),
                () -> assertThat("First tag ID should match", response.getTags().get(0).getId(), is(testPetRequest.getTags().get(0).getId())),
                () -> assertThat("First tag name should match", response.getTags().get(0).getName(), is(testPetRequest.getTags().get(0).getName()))
        );

        cleanupTestPet();
    }

    @Test
    public void testUpdatePetWithFormData() {
        setupTestPet();

        UpdatePetFormRequest updateRequest = PetTestDataBuilder.buildUpdatePetFormRequest();
        ApiResponse response = petClient.updatePetWithFormWithRetry(testPetId, updateRequest, 10,1000);

        assertAll("Pet Update With Form Assertions",
                () -> assertThat("Pet ID should match", response.getCode(), is(200)),
                () -> assertThat("Updated pet name should match", response.getType(), is("unknown")),
                () -> assertThat("First tag name should match", response.getMessage(), is(testPetId.toString()))
        );

        cleanupTestPet();
    }

    @Test
    public void testDeletePet() {
        setupTestPet();
        
        ApiResponse response = petClient.deletePetWithRetry(testPetId, 10, 1000);

        assertAll("Delete Pet Assertions",
                () -> assertThat("Response code should be 200", response.getCode(), is(200)),
                () -> assertThat("Response type should be 'unknown'", response.getType(), is("unknown")),
                () -> assertThat("Response message should match the pet id", response.getMessage(), is(testPetId.toString()))
        );

        // No cleanup needed as this is a delete test
    }

    // ***************** Negative cases ********************

    @Test
    public void testInvalidImageUpload() {
        ApiResponse apiResponse = petClient.uploadImage(134134553423142541L);

        assertAll("Negative Image Assertions",
                () -> assertThat("Response code should be 200", apiResponse.getCode(), is(200)),
                () -> assertThat("Response type should be 'unknown'", apiResponse.getType(), is("unknown")),
                () -> assertThat("Response message should match", apiResponse.getMessage(), notNullValue())
        );
    }

    @Test
    public void testCreatePetWithInvalidData() {
        PetRequest newPetRequest = PetTestDataBuilder.buildInvalidPetRequest();
        PetResponse response = petClient.createPet(newPetRequest);

        Long createdPetId = response.getId();

        assertAll("Pet Creation Assertions",
                () -> assertThat("Pet ID should exist", response.getId(), notNullValue()),
                () -> assertThat("Pet name should match", response.getName(), is(newPetRequest.getName())),
                () -> assertThat("Pet status should match", response.getStatus(), is(newPetRequest.getStatus()))
        );

        // Cleanup the invalid pet
        testPetId = createdPetId;
        cleanupTestPet();
    }

    @Test
    public void testUpdatePetWithInvalidData() {
        setupTestPet();

        PetRequest updateRequest = PetTestDataBuilder.buildInvalidUpdatePetRequest(testPetId);
        PetResponse response = petClient.updatePet(updateRequest);

        assertAll("Pet Update Assertions",
                () -> assertThat("Pet ID should match", response.getId(), is(testPetId)),
                () -> assertThat("Updated pet name should match", response.getName(), is("")),
                () -> assertThat("Pet status should match", response.getStatus(), is(updateRequest.getStatus())),
                () -> assertThat("Pet category ID should match", response.getCategory().getId(), is(0L)),
                () -> assertThat("Pet category name should match", response.getCategory().getName(), is(""))
        );

        cleanupTestPet();
    }

    @Test
    public void testUpdatePetWithInvalidPetId() {
        PetRequest updateRequest = PetTestDataBuilder.buildInvalidUpdatePetRequest(Constants.INVALID_ID.getConstant());
        PetResponse response = petClient.updatePet(updateRequest);

        assertAll("Pet Update Assertions",
                () -> assertThat("Pet ID should match", response.getId(), is(Constants.INVALID_ID.getConstant())),
                () -> assertThat("Updated pet name should match", response.getName(), is("")),
                () -> assertThat("Pet status should match", response.getStatus(), is(updateRequest.getStatus())),
                () -> assertThat("Pet category ID should match", response.getCategory().getId(), is(0L)),
                () -> assertThat("Pet category name should match", response.getCategory().getName(), is(""))
        );
    }

    @Test
    public void testGetPetByStatusWithInvalidStatus() {
        PetResponse[] response = petClient.findPetsByStatus("oguzhan");
        // Array is empty for invalid status
        assertTrue(response.length == 0);
    }

    @Test
    public void testGetPetWithInvalidId() {
        PetResponse response = petClient.getPetById(Constants.INVALID_ID.getConstant());

        assertAll("Pet Response Assertions",
                () -> assertThat("Pet ID should match", response.getCode(), is(1)),
                () -> assertThat("Updated pet name should match", response.getType(), is("error")),
                () -> assertThat("First tag name should match", response.getMessage(), is("Pet not found"))
        );
    }

    @Test
    public void testFormUpdatePetWithInvalidData() {
        UpdatePetFormRequest updateRequest = PetTestDataBuilder.buildUpdatePetFormRequest();
        ApiResponse response = petClient.updatePetWithFormWithRetry(Constants.INVALID_ID.getConstant(), updateRequest, 10, 1000);

        assertAll("Pet Negative Update With Form Assertions",
                () -> assertThat("Pet ID should match", response.getCode(), is(200)),
                () -> assertThat("Updated pet name should match", response.getType(), is("unknown")),
                () -> assertThat("First tag name should match", response.getMessage(), is(Constants.INVALID_ID.getConstant().toString()))
        );
    }

    @Test
    public void testFormUpdatePetWithInvalidId() {
        setupTestPet();

        UpdatePetFormRequest updateRequest = PetTestDataBuilder.builInvaliddUpdatePetFormRequest();
        ApiResponse response = petClient.updatePetWithFormWithRetry(testPetId, updateRequest, 10, 1000);

        assertAll("Pet Negative Update With Form Assertions",
                () -> assertThat("Pet ID should match", response.getCode(), is(200)),
                () -> assertThat("Updated pet name should match", response.getType(), is("unknown")),
                () -> assertThat("First tag name should match", response.getMessage(), is(String.valueOf(testPetId)))
        );

        cleanupTestPet();
    }

    @Test
    public void testDeletePetWithInvalidId() throws InterruptedException {
        Long invalidId = Constants.INVALID_ID.getConstant();
        Response response = petClient.deletePetUntilNotFound(invalidId, 5, 500);

        assertAll("Delete pet with invalid ID assertions",
                () -> assertThat("Response code should be 404", response.getStatusCode(), is(404))
        );
    }
} 