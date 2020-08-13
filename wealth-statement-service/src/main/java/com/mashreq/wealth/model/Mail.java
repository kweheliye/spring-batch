package com.mashreq.wealth.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Data Transfer Object, this class will hold  information that's needed
 *  When sending email.
 */
@Data
public class Mail {
    private String mailFrom;
    private String mailTo;
    private String mailSubject;
    private String mailContent;
    private String contentType= "text/plain";
    private File attachmentFile;
    private String attachmentName;
    private CustomerInfo customerInfo;
}
