package com.mashreq.wealth.batch.sender;

import com.mashreq.wealth.config.EmailProperties;
import com.mashreq.wealth.entity.Statement;
import com.mashreq.wealth.enums.EmailStatus;
import com.mashreq.wealth.enums.JobType;
import com.mashreq.wealth.model.CustomerInfo;
import com.mashreq.wealth.model.Mail;
import com.mashreq.wealth.service.CustomerInfoService;
import com.mashreq.wealth.service.MailService;
import com.mashreq.wealth.service.StatementService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;


@Slf4j
public class StatementMailSenderItemWriter implements ItemWriter<Statement> {

    @Autowired
    private StatementService statementService;
    @Autowired
    @Qualifier("EOMCustomerInfoService")
    private CustomerInfoService eomCustomerInfoService;
    @Autowired
    @Qualifier("AdhocCustomerInfoService")
    private CustomerInfoService adhocCustomerInfoService;
    @Autowired
    private MailService mailService;
    @Autowired
    private Configuration freemarkerConfig;
    @Autowired
    private EmailProperties emailProperties;
    @Value("${template.statement}")
    private String templateStatement;
    @Value("${template.folder}")
    private String templatePathFolder;

    public StatementMailSenderItemWriter() {
    }

    //for testing purpose
    public StatementMailSenderItemWriter(StatementService statementService, CustomerInfoService adhocCustomerInfoService,
                                         CustomerInfoService customerInfoService, MailService mailService,
                                         Configuration freemarkerConfig, EmailProperties emailProperties) {
        this.statementService = statementService;
        this.eomCustomerInfoService = customerInfoService;
        this.mailService = mailService;
        this.freemarkerConfig = freemarkerConfig;
        this.emailProperties = emailProperties;
        this.adhocCustomerInfoService = adhocCustomerInfoService;
    }

    @Override
    public void write(List<? extends Statement> statements) throws Exception {
        log.info("Sending email process ItemWriter has started");
        for (Statement statement : statements) {
            try {
                //LOAD customer information based on JOB-TYPE
                CustomerInfo customerInfo = null;
                if (statement.getJobType() == JobType.EOM)
                    customerInfo = eomCustomerInfoService.getCustomerInfoByCif(statement.getCifId());
                else if (statement.getJobType() == JobType.ADHOC)
                    customerInfo = adhocCustomerInfoService.getCustomerInfoByCif(statement.getCifId());

                //Only send email when customer is found
                if (customerInfo != null) {
                    log.info("Preparing to send email for filename:{} and for cifId:{}", statement.getFilename(), statement.getCifId());
                    Mail mail = new Mail();
                    mail.setMailSubject(emailProperties.getSubject());
                    mail.setMailFrom(emailProperties.getFrom());
                    mail.setMailTo(customerInfo.getEmail());
                    mail.setAttachmentFile(statement.getFile());
                    mail.setAttachmentName(statement.getAttachmentFilename());
                    mail.setCustomerInfo(customerInfo);

                    freemarkerConfig.setClassForTemplateLoading(this.getClass(), this.templatePathFolder);
                    Template template = freemarkerConfig.getTemplate(this.templateStatement);

                    mailService.sendMailStatementToCustomer(mail, template);
                    //Update email_status to success and then send it
                    statement.getStatementStatus().setEmailStatus(EmailStatus.SENT_SUCCESS);
                    log.info("Email successfully sent for filename:{} ", statement.getFilename());
                } else {
                    //NO customer found in this case
                    log.error("Customer not found for filename:{}", statement.getFilename());
                }
            } catch (Exception ex) {
                statement.getStatementStatus().setEmailStatus(EmailStatus.SENT_FAILURE);
                log.error("Failure in sending email for filename:{}, exception message:{}", statement.getFilename(), ex.getMessage());
            }
            statementService.saveOrUpdate(statement);
        }

    }

}
