/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
/* 
 * Created on 27.06.2012
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.mailconnector.smtp;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import eu.tsystems.mms.tic.testframework.mailconnector.util.AbstractMailConnector;
import eu.tsystems.mms.tic.testframework.mailconnector.util.MessageUtils;
import net.iharder.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * MailConnector using the SMTP Protocol. Creates a session with values from mailconnection.properties.
 * 
 * @author pele, mrgi
 */
public class SMTPMailConnector extends AbstractMailConnector {

    /** The Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SMTPMailConnector.class);

    /** messageID from the current sent message. */
    private String messageID;
    /** the last sent message. */
    private MimeMessage lastSentMessage;

    /** Constructor, creates a SMTPMailConnector Object. */
    public SMTPMailConnector() {
        this.init();
    }

    /**
     * Called from constructor. Initializes the ImapMailConnector.
     */
    private void init() {
        PropertyManager.loadProperties("mailconnection.properties");

        setUsername(PropertyManager.getProperty("SMTP_USERNAME", null));
        setPassword(PropertyManager.getProperty("SMTP_PASSWORD", null));
        setServer(PropertyManager.getProperty("SMTP_SERVER", null));
        setPort(PropertyManager.getProperty("SMTP_SERVER_PORT", null));

        setDebug(PropertyManager.getBooleanProperty("DEBUG_SETTING", false));
        setSslEnabled(PropertyManager.getBooleanProperty("SMTP_SSL_ENABLED", false));
    }

