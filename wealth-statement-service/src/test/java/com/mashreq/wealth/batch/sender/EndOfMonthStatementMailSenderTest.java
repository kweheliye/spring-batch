package com.mashreq.wealth.batch.sender;

import com.mashreq.wealth.config.EmailProperties;
import com.mashreq.wealth.entity.Statement;
import com.mashreq.wealth.entity.StatementStatus;
import com.mashreq.wealth.enums.BusinessGroupType;
import com.mashreq.wealth.enums.JobType;
import com.mashreq.wealth.model.CustomerInfo;
import com.mashreq.wealth.model.Mail;
import com.mashreq.wealth.service.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.Arrays;

import static org.mockito.Mockito.*;


@Slf4j
class EndOfMonthStatementMailSenderTest {

    @TempDir
    public Path tempDir;

    private StatementMailSenderItemWriter itemMailSender;
    //mock
    private StatementService statementServiceMock;
    private CustomerInfoService eomCustomerInfoServiceMock;
    private CustomerInfoService adhocCustomerInfoServiceMock;

    private MailService mailServiceMock;
    private Statement statement;
    private StatementStatus statementStatus;
    private Configuration freeMarkConfigurationMock;
    private EmailProperties emailPropertiesMock;

    private Path file = null;
    private String filename = "201912311200_12333_44331_1.pdf";


    @BeforeEach
    public void initializeBeforeTest() {
        file = tempDir.resolve(filename);
        statementStatus = new StatementStatus();
        statement = Statement.builder()
                .cifId("013368406")
                .statementStatus(statementStatus)
                .file(file.toFile())
                .filename(filename)
                .jobType(JobType.EOM)
                .build();

        statementServiceMock = mock(StatementService.class);
        freeMarkConfigurationMock = mock(Configuration.class);
        emailPropertiesMock = mock(EmailProperties.class);
        //needs re testing
        eomCustomerInfoServiceMock = Mockito.mock(EOMCustomerInfoServiceImpl.class);
        adhocCustomerInfoServiceMock = Mockito.mock(AdhocCustomerInfoServiceImpl.class);
        mailServiceMock = Mockito.mock(MailService.class);
        itemMailSender = new StatementMailSenderItemWriter(statementServiceMock, adhocCustomerInfoServiceMock, eomCustomerInfoServiceMock, mailServiceMock,
                freeMarkConfigurationMock, emailPropertiesMock);

    }

    @Test
    public void write_whenClientIdExist_And_EmailIsInValid_MailServiceIsNeverInvoked() throws Exception {
        //expecting saved statement when all
        CustomerInfo customerInfoWithInvalidEmail = CustomerInfo.builder()
                .cifId("013368406")
                .email("mash")
                .build();

        when(eomCustomerInfoServiceMock.getCustomerInfoByCif("013368406")).thenReturn(customerInfoWithInvalidEmail);
        itemMailSender.write(Arrays.asList(statement));

        //when email is invalid mailService sendMailStatementToCustomer method  never invoked
        Mockito.verify(mailServiceMock, times(0)).sendMailStatementToCustomer(null, null);

    }

    @Test
    public void write_whenClientIdExist_And_EmailIsValid_MailServiceIsInvoked() throws Exception {
        CustomerInfo customerInfoWithValidEmail = CustomerInfo.builder()
                .cifId("013368406")
                .email("mash@mashreq.com")
                .businessGroupType(BusinessGroupType.RBG)
                .build();

        when(eomCustomerInfoServiceMock.getCustomerInfoByCif("013368406")).thenReturn(customerInfoWithValidEmail);

        itemMailSender.write(Arrays.asList(statement));


        //Expected mail properties
        Mail expectedMail = new Mail();
        expectedMail.setMailTo("mash@mashreq.com");
        expectedMail.setAttachmentFile(file.toFile());
        expectedMail.setCustomerInfo(customerInfoWithValidEmail);
        Mockito.verify(mailServiceMock).sendMailStatementToCustomer(Mockito.eq(expectedMail), eq(null));
        //mailService is invoked only Once
        Mockito.verify(mailServiceMock, times(1)).sendMailStatementToCustomer(Mockito.eq(expectedMail), nullable(Template.class));
    }


    @Test
    public void write_whenClientIdNotExist_DisplayCustomerNotFoundException() throws Exception {

        CustomerInfo customerInfoWithInvalidEmail = CustomerInfo.builder()
                .cifId("013368406")
                .email("mash@mashreq.com")
                .build();

        when(eomCustomerInfoServiceMock.getCustomerInfoByCif("145")).thenReturn(null);

        itemMailSender.write(Arrays.asList(statement));

        //when clientId does not exist mailService sendMailStatementToCustomer method is never invoked
        Mockito.verify(mailServiceMock, times(0)).sendMailStatementToCustomer(null, null);

    }

}