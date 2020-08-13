package com.mashreq.wealth.service;

import com.mashreq.wealth.enums.BusinessGroupType;
import com.mashreq.wealth.helper.CSVReader;
import com.mashreq.wealth.model.CustomerInfo;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EOMCustomerInfoServiceImplTest {
    private CustomerInfoService customerInfoService = null;
    private CSVReader csvReaderMock = null;
    private Map<String, CustomerInfo> inputMap= null;

    @BeforeEach
    public void initialize() {
        csvReaderMock = mock(CSVReader.class);
        customerInfoService = new EOMCustomerInfoServiceImpl(csvReaderMock);
        inputMap = new ConcurrentHashMap<>();

        CustomerInfo
                customerInfoDto1= CustomerInfo.builder()
                .cifId("12345")
                .firstname("Mashreq1")
                .businessGroupType(BusinessGroupType.RBG)
                .build();

        CustomerInfo
                customerInfoDto2 = CustomerInfo.builder()
                .cifId("123456")
                .firstname("Mashreq1")
                .businessGroupType(BusinessGroupType.RBG)
                .build();

        inputMap.put(customerInfoDto1.getCifId(), customerInfoDto1);
        inputMap.put(customerInfoDto2.getCifId(), customerInfoDto2);
    }

    @Test
    void getInfoByClientId_whenGivenClientId_Exist_thenReturnCustomerInfo(){
        when(csvReaderMock.getCustomerInfoMap()).thenReturn(inputMap);
        CustomerInfo result =customerInfoService.getCustomerInfoByCif("12345");
        //not empty
        Assert.assertNotNull(result);
        Assert.assertEquals( String.valueOf( 12345),result.getCifId());
    }

    @Test
    void getInfoByClientId_whenGivenClientId_NotExist_thenReturnNull(){
        when(csvReaderMock.getCustomerInfoMap()).thenReturn(inputMap);
        CustomerInfo result =customerInfoService.getCustomerInfoByCif("12335");
        Assert.assertNull(result);
    }

}