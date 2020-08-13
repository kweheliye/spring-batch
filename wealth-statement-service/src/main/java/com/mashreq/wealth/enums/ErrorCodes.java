package com.mashreq.wealth.enums;

public enum ErrorCodes {
    INVALID_EMAIL_ADDRESS(203, "Invalid Email Address");

    private int code;
    private String message;

    ErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
