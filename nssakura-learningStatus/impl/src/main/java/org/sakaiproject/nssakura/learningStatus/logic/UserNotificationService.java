package org.sakaiproject.nssakura.learningStatus.logic;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.email.api.AddressValidationException;
import org.sakaiproject.email.api.ContentType;
import org.sakaiproject.email.api.EmailAddress;
import org.sakaiproject.email.api.EmailAddress.RecipientType;
import org.sakaiproject.email.api.EmailMessage;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.email.api.NoRecipientsException;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.nssakura.learningStatus.model.MessageDaoModel;
import org.sakaiproject.user.api.User;

public class UserNotificationService {
	
	private final String EVENT_EMAIL_SEND = "nssakura-learningStatus.email.send";
	
	public class RunSendToUsers implements Runnable {
	    List<EmailMessage> msgs;
	    long sleeptime;

	    public RunSendToUsers(List<EmailMessage> msgs, long sleeptime) {
	    	this.msgs = msgs;
	    	this.sleeptime = sleeptime;
	    }

	    public void run() {
	        EmailService emailService = (EmailService) ComponentManager.get(EmailService.class);
	        EventTrackingService eventService = (EventTrackingService) ComponentManager.get(EventTrackingService.class);
	        if (emailService == null) {
	            throw new RuntimeException("Unable to get EmailService to send emails");
	        }
	        for(EmailMessage msg:msgs){
	        	boolean sleepFlg = true;
				try{
					List<EmailAddress> invalids = emailService.send(msg,true);
					List<String> rets = EmailAddress.toStringList(invalids);
					Event event = eventService.newEvent(EVENT_EMAIL_SEND,
							null, false);
					eventService.post(event);
				}catch (AddressValidationException e){
					e.printStackTrace();
				}catch (NoRecipientsException e){
					e.printStackTrace();
					sleepFlg = false;
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				if(sleepFlg){
					try {
						Thread.sleep(sleeptime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
	        }
	    }
	}
	
	public void NotifyNewmessages(List<User> recipients, String replyToEmail, String replyToName, MessageDaoModel model, int max, int sleeptime){
		int num = recipients.size();
		int maxcount = num/max + 1;
		List<EmailMessage> msgs = new ArrayList<EmailMessage>();
		for(int n=0; n< maxcount; n++){
			EmailMessage msg = new EmailMessage();
			msg.setFrom(new EmailAddress(replyToEmail, replyToName));
			msg.setSubject(model.getSubject());
			msg.setContentType(ContentType.TEXT_HTML);
			msg.setBody(model.getContent());
			ArrayList<EmailAddress> tos = new ArrayList<EmailAddress>();
			for(int i=0; i<max; i++){
				int no = n*max + i;
				if(no < recipients.size()){
					User recipient = recipients.get(no);
					tos.add(new EmailAddress(recipient.getEmail(),recipient.getDisplayName()));
				}else{
					break;
				}
			}
			if(!tos.isEmpty()){
				msg.addRecipients(RecipientType.BCC, tos);
				msg.addHeader("X-Mailer", "sakai-mailsender");
				msg.addHeader("Content-Transfer-Encoding", "quoted-printable");
				msgs.add(msg);
			}
		}
		(new Thread(new RunSendToUsers(msgs, sleeptime))).start();
	}
	

}
