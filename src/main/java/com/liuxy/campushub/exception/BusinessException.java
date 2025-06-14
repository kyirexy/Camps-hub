package com.liuxy.campushub.exception;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {
    private int errorCode;
    private String message;

    public BusinessException(String message) {
        super(message);
        this.message = message;
        this.errorCode = 500; // Default error code
    }

    public BusinessException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
} 