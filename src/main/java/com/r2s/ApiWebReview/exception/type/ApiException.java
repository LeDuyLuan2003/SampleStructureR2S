package com.r2s.ApiWebReview.exception.type;

public abstract class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
