package backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import backend.ConfigValues;
import backend.utils.UrlUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSenderService {
	
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

	public void sendResetPasswordEmail(String toEmail, String resetLink, String expirationText) throws MessagingException {
	    MimeMessage message = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	    Context context = new Context();
	    context.setVariable("resetLink", resetLink);
	    context.setVariable("expirationText", expirationText); // 👈 dynamic text like "30 seconds"

	    String htmlContent = templateEngine.process("reset_password_template", context);

	    helper.setTo(toEmail);
	    helper.setSubject("MediaMarketplace: Reset Your Password");
	    helper.setText(htmlContent, true);

	    mailSender.send(message);
	}
}
