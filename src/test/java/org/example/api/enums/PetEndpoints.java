package org.example.api.enums;

// Enumeration of Pet API endpoints
public enum PetEndpoints {

    UPLOAD_IMAGE("/pet/{petId}/uploadImage"),
    CREATE_PET("/pet"),
    UPDATE_PET("/pet"),
    FIND_BY_STATUS("/pet/findByStatus"),
    FIND_BY_TAGS("/pet/findByTags"),
    GET_PET_BY_ID("/pet/{petId}"),
    UPDATE_PET_WITH_FORM("/pet/{petId}"),
    DELETE_PET("/pet/{petId}");

    private final String path;

    PetEndpoints(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
} 