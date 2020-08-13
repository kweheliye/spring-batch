package com.mashreq.wealth.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Fetch email properties into EmailProperties bean
 */
@Component
@Data
public class EmailProperties {

    @Value("${mail.host}")
    private String host;
    @Value("${mail.port}")
    private String port;
    @Value("${mail.username}")
    private String username;
    @Value("${mail.from}")
    private String from;
    @Value("${mail.password}")
    private String password;
    @Value("${mail.subject}")
    private String subject;
    @Value("${mail.debug}")
    private String debug;
    @Value("${mail.smtp.starttls.enable}")
    private String smtpStarttlsEnable;
    @Value("${mail.smtp.auth}")
    private String smtpAuth;


}
