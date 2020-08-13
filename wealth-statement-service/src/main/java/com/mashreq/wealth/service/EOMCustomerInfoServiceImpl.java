package com.mashreq.wealth.service;

import com.mashreq.wealth.enums.BusinessGroupType;
import com.mashreq.wealth.helper.CSVReader;
import com.mashreq.wealth.model.CustomerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.function.Predicate;

@Service("EOMCustomerInfoService")
@Slf4j
public class EOMCustomerInfoServiceImpl implements CustomerInfoService {
    private CSVReader csvReader;

    public EOMCustomerInfoServiceImpl(CSVReader csvReader) {
        this.csvReader = csvReader;
    }

    @Override
    public CustomerInfo getCustomerInfoByCif(String cifId) {
        return findCustomerByCifIdAndBusinessGroup(this.csvReader.getCustomerInfoMap(), filterCustomerByCifIdAndBusinessGroup(cifId) );
    }

    /**
     * Find customer by cifId and businessGroup
     * @param cifId
     * @return CustomerInfo object
     */

    private Predicate<Map.Entry<String, CustomerInfo>> filterCustomerByCifIdAndBusinessGroup(String cifId){
        return p-> p.getKey().equals(cifId) && (p.getValue().getBusinessGroupType() == BusinessGroupType.RBG);
    }
    private CustomerInfo findCustomerByCifIdAndBusinessGroup(Map<String, CustomerInfo> customersInfo, Predicate<Map.Entry<String, CustomerInfo>> customerInfoDtoPredicate) {
        return customersInfo.entrySet()
                .stream()
                .filter(customerInfoDtoPredicate)
                .map(Map.Entry::getValue)  //returning customerInfo object
                .findAny()  // the object is optional, it can be null
                .orElse(null); //return null when customerInfo is not present
    }

}
