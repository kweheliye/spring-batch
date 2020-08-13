package com.mashreq.wealth.exception;

import com.mashreq.wealth.enums.ErrorCodes;

/**
 * The InvalidFilenameException wraps all checked standard Java exception and
 * enriches them with a custom Error code
 *
 */
public class FileProcessException extends  RuntimeException{
    private  Integer errorCode;

    public FileProcessException(String message){
        super(message);
    }
    public FileProcessException(String message, Throwable cause){
        super(message, cause);
    }
    public FileProcessException(String message, Throwable cause, ErrorCodes errorCode ){
        super(message, cause);
        this.errorCode = errorCode.getCode();
    }

    public FileProcessException(String message, ErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode.getCode();
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
