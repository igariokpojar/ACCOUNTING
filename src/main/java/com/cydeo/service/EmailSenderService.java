package com.cydeo.service;

import com.cydeo.EmailContext;

import javax.mail.MessagingException;
import javax.swing.text.html.HTMLDocument;
import java.io.File;
import java.io.IOException;

public interface EmailSenderService {

    void sendEmail(String subject, String body) throws MessagingException;
    void sendEmailAttach(String subject, String text, String pathToAttachment)throws MessagingException, IOException;


}
