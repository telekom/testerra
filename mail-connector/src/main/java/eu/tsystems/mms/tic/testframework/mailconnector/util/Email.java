/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package eu.tsystems.mms.tic.testframework.mailconnector.util;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * E-mail object that reads the content of the javax.mail.Message object
 *
 * @author sepr, clgr
 */
public class Email implements Loggable {

    private final List<EmailAttachment> attachments = new ArrayList<>();

    private String messageText;

    private final MimeMessage message;

    private final List<String> recipientList = new LinkedList<String>();

    private final List<String> senderList = new LinkedList<String>();

    private String subject = null;

    private String messageID;

    private Date sentDate;

    public MimeMessage getMessage() {
        return this.message;
    }

    /**
     * Converts an Address-Array into a list of recipients
     *
     * @param allRecipients Array containing all recipient addresses
     */
    private void setRecipientsFromAddressArray(final Address[] allRecipients) {
        if (allRecipients != null) {
            for (Address address : allRecipients) {
                recipientList.add(address.toString());
            }
        }
    }

    /**
     * Converts an Address-Array into a list of senders
     *
     * @param allSenders Array containing all sender addresses
     */
    private void setSendersFromAddressArray(final Address[] allSenders) {
        if (allSenders != null) {
            for (Address address : allSenders) {
                senderList.add(address.toString());
            }
        }
    }

    private void setSubject(final String subject) {
        this.subject = subject;
    }

    public List<String> getRecipients() {
        return this.recipientList;
    }

    public List<String> getSenders() {
        return this.senderList;
    }

    public String getSubject() {
        return this.subject;
    }

    /**
     * Constructor, converts a Message object to an FTMessage
     *
     * @param javaMessage Message object to be converted
     */
    public Email(final MimeMessage javaMessage) {
        this.message = javaMessage;

        readMessageContents();

        try {
            this.setSubject(javaMessage.getSubject());
        } catch (MessagingException e) {
            this.setSubject("");
        }
        try {
            this.setMessageID(javaMessage.getMessageID());
        } catch (MessagingException e) {
            this.setMessageID(null);
        }
        try {
            this.setSentDate(javaMessage.getSentDate());
        } catch (MessagingException e) {
            this.setSentDate(null);
        }
        try {
            Address[] allRecipients = javaMessage.getAllRecipients();
            this.setRecipientsFromAddressArray(allRecipients);
        } catch (MessagingException e) {
            this.setRecipientsFromAddressArray(null);
        }
        try {
            Address[] allSenders = javaMessage.getFrom();
            this.setSendersFromAddressArray(allSenders);
        } catch (MessagingException e) {
            this.setSendersFromAddressArray(null);
        }
    }

    /**
     * Saves message text and attachments in local fields
     */
    private void readMessageContents() {
        InputStream is;
        String encoding;
        try {
            // Check if multipart mail or not
            if (message.getContentType().startsWith("multipart")) {
                Multipart content = (Multipart) message.getContent();

                for (int j = 0; j < content.getCount(); j++) {
                    BodyPart part = content.getBodyPart(j);
                    is = part.getInputStream();
                    encoding = part.getContentType();
                    encoding = getCharSetForEncoding(encoding);

                    if (j == 0) {
                        // Message content
                        try {
                            messageText = IOUtils.toString(is, encoding).replaceAll("\r", "");
                        } catch (IllegalCharsetNameException e) {
                            log().error("Unable to encode input stream", e);
                        }
                    } else {
                        // Attachment
                        String fileName = part.getFileName();
                        EmailAttachment attachment = new EmailAttachment(fileName, is, encoding);
                        attachments.add(attachment);
                    }

                } // end for
            } else {
                is = message.getInputStream();
                encoding = message.getContentType();
                encoding = getCharSetForEncoding(encoding);

                messageText = IOUtils.toString(is, encoding).replaceAll("\r", "");
            }
        } catch (MessagingException | IOException ex) {
            log().error("Unable to read email details", ex);
        }
    }

    /**
     * Get charset for encoding from Mail, use UTF-8 as default and
     * ISO_8859_1 for binary files
     *
     * @param encoding the ContentType of the Part
     * @return encoding for the given ContentType
     */
    private String getCharSetForEncoding(String encoding) {
        final int indexOfEncoding = encoding.lastIndexOf("charset=");

        if (indexOfEncoding > -1) {
            // Extracting the encoding from Content Type
            // CSOFF: MagicNumber
            encoding = encoding.substring(indexOfEncoding + 8);
            // CSON: MagicNumber
            final int index = encoding.indexOf(';');

            if (index > 0) {
                encoding = encoding.substring(0, encoding.indexOf(';'));
            }
            // Remove quotes from encoding string
            encoding = encoding.replace("\"", "");
        } else {
            if (encoding.startsWith("image/") || encoding.startsWith("application/octet-stream")) {
                encoding = StandardCharsets.ISO_8859_1.name();
            } else {
                log().warn("No encoding found in email. Using '" + StandardCharsets.UTF_8.name() + "' instead");
                encoding = StandardCharsets.UTF_8.name();
            }
        }

        return encoding;
    }

    public List<EmailAttachment> getAttachments() {
        return attachments;
    }

    /**
     * Gets the content of the given attachment
     *
     * @param fileName name of the attachment file
     * @return attachment
     */
    public EmailAttachment getAttachment(String fileName) throws IOException {
        for (EmailAttachment attachment : attachments) {
            if (attachment.getFileName().equals(fileName)) {
                return attachment;
            }
        }
        return null;
    }

    public String getMessageText() {
        return messageText;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
}
