package com.mashreq.wealth.exception;

import com.mashreq.wealth.enums.ErrorCodes;

/**
 * The InvalidFilenameException wraps all checked standard Java exception and
 * enriches them with a custom Error code
 *
 */
public class LdapException extends  RuntimeException{
    private  Integer errorCode;

    public LdapException(String message){
        super(message);
    }
    public LdapException(String message, Throwable cause){
        super(message, cause);
    }
    public LdapException(String message, Throwable cause, ErrorCodes errorCode ){
        super(message, cause);
        this.errorCode = errorCode.getCode();
    }

    public LdapException(String message, ErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode.getCode();
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
