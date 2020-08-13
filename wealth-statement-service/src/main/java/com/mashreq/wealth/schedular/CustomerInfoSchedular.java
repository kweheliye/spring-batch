package com.mashreq.wealth.schedular;


import com.mashreq.wealth.helper.CSVReader;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class CustomerInfoSchedular {

    @Autowired
    private CSVReader csvReader;

    @Value("${file.path.customer-info}")
    private String customerInfoFilePath;


    @Scheduled(cron = "${cron.job.customer}")
    @SchedulerLock(name = "CustomerInfoSchedular_scheduledLoadCustomerInfoFromCSVFile", lockAtLeastFor = "${shedlock.customer.atleast}", lockAtMostFor = "${shedlock.customer.atmost}")
    public void scheduledLoadCustomerInfoFromCSVFile() throws Exception {
        log.info("CustomerInfoSchedular task has started at:{}", LocalDate.now());
        csvReader.readCustomerInfoFromCSV(customerInfoFilePath);

    }
}