    /**
     * Open a new SMTP Session and save in session object.
     */
    @Override
    protected void openSession() {
        final Properties mailprops = new Properties();
        LOGGER.info("Setting host: " + getServer());
        LOGGER.info("Setting port: " + getPort());
        LOGGER.info("Setting SSL enabled: " + isSslEnabled());

        if (isSslEnabled()) {
            mailprops.put("mail.transport.protocol", "smtps");
            mailprops.put("mail.smtps.host", getServer());
            mailprops.put("mail.smtps.port", getPort());
            mailprops.put("mail.smtp.ssl.enable", true);
            mailprops.put("mail.smtps.user", getUsername());
            mailprops.put("mail.smtps.password", getPassword());
        } else {
            mailprops.put("mail.transport.protocol", "smtp");
            mailprops.put("mail.smtp.host", getServer());
            mailprops.put("mail.smtp.port", getPort());
            mailprops.put("mail.smtp.user", getUsername());
            mailprops.put("mail.smtp.password", getPassword());
        }

        mailprops.put("mail.smtp.auth", "true");
        mailprops.put("mail.debug", isDebug());
        mailprops.put("mail.smtp.socketFactory.port", getPort());
        mailprops.put("mail.smtp.socketFactory.class",
                "eu.tsystems.mms.tic.testframework.mailconnector.FennecSSLSocketFactory");

        LOGGER.info("building session");
        setSession(Session.getInstance(mailprops,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(getUsername(), getPassword());
                    }
                }

                ));
        getSession().setDebug(isDebug());
        LOGGER.info("Done.");
    }

    /**
     * Send a new message.
     * 
     * @param message The message to send.
     * @throws FennecSystemException thrown if message was not sent.
     * 
     */
    public void sendMessage(final MimeMessage message) throws FennecSystemException {
        this.pSendMessage(message);
    }

    /**
     * Send a new message.
     * 
     * @param message The message to send.
     * @throws FennecSystemException thrown if message was not sent.
     * 
     */
    private void pSendMessage(final MimeMessage message) throws FennecSystemException {
        Transport transport = null;
        try {
            transport = getSession().getTransport();
            MessageUtils.multiPartBugfix(message);

            // send
            transport.connect();

            // save message id
            if (message.getMessageID() != null) {
                this.messageID = message.getMessageID();
            }

            transport.sendMessage(message, message.getAllRecipients());
            LOGGER.info("Message sent! ");
            transport.close();
            this.lastSentMessage = message;
        } catch (final NoSuchProviderException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (final MessagingException e) {
            throw new FennecSystemException("Email NOT sent! " + e.getMessage(), e);
        }
    }

    /**
     * Creates a MimeBodyPart attachment from file.
     * 
     * @param file The file to convert to MimeBodyPart.
     * @return MimeBodyPart.
     */
    public MimeBodyPart createAttachment(final File file) {
        return this.pCreateAttachment(file);
    }

    /**
     * Creates a MimeBodyPart attachment from file.
     * 
     * @param file The file to convert to MimeBodyPart.
     * @return MimeBodyPart.
     */
    private MimeBodyPart pCreateAttachment(final File file) {
        final MimeBodyPart attachment = new MimeBodyPart();
        try {
            attachment.setDataHandler(new DataHandler(new FileDataSource(file)));
            attachment.setFileName(file.getName());
            attachment.setDisposition(MimeBodyPart.ATTACHMENT);
        } catch (final MessagingException e) {
            LOGGER.error(e.getMessage());
        }

        return attachment;
    }

    /**
     * Add MimeBodyParts to a message. Can only called once, otherwise message text can not saved.
     * 
     * @param attachments An array containing the MimeBodyParts.
     * @param message The message to add the attachments.
     * @return The message with the attached MimeBodyParts.
     */
    public MimeMessage addAttachmentsToMessage(final MimeBodyPart[] attachments, final Message message) {
        return this.pAddAttachmentsToMessage(attachments, message);
    }

    /**
     * Add MimeBodyParts to a message. Can only called once, otherwise message text can not saved.
     * 
     * @param attachments An array containing the MimeBodyParts.
     * @param message The message to add the attachments.
     * @return The message with the attached MimeBodyParts.
     */
    private MimeMessage pAddAttachmentsToMessage(final MimeBodyPart[] attachments, final Message message) {
        try {
            final MimeMultipart mimeMultipart = new MimeMultipart();
            final MimeBodyPart text = new MimeBodyPart();

            text.setText(message.getContent().toString());
            text.setDisposition(MimeBodyPart.INLINE);
            mimeMultipart.addBodyPart(text);
            for (final MimeBodyPart attachment : attachments) {
                mimeMultipart.addBodyPart(attachment);
            }

            message.setContent(mimeMultipart);
            message.saveChanges();
        } catch (final MessagingException e) {
            LOGGER.error(e.getLocalizedMessage());
        } catch (final IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }

        return (MimeMessage) message;
    }

    /**
     * Send a virus mail.
     * 
     * @param from The from address.
     * @param receiver The to address.
     * @param ccReceiver The cc address. Can be null.
     * @param bcc The bcc address. Can be null.
     * 
     * @return A MimeMessage containing a virus signature.
     * 
     * @throws FennecSystemException thrown if virus Mail can't generated.
     * @throws FennecRuntimeException thrown if address parameters were wrong.
     */
    public MimeMessage generateVirusMail(final String from, final String receiver,
            final String ccReceiver, final String bcc) throws FennecSystemException, FennecRuntimeException {
        return this.pGenerateVirusMail(from, receiver, ccReceiver, bcc);
    }

    /**
     * Send a virus mail.
     * 
     * @param from The from address.
     * @param receiver The to address.
     * @param ccReceiver The cc address. Can be null.
     * @param bcc The bcc address. Can be null.
     * 
     * @return A MimeMessage containing a virus signature.
     * 
     * @throws FennecSystemException thrown if virus Mail can't generated.
     * @throws FennecRuntimeException thrown if address parameters were wrong.
     */
    private MimeMessage pGenerateVirusMail(final String from, final String receiver,
            final String ccReceiver, final String bcc) throws FennecSystemException, FennecRuntimeException {
        final MimeMessage message = new MimeMessage(getSession());
        try {

            message.setFrom(new InternetAddress(from));
            message.setRecipient(RecipientType.TO, new InternetAddress(receiver));

            if (ccReceiver != null) {
                message.setRecipient(RecipientType.CC, new InternetAddress(ccReceiver));
            }

            if (bcc != null) {
                message.setRecipient(RecipientType.BCC, new InternetAddress(bcc));
            }

            final String virusPattern = "X5O!P%@AP[4\\PZX54" + "(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*";
            message.setSubject("Antivirus-Test");

            final MimeMultipart multiPart = new MimeMultipart();

            final MimeBodyPart bp1 = new MimeBodyPart();

            bp1.setContent(String.class, "text/plain");

            multiPart.addBodyPart(bp1);

            final byte[] encoded = Base64.encodeBytes(virusPattern.getBytes()).getBytes();
            final MimeBodyPart bp2 = new MimeBodyPart();
            bp2.setFileName("virus.exe");
            bp2.setContent(encoded, "application/octet-stream");
            bp2.setHeader("Content-Transfer-Encoding", "base64");

            multiPart.addBodyPart(bp2);

            message.setContent(multiPart);
            message.saveChanges();
        } catch (final AddressException aex) {
            throw new FennecRuntimeException("Some of the address parameters were wrong.", aex);
        } catch (final MessagingException e) {
            throw new FennecSystemException(e);
        }
        return message;
    }

    /**
     * .
     * 
     * @return .
     */
    public String getMessageID() {
        return messageID;
    }

    /**
     * .
     * 
     * @return last sent message
     */
    public MimeMessage getLastSentMessage() {
        return lastSentMessage;
    }

    /**
     * sets the messageID
     *
     * @param messageID the messageID to set
     */
    public void setMessageID(final String messageID) {
        this.messageID = messageID;
    }

    /**
     * sets the last sent message
     *
     * @param lastSentMessage the lastSentMessage to set
     */
    public void setLastSentMessage(final MimeMessage lastSentMessage) {
        this.lastSentMessage = lastSentMessage;
    }
}
