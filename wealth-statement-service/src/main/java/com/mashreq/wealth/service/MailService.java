package com.mashreq.wealth.service;

import com.mashreq.wealth.model.Mail;
import freemarker.template.Template;

import java.util.concurrent.Future;

public interface MailService {
      //send mail statement to one customer
      Boolean sendMailStatementToCustomer(Mail mail, Template template) throws Exception;
}
