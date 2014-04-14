package elaborate.util;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2014 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


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
