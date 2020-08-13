package com.mashreq.wealth.batch.listener;

import com.mashreq.wealth.helper.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class EOMJobExecutionListener implements JobExecutionListener {

    @Autowired
    private CSVReader csvReader;

    private String customerInfoFilePath;

    public EOMJobExecutionListener(CSVReader csvReader, String customerInfoFilePath) {
        this.csvReader = csvReader;
        this.customerInfoFilePath = customerInfoFilePath;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        csvReader.readCustomerInfoFromCSV(customerInfoFilePath);
        log.info("BeforeJob starts Load CustomerInfo from CSV file. MapSize:{}", csvReader.getCustomerInfoMap().size());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        csvReader.setCustomerInfoMap(null);
        log.info("AfterJob finishes clear the MapSize {}",
                (null == csvReader.getCustomerInfoMap()) ? null : csvReader.getCustomerInfoMap().size());
    }


}
