package com.smart.service;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	public boolean sendEmail(String subject, String message, String to) {
		boolean flag = false;
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true); // Fix typo here
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.host", "smtp.gmail.com");

		String from = "devadhavalsonde20@gmail.com";
		String password = "vdms onzd rmtr fbqw";

		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		});

		try {
			MimeMessage m = new MimeMessage(session);

			m.setFrom(from);
			m.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
			m.setSubject(subject);
			m.setContent(message,"text/html");

			Transport.send(m);

			System.out.println("email Send.......");

			flag = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}
}
