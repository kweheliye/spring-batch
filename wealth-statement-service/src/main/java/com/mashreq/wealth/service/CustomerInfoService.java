package com.mashreq.wealth.service;

import com.mashreq.wealth.model.CustomerInfo;

public interface CustomerInfoService {
    CustomerInfo getCustomerInfoByCif(String cif);
}
