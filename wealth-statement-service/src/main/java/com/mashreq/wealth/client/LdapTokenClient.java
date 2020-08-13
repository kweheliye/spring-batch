package com.mashreq.wealth.client;


import com.mashreq.wealth.client.response.LdapTokenResponse;
import com.mashreq.wealth.constant.WealthStatementConstants;
import com.mashreq.wealth.exception.LdapException;
import com.mashreq.wealth.model.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class LdapTokenClient {

    private RestTemplate restTemplate;
    private final String ldapAuthServiceGateway;
    private final String ldapClientIdKey;
    private final String ldapAdUsername;
    private final String ldapAdPass;
    private final String ldapAuthHeaderUser;
    private final String ldapAuthHeaderPass;
    private final String ldapAdPassKey;



    @Autowired
    public LdapTokenClient(@Value("${ldap.service.url}") final String ldapAuthServiceGateway,
                           @Value("${ldap.client.id}") final String ldapClientIdKey,
                           @Value("${ldap.ad.username}") final String ldapAdUsername,
                           @Value("${ldap.ad.password}") final String ldapAdPass,
                           @Value("${ldap.auth.header.username}") final String ldapAuthHeaderUser,
                           @Value("${ldap.auth.header.password}") final String ldapAuthHeaderPass,
                           @Value("${ldap.ad.pass_key}") final String ldapAdPassKey,
                           RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.ldapAuthServiceGateway = ldapAuthServiceGateway;
        this.ldapClientIdKey = ldapClientIdKey;
        this.ldapAdUsername = ldapAdUsername;
        this.ldapAdPass = ldapAdPass;
        this.ldapAuthHeaderUser = ldapAuthHeaderUser;
        this.ldapAuthHeaderPass = ldapAuthHeaderPass;
        this.ldapAdPassKey = ldapAdPassKey;
    }

    public void getLdapToken() {
        LdapTokenResponse ldapTokenResponse = new LdapTokenResponse();

        try {
            HttpHeaders headers = updateLdapAuthServiceHeaders();
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromHttpUrl(this.ldapAuthServiceGateway
                            + WealthStatementConstants.LDAP_AUTH_ENDPOINT)
                    .queryParam(WealthStatementConstants.LDAP_CLIENT_ID_KEY,
                            this.ldapClientIdKey)
                    .queryParam(WealthStatementConstants.LDAP_GRANT_TYPE_KEY, WealthStatementConstants.LDAP_GRANT_TYPE)
                    .queryParam(WealthStatementConstants.LDAP_AD_USERNAME_KEY, this.ldapAdUsername)
                    .queryParam(this.ldapAdPassKey, this.ldapAdPass);

            ResponseEntity<LdapTokenResponse> response = restTemplate.exchange(uriBuilder.toUriString(),
                    HttpMethod.POST, requestEntity, LdapTokenResponse.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                ldapTokenResponse = response.getBody();
                Token.setJwtToken(response.getBody().getAccessToken());
            }

        } catch (HttpServerErrorException e) {
            log.error("LDAPAUTH Service InternalServerError ..");
            throw new LdapException("LDAP Error", e);
        } catch (Exception ex) {
            log.info("LDAPAUTH service Response... {}", ex);
            throw ex;

        }
    }

    private HttpHeaders updateLdapAuthServiceHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String authUser = this.ldapAuthHeaderUser;
        String authPwd = this.ldapAuthHeaderPass;
        headers.setBasicAuth(authUser, authPwd);
        return headers;
    }
}
