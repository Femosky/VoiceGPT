package com.example.group6finalgroupproject.model;

/**
 * Wrapper for API error responses.
 */
public class ErrorResponse {
    private ErrorData error;

    // Getters and setters

    public ErrorData getError() {
        return error;
    }

    public void setError(ErrorData error) {
        this.error = error;
    }
}
