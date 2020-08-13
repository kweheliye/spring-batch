package com.mashreq.wealth.exception;

import com.mashreq.wealth.enums.ErrorCodes;

/**
 * The InvalidFilenameException wraps all checked standard Java exception and
 * enriches them with a custom Error code
 *
 */
public class CustomerInfoMapperException extends  RuntimeException{
    private  Integer errorCode;

    public CustomerInfoMapperException(String message){
        super(message);
    }
    public CustomerInfoMapperException(String message, Throwable cause){
        super(message, cause);
    }
    public CustomerInfoMapperException(String message, Throwable cause, ErrorCodes errorCode ){
        super(message, cause);
        this.errorCode = errorCode.getCode();
    }

    public CustomerInfoMapperException(String message, ErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode.getCode();
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
