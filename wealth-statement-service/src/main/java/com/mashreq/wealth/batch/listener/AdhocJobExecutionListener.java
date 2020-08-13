package com.mashreq.wealth.batch.listener;

import com.mashreq.wealth.client.LdapTokenClient;
import com.mashreq.wealth.model.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class AdhocJobExecutionListener implements JobExecutionListener {

    private LdapTokenClient ldapTokenClient = null;

    public AdhocJobExecutionListener(LdapTokenClient ldapTokenClient) {
        this.ldapTokenClient = ldapTokenClient;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        Token.setJwtToken(null);
        ldapTokenClient.getLdapToken();
        log.info("jwtToken initialized {}", Token.getJwtToken());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Token.setJwtToken(null);
    }


}
