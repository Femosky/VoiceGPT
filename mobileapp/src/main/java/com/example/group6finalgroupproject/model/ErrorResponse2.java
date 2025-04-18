package com.example.group6finalgroupproject.model;

/**
 * Top-level error response wrapper when the API returns an error.
 */
public class ErrorResponse2 {
    private ErrorData2 error;

    // Getters and setters

    public ErrorData2 getError() {
        return error;
    }

    public void setError(ErrorData2 error) {
        this.error = error;
    }
}
