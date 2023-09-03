package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	@Autowired
	private JavaMailSender mailSender;
	
	public void sendEamil(String toEmail,String subject,String body)
	{
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("ashickamubbarak@gamil.com");
		message.setTo(toEmail);
		message.setText(body);
		//message.setTo(body1);
		message.setSubject(subject);
		
		mailSender.send(message);
		
		
System.out.println("mailSender");	}

	

}
