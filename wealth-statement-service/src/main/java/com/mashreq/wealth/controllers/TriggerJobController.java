package com.mashreq.wealth.controllers;

import com.mashreq.wealth.client.CustomerServiceRestClient;
import com.mashreq.wealth.model.CustomerInfo;
import com.mashreq.wealth.schedular.AdhocStatementSchedular;
import com.mashreq.wealth.schedular.CustomerInfoSchedular;
import com.mashreq.wealth.schedular.EndOfMonthStatementSchedular;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TriggerJobController {

    private CustomerInfoSchedular customerInfoSchedular;
    private EndOfMonthStatementSchedular endOfMonthStatementSchedular;
    private AdhocStatementSchedular adhocStatementSchedular;
    private CustomerServiceRestClient  customerServiceRestClient;

    @Autowired
    private TriggerJobController(CustomerInfoSchedular customerInfoSchedular, EndOfMonthStatementSchedular endOfMonthStatementSchedular,
                                 AdhocStatementSchedular adhocStatementSchedular, CustomerServiceRestClient  customerServiceRestClient) {
        this.customerInfoSchedular = customerInfoSchedular;
        this.endOfMonthStatementSchedular = endOfMonthStatementSchedular;
        this.adhocStatementSchedular = adhocStatementSchedular;
        this.customerServiceRestClient = customerServiceRestClient;
    }

    @GetMapping("triggers/{jobType}")
    public ResponseEntity<String> triggerJobByJobType(@PathVariable String jobType) throws Exception {
        switch (jobType) {
            case "1":
                endOfMonthStatementSchedular.scheduledEndOfMonthStatement();
                return new ResponseEntity<>("EOM job has been triggered", HttpStatus.OK);
            case "2":
                customerInfoSchedular.scheduledLoadCustomerInfoFromCSVFile();
                return new ResponseEntity<>("LOAD_CUSTOMERS job has been triggered", HttpStatus.OK);
            case "3":
                adhocStatementSchedular.scheduledAdhocStatement();
                return new ResponseEntity<>("Adhoc Statement Schedular is called", HttpStatus.OK);
            case  "4":
                CustomerInfo customerProfile = customerServiceRestClient.getCustomerProfileByCifId("010586052");
                log.info("customer:{}",customerProfile);
                return new ResponseEntity<>("CUSTOMER_SERVICE PROFILE ENDPOINT IS called", HttpStatus.OK);
            case  "5":
                CustomerInfo customerSearch = customerServiceRestClient.searchCustomerTypeByCifId("010586052");
                log.info("customer:{}",customerSearch);
                return new ResponseEntity<>("CUSTOMER_SERVICE SEARCH ENDPOINT IS called", HttpStatus.OK);


            default:
                return new ResponseEntity<>("No job to run", HttpStatus.OK);
        }

    }

}
