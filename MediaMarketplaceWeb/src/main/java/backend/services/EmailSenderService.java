package backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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
	    context.setVariable("expirationText", expirationText);

	    String htmlContent = templateEngine.process("reset_password_template", context);

	    helper.setTo(toEmail);
	    helper.setSubject("MediaMarketplace: Reset Your Password");
	    helper.setText(htmlContent, true);

	    mailSender.send(message);
	}
	
    public void sendRegistrationConfirmationEmail(String toEmail, String confirmLink, String expirationText) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();
        context.setVariable("confirmLink", confirmLink);
        context.setVariable("expirationText", expirationText);

        String htmlContent = templateEngine.process("confirm_email_template", context);

        helper.setTo(toEmail);
        helper.setSubject("MediaMarketplace: Confirm your registration");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
