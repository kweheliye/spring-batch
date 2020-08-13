package com.mashreq.wealth;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The WealthStatementServiceApplication will send statements to customers
 * End of month and as Adhoc.
 *
 * @author Kamal Hashi
 * @version 1.0
 * @since 21/07/2020
 */

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "${shedlock.default}")
@EnableAsync
@EnableRetry
public class WealthStatementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WealthStatementServiceApplication.class, args);
    }

}
