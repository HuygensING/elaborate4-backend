package elaborate.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Emailer {

	private final String mailhost;

	public Emailer(final String _mailhost) {
		this.mailhost = _mailhost;
	}

	public void sendMail(final String from_email, final String from_name, final String to_email, final String subject, final String body)/*(String from, String to, String subject, String body)*/throws MessagingException {
		Properties props = System.getProperties();
		if (props.get("mail.smtp.host") == null) {
			props.put("mail.smtp.host", this.mailhost);
		}
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from_email));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to_email));
		message.setText(body, "utf-8", "html");
		message.setSubject(subject);
		Transport.send(message);
	}

}
