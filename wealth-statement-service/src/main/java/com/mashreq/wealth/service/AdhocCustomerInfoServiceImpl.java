package com.mashreq.wealth.service;

import com.mashreq.wealth.client.CustomerServiceRestClient;
import com.mashreq.wealth.enums.BusinessGroupType;
import com.mashreq.wealth.model.CustomerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("AdhocCustomerInfoService")
@Slf4j
public class AdhocCustomerInfoServiceImpl implements CustomerInfoService {

    private CustomerServiceRestClient customerServiceRestClient;

    @Autowired
    public AdhocCustomerInfoServiceImpl(CustomerServiceRestClient customerServiceRestClient) {
        this.customerServiceRestClient = customerServiceRestClient;
    }


    @Override
    public CustomerInfo getCustomerInfoByCif(String cifId) {
        CustomerInfo customerInfo = null;
        //fetch customer type from customer-service api, no need try and catch the calling methods has
        customerInfo = customerServiceRestClient.searchCustomerTypeByCifId(cifId);
        if (customerInfo != null) {
            if (isCustomerTypeRetail(customerInfo)) { //check whether the customer type is retail
                return customerServiceRestClient.getCustomerProfileByCifId(cifId);
            }
        }
        return customerInfo;
    }

    //is customer part of RETAIL segment
    private Boolean isCustomerTypeRetail(CustomerInfo customerInfo) {
        if (customerInfo != null && customerInfo.getBusinessGroupType() == BusinessGroupType.RETAIL) {
            return true;
        }
        return false;
    }


}
