package com.mashreq.wealth.config;

import com.mashreq.wealth.batch.listener.AdhocJobExecutionListener;
import com.mashreq.wealth.batch.listener.EOMJobExecutionListener;
import com.mashreq.wealth.batch.processor.StatementItemProcessor;
import com.mashreq.wealth.batch.reader.DirectoryItemReader;
import com.mashreq.wealth.batch.sender.StatementMailSenderItemWriter;
import com.mashreq.wealth.client.LdapTokenClient;
import com.mashreq.wealth.entity.Statement;
import com.mashreq.wealth.enums.JobType;
import com.mashreq.wealth.helper.CSVReader;
import com.mashreq.wealth.service.StatementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableBatchProcessing
@Slf4j
public class StatementBatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Value("${directory.statement.eom}")
    String directoryPathEOM;
    @Value("${directory.statement.adhoc}")
    String directoryPathAdhoc;
    @Autowired
    private StatementService statementService;
    @Autowired
    private LdapTokenClient ldapTokenClient;
    @Autowired
    private CSVReader csvReader;
    @Value("${file.path.customer-info}")
    private String customerInfoFilePath;


    @Bean(name = "eomStatementJob")
    public Job eomStatementJob() throws IOException {
        return jobBuilderFactory.get("eomStatementJob")
                .incrementer(new RunIdIncrementer())
                .listener(new EOMJobExecutionListener(csvReader, customerInfoFilePath))

                .flow(eomStatementStep())
                .end()
                .build();
    }

    @Bean(name = "adhocStatementJob")
    public Job adhocStatementJob() throws IOException {
        return jobBuilderFactory.get("adhocStatementJob")
                .incrementer(new RunIdIncrementer())
                .listener(new AdhocJobExecutionListener(ldapTokenClient))
                .flow(adhocStatementStep())
                .end()
                .build();
    }

    @Bean
    public Step adhocStatementStep() throws IOException {
        return stepBuilderFactory.get("adhocStatementStep").<File, Statement>chunk(10)
                .reader(adhocDirectoryItemReader())
                .processor(statementItemProcessor())
                .writer(statementMailSenderItemWriter())
                .build();
    }

    @Bean
    public Step eomStatementStep() throws IOException {
        return stepBuilderFactory.get("eomStatementStep").<File, Statement>chunk(10)
                .reader(eomDirectoryItemReader())
                .processor(statementItemProcessor())
                .writer(statementMailSenderItemWriter())
                .build();
    }


    @Bean
    public StatementItemProcessor statementItemProcessor() {

        return new StatementItemProcessor(statementService);
    }


    /**
     * This bean will return the ItemWriterObject
     * The responsibility of ItemWriter Object is to send email and and store status in DB
     *
     * @return ItemWriter object
     */
    @Bean
    public StatementMailSenderItemWriter statementMailSenderItemWriter() {
        return new StatementMailSenderItemWriter();
    }

    /**
     * The first process is to read all files from a directory
     * This bean will be managed by spring-container and the responsibility
     * of this bean is to read files from a given directory
     *
     * @return it will return all statement files that's in storage
     */
    @Bean
    @JobScope
    public DirectoryItemReader adhocDirectoryItemReader() throws IOException {
        return new DirectoryItemReader(this.resolveFilePath(JobType.ADHOC));
    }

    @Bean
    @JobScope
    public DirectoryItemReader eomDirectoryItemReader() throws IOException {
        return new DirectoryItemReader(this.resolveFilePath(JobType.EOM));
    }

    /**
     * a helper function to resolve absolute file path
     *
     * @return
     */
    private String resolveFilePath(JobType jobType) {
        LocalDate today = LocalDate.now();
        switch (jobType) {
            case EOM:
                return "file:" + directoryPathEOM + today.format(DateTimeFormatter.ofPattern("yyyyMM")) + File.separator + "*.pdf";
            case ADHOC:
                return "file:" + directoryPathAdhoc + today.format(DateTimeFormatter.ofPattern("yyyyMM")) + File.separator + "*.pdf";
            default:
                log.debug("resolveFilePath should not be in this line");
                return null;
        }
    }

}
