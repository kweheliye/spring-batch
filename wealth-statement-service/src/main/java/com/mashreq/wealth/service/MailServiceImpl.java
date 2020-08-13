package com.mashreq.wealth.service;


import com.mashreq.wealth.model.Mail;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    private JavaMailSender sender;

    @Autowired
    public MailServiceImpl(JavaMailSender sender) {
        this.sender = sender;
    }

    @Override
    @Async("emailTaskExecutor")
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public Boolean sendMailStatementToCustomer(Mail mail, Template template) throws MessagingException, IOException, TemplateException {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            // Process the template
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, mail);
            helper.setFrom(mail.getMailFrom());
            helper.setTo(mail.getMailTo());
            helper.setText(text, true);
            helper.setSubject(mail.getMailSubject());
            helper.addAttachment(mail.getAttachmentName(), mail.getAttachmentFile());
            sender.send(message);
            return true;
        } catch (Exception e) {
            log.error("Error in sending email for clientId: {}, exception message:{}", mail.getCustomerInfo().getCifId(), e.getMessage());
            log.error("Retry Count= {}", RetrySynchronizationManager.getContext().getRetryCount());
            throw e;
        }
    }
}
