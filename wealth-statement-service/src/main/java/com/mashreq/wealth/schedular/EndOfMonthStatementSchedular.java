package com.mashreq.wealth.schedular;


import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class EndOfMonthStatementSchedular {

    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    @Qualifier("eomStatementJob")
    Job job;

    @Scheduled(cron = "${cron.job.eom}")
    @SchedulerLock(name = "EndOfMonthStatementSchedular_scheduledEndOfMonthStatement", lockAtLeastFor = "${shedlock.eom.atleast}", lockAtMostFor = "${shedlock.eom.atmost}")
    public void scheduledEndOfMonthStatement() throws Exception {
        log.info("EndOfMonthStatementSchedular task has started at:{}", LocalDate.now());
        jobLauncher.run(
                job, new JobParametersBuilder()
                        .addLong("Job ID", System.nanoTime())
                        .toJobParameters()
        );
        log.info("EndOfMonthStatementSchedular task has ended at:{}", LocalDate.now());

    }
}
