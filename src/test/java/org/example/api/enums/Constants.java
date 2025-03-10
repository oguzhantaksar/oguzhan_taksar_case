package org.example.api.enums;

// Constants used across the API tests
public enum Constants {
    // An invalid pet ID for negative test cases
    INVALID_ID(999999999999999999L);

    private final Long constant;

    Constants(Long constant) {
        this.constant = constant;
    }

    public Long getConstant() {
        return constant;
    }
} 