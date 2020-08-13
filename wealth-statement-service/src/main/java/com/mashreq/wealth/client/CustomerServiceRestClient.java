package com.mashreq.wealth.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.wealth.client.request.CustomerSearchRequest;
import com.mashreq.wealth.enums.BusinessGroupType;
import com.mashreq.wealth.enums.EndpointType;
import com.mashreq.wealth.model.CustomerInfo;
import com.mashreq.wealth.model.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
public class CustomerServiceRestClient {
    private RestTemplate restTemplate;
    private final String customerServiceGatewayUrl;
    private String customerServiceEndpointProfile;
    private String customerServiceEndpointSearch;
    private LdapTokenClient ldapTokenClient;

    @Autowired
    public CustomerServiceRestClient(RestTemplate restTemplate, LdapTokenClient ldapTokenClient,
                                     @Value("${customer.service.url}") final String customerServiceGatewayUrl,
                                     @Value("${customer.service.profile.path}") final String customerServiceEndpointProfile,
                                     @Value("${customer.service.search.path}") final String customerServiceEndpointSearch) {
        this.restTemplate = restTemplate;
        this.customerServiceGatewayUrl = customerServiceGatewayUrl;
        this.customerServiceEndpointProfile = customerServiceEndpointProfile;
        this.customerServiceEndpointSearch = customerServiceEndpointSearch;
        this.ldapTokenClient = ldapTokenClient;
    }

    /**
     * Retrieve customer information from CustomerService profile endpoint by CifId
     *
     * @param cifId
     * @return
     */
    public CustomerInfo getCustomerProfileByCifId(String cifId) {
        String uri = null;
        CustomerInfo customerInfo = null;
        try {
            log.info("Customer Profile is being fetched from customer-service endpoint for cifId:{}:", cifId);
            uri = customerServiceGatewayUrl.concat(customerServiceEndpointProfile + cifId).concat("/profile");
            HttpHeaders headers = updateServiceHeaders();
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            //We only need three fields. Once we get the result as string. Go through the string to get the right values
            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(),
                    HttpMethod.GET, entity, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                log.info("CustomerService Profile endpoint response HttpStatus.OK");
                customerInfo = mapToCustomerInfo(responseEntity.getBody(), EndpointType.PROFILE);
            }
        } catch (Exception ex) {
            log.error("Customer Info could not be fetched from CustomerService Profile for cif:{}, ExGetMessage:{}", cifId, ex.getMessage());
        }
        return customerInfo;
    }


    /**
     * Retrieve customer information from CustomerService profile endpoint by CifId
     *
     * @param cifId
     * @return
     */
    public CustomerInfo searchCustomerTypeByCifId(String cifId) {
        String uri = null;
        CustomerInfo customerInfo = null;
        try {
            log.info("CustomerType is being searched through customer-service endpoint for cifId:{}:", cifId);
            uri = customerServiceGatewayUrl.concat(customerServiceEndpointSearch);
            HttpHeaders headers = updateServiceHeaders();
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);

            CustomerSearchRequest customerSearchRequest = CustomerSearchRequest.builder().cifId(cifId).build();
            HttpEntity<?> entity = new HttpEntity<>(customerSearchRequest, headers);


            //We only need three fields. Once we get the result as string. Go through the string to get the right values
            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(),
                    HttpMethod.POST, entity, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                log.info("CustomerService Search Endpoint Response HttpStatus.OK");
                customerInfo = mapToCustomerInfo(responseEntity.getBody(), EndpointType.SEARCH);
            }
        } catch (Exception ex) {
            log.error("Customer Info could not be fetched from CustomerService Search for cif:{}, ExGetMessage:{}", cifId, ex.getMessage());
        }
        return customerInfo;
    }


    /**
     * Helper function to filter and select desired fields from returned object.
     *
     * @param response
     * @return
     * @throws JsonProcessingException
     */
    private CustomerInfo mapToCustomerInfo(String response, EndpointType endpointType) throws JsonProcessingException {
        CustomerInfo customerInfo = null;
        try {
            customerInfo = new CustomerInfo();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response);
            //Get only fields that's required to send email
            JsonNode rootNode = node.get("data");
            //For profile endpoint, Extract email from response
            if (endpointType == EndpointType.PROFILE) {
                JsonNode emailField = rootNode.get("primaryEmail");
                JsonNode nameField = rootNode.get("name");
                customerInfo.setEmail(emailField.asText());
            } else if (endpointType == EndpointType.SEARCH) {
                JsonNode categoryField = rootNode.get(0).get("category");
                JsonNode customerTypeField = categoryField.get("customerType");
                customerInfo.setBusinessGroupType(BusinessGroupType.findBusinessGroupTypeByValue(customerTypeField.asText()));
            }
        } catch (IOException ex) {
            log.error("Failed to map CustomerServiceResponse Object to customerInfoData reason:{}", ex.getMessage());
        }
        log.info("mapped customerInfo :{}", customerInfo);
        return customerInfo;
    }

    private HttpHeaders updateServiceHeaders() {
        HttpHeaders headers = new HttpHeaders();
        if (!Token.isThereAJwtToken()) {
            log.error("Getting a new token, the token should have been generated before the job started");
            ldapTokenClient.getLdapToken();
        }
        String token = Token.getJwtToken();
        headers.setBearerAuth(token);
        return headers;
    }
}
