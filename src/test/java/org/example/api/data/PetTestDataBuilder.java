package org.example.api.data;

import org.example.api.models.request.PetRequest;
import org.example.api.models.request.UpdatePetFormRequest;

import java.util.Arrays;
import java.util.Collections;

// Test data builder for Pet API tests
public class PetTestDataBuilder {

    // Builds a valid pet creation request
    public static PetRequest buildCreatePetRequest() {
        return PetRequest.builder()
                .id(System.currentTimeMillis())
                .name("Oguzhan' s Dog")
                .status("healthy")
                .category(new PetRequest.Category(1L, "Dogs"))
                .photoUrls(Collections.singletonList("https://images.pexels.com/photos/1805164/pexels-photo-1805164.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"))
                .tags(Collections.singletonList(new PetRequest.Tag(1L, "tag1")))
                .build();
    }

    // Builds a valid pet update request
    public static PetRequest buildUpdatePetRequest(Long petId) {
        return PetRequest.builder()
                .id(petId)
                .category(new PetRequest.Category(2L, "Cats"))
                .name("Oguzhan's Catt")
                .photoUrls(Arrays.asList("https://example.com/updated1.jpg"))
                .tags(Arrays.asList(buildTag(1L, "updated")))
                .status("sold")
                .build();
    }

    // Builds an invalid pet update request for negative testing
    public static PetRequest buildInvalidUpdatePetRequest(Long petId) {
        return PetRequest.builder()
                .id(petId)
                .category(new PetRequest.Category(null, ""))
                .name("")
                .photoUrls(null)
                .tags(null)
                .status("invalid_status")
                .build();
    }

    // Builds a valid update pet form request
    public static UpdatePetFormRequest buildUpdatePetFormRequest() {
        return UpdatePetFormRequest.builder()
                .name("Updated Form Name")
                .status("sold")
                .build();
    }

    // Builds an invalid update pet form request
    public static UpdatePetFormRequest builInvaliddUpdatePetFormRequest() {
        return UpdatePetFormRequest.builder()
                .name("")
                .status("invalidStatus")
                .build();
    }

    // Helper method to build a tag
    private static PetRequest.Tag buildTag(Long id, String name) {
        return new PetRequest.Tag(id, name);
    }

    // Builds an invalid pet request
    public static PetRequest buildInvalidPetRequest() {
        return PetRequest.builder()
                .id(null)
                .name("")
                .status("invalid_status")
                .category(null)
                .photoUrls(null)
                .tags(null)
                .build();
    }
} 
