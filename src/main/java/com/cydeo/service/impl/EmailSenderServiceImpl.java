package com.cydeo.service.impl;

import com.cydeo.EmailContext;
import com.cydeo.service.EmailSenderService;
import com.cydeo.service.SecurityService;
import com.sun.mail.smtp.SMTPTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


@Service
public class EmailSenderServiceImpl implements EmailSenderService {
private final SecurityService securityService;


    @Autowired
    private JavaMailSender mailSender;

    public EmailSenderServiceImpl(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Scope("prototype") //does it work? How can I check it?
    public void sendEmail(String subject, String body) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        String toEmail= securityService.getLoggedInUser().getUsername();
//        SimpleMailMessage sms = new SimpleMailMessage();
//        String phone = securityService.getLoggedInUser().getPhone();
//        String emailToSmsVerizon=phone + "@vtext.com";
//        String emailToSmsTT = phone + "number@txt.att.net"
//        sms.setFrom("javadevelopertest2000@googlemail.com");
//        sms.setTo(emailToSmsVerizon);
//        sms.setText(body);
//        sms.setSubject(subject);
//        mailSender.send(sms
        MimeMessageHelper helper1 = new MimeMessageHelper(message,true);
        helper1.setFrom("javadevelopertest2000@googlemail.com");
        //helper1.setTo("marklen86@gmail.com");
        helper1.setTo(toEmail);
        helper1.setSubject(subject);
        helper1.setText(body,true);
        mailSender.send(message);
        message.getSession().getTransport().close();
    }

    @Override
    public void sendEmailAttach(String subject, String text,String pathToAttachment) throws MessagingException, IOException {
        MimeMessage message2 = mailSender.createMimeMessage();
        message2.setContent("invoice_print.html","text/html");
        MimeMessageHelper helper = new MimeMessageHelper(message2,true);
        helper.setFrom("javadevelopertest2000@googlemail.com");
        String toEmail= securityService.getLoggedInUser().getUsername();
        //helper.setTo("marklen86@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(text,true);
        FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
        helper.addAttachment("Invoice", file, "text/html" );
        mailSender.send(message2);
        message2.getSession().getTransport().close();

    }
}










