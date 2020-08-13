package com.mashreq.wealth.helper;

import com.mashreq.wealth.enums.BusinessGroupType;
import com.mashreq.wealth.exception.CustomerInfoMapperException;
import com.mashreq.wealth.model.CustomerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CSVReader {


    private Map<String, CustomerInfo> customerInfoMap = null;

    public CSVReader() {
        customerInfoMap = new ConcurrentHashMap<>();
    }

    public Map<String, CustomerInfo> getCustomerInfoMap() {
        return customerInfoMap;
    }
    public void setCustomerInfoMap(Map<String, CustomerInfo> customerInfoMap) {
        this.customerInfoMap = customerInfoMap;
    }

    public Map<String, CustomerInfo> readCustomerInfoFromCSV(String inputFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            customerInfoMap = br.lines()  // returns a stream object.
                    .skip(1) // skip the first line in the CSV file, which in this case is the header of the file
                    .map(mapToCustomerInfoDto) //map to the customerInfo for each line in the file
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(CustomerInfo::<String>getCifId, customerInfo -> customerInfo));
        } catch (IOException e) {
            log.error("Cannot read and open filename:{}, ExceptionMessage:{} ", inputFilePath, e.getMessage());
        }
        return Collections.unmodifiableMap(customerInfoMap);
    }

    /**
     *
     */
    public final Function<String, CustomerInfo> mapToCustomerInfoDto = line -> {
        CustomerInfo customerInfoDto = null;
        //throws exception if splitted fields are null.
        try {

            String[] p = line.split(Pattern.quote("|"));// a Txt  has pip symbol separated, \\ escape is needed
            //the first column in the csv file
            log.info("start mapping file lines to CustomerInfoDto object for ClientId:{}", p[0]);
            log.info("start mapping file lines to CustomerInfoDto object for BusinessGroup:{}", p[120]);
            log.info("start mapping file lines to CustomerInfoDto object for Email:{}", p[95]);
            log.info("start mapping file lines to CustomerInfoDto object for Shortname:{}", p[3]);
            if ((!Objects.isNull(p[0]) && p[0].trim().length() == 9)
                    && (!Objects.isNull(p[120]) && !p[120].isEmpty())
                    && (!Objects.isNull(p[95]) && !p[95].isEmpty())) {
                customerInfoDto = new CustomerInfo();
                customerInfoDto.setCifId(p[0].trim());
                customerInfoDto.setEmail(p[95].trim());
                customerInfoDto.setBusinessGroupType(BusinessGroupType.findBusinessGroupTypeByValue(p[120].trim()));
            }else
                throw new CustomerInfoMapperException("CustomerInfoMapperException");

        } catch (Exception e) {
            log.error(" failed to map file lines to CustomerInfoDto object has failed, wrong data:{}", e.getMessage());
        }
        return customerInfoDto;
    };

}
