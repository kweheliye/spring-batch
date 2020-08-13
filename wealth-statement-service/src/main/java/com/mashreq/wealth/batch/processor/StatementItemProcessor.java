package com.mashreq.wealth.batch.processor;

import com.mashreq.wealth.entity.Statement;
import com.mashreq.wealth.entity.StatementStatus;
import com.mashreq.wealth.enums.FileNameStatus;
import com.mashreq.wealth.enums.JobType;
import com.mashreq.wealth.enums.ProcessStatus;
import com.mashreq.wealth.service.StatementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

@Slf4j
public class StatementItemProcessor implements ItemProcessor<File, Statement> {

    private static final int EXPECTED_PART_COUNT = 4;
    private StatementService statementService;
    private JobType jobType;

    @Autowired
    public StatementItemProcessor(StatementService statementService) {
        this.statementService = statementService;
    }

    /**
     * Filter all files that has been processed before.
     * Only process files that's not in DB
     *
     * @param fileItem
     * @return
     * @throws Exception
     */
    @Override
    public Statement process(final File fileItem) throws Exception {
        String filename = fileItem.getName();

        //return if file exist in DB
        if (statementService.findByFilename(filename) != null)
            return null;

        log.info("Process started for Filename: {} , has started", filename);

        Statement statement = parseFileName(filename);
        //file will not be saved, however is needed for sending the mail attachment
        statement.setFile(fileItem);

        //saving it to DB, regardless if filename is valid or invalid, we need to capture it the status
        log.info("Saving filename:{} to DB , ProcessStatus:{}", statement.getFilename(), statement.getStatementStatus().getProcessStatus());
        statementService.saveOrUpdate(statement);
        return statement;
    }

    // Package protected for test purpose
    Statement parseFileName(String filename) {
        //set and initialize properties before saving
        Statement statement = new Statement();
        StatementStatus statementStatus = new StatementStatus();

        String[] splitFilename = filename.split("_");
        statement.setFilename(filename);
        //referencing entities
        statementStatus.setStatement(statement);
        statement.setStatementStatus(statementStatus);

        if (splitFilename.length == EXPECTED_PART_COUNT) {
            try {
                statement.setDate(splitFilename[2]);
                //remove the last dot followed by one or more characters
                statement.setCifId(splitFilename[3].replaceFirst("[.][^.]+$", ""));
                statement.setAttachmentFilename(splitFilename[0] + "_" + splitFilename[1] + "_" + splitFilename[2] + ".pdf");
                statement.setJobType(this.jobType);
                statementStatus.setFileNameStatus(FileNameStatus.VALID);
                statementStatus.setProcessStatus(ProcessStatus.STARTED);
            } catch (Exception e) {
                log.error("An error has been thrown when processing filename: {} {}", filename, e);
            }
        } else {
            log.error("Error when parsing file {}, not compliant to expected pattern");
        }

        return statement;
    }

    @BeforeStep
    public void setJobName(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        this.jobType= JobType.findJobTypeByName(jobExecution.getJobInstance().getJobName());
    }
}