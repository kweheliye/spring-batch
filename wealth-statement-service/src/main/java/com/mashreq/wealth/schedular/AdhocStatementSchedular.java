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
public class AdhocStatementSchedular {

    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    @Qualifier("adhocStatementJob")
    Job job;

    @Scheduled(cron = "${cron.job.adhoc}")
    @SchedulerLock(name = "AdhocStatementSchedular_scheduledAdhocStatement", lockAtLeastFor = "${shedlock.adhoc.atleast}", lockAtMostFor = "${shedlock.adhoc.atmost}")
    public void scheduledAdhocStatement() throws Exception {
        log.info("AdhocStatementSchedular task has started at:{}", LocalDate.now());
        jobLauncher.run(
                job, new JobParametersBuilder()
                        .addLong("Adhoc Job ID", System.nanoTime())
                        .toJobParameters()
        );
        log.info("AdhocStatementSchedular task has ended at:{}", LocalDate.now());

    }
}
