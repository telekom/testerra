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
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * E-Mail Objekt, das alle Inhalte eines javax.mail.Message-Objekts ausliest.
 *
 * @author sepr, clgr
 */
public class Email implements Loggable {
    /**
     * Liste von Anhängen der Mail.
     */
    private final Map<String, InputStream> attachments;

    /**
     * Textinhalt der Mail.
     */
    private String messageText;

    /**
     * Message-Object
     */
    private final MimeMessage message;

    /**
     * Liste der Empfänger (CC,BCC,TO)
     */
    private final List<String> recipientList = new LinkedList<String>();

    /**
     * Liste der Absender
     */
    private final List<String> senderList = new LinkedList<String>();

    /**
     * Betreff der Nachricht
     */
    private String subject = null;

    private String messageID;

    private Date sentDate;

    /**
     * Methode liefert die enthaltene Message Instanz
     *
     * @return message
     */
    public MimeMessage getMessage() {
        return this.message;
    }

    /**
     * Methode zur Umwandlung eines Address-Arrays in Empfängerliste
     *
     * @param allRecipients Array, mit zu wandelnden Empfänger-Adressen
     */
    private void setRecipientsFromAddressArray(final Address[] allRecipients) {
        if (allRecipients != null) {
            for (Address address : allRecipients) {
                recipientList.add(address.toString());
            }
        }
    }

    /**
     * Methode zur Umwandlung eines Address-Array in Absenderliste
     *
     * @param allSenders Array, mit zu wandelnden Absendern-Adressen
     */
    private void setSendersFromAddressArray(final Address[] allSenders) {
        if (allSenders != null) {
            for (Address address : allSenders) {
                senderList.add(address.toString());
            }
        }
    }

    /**
     * Methode zum Setzen des Betreffes
     *
     * @param subject String E-Mail-Betreff
     */
    private void setSubject(final String subject) {
        this.subject = subject;
    }

    /**
     * Methode liefert ein Array aller Empfänger
     *
     * @return Address[], Liste aller Empfänger
     */
    public List<String> getRecipients() {
        return this.recipientList;
    }

    /**
     * Methode liefert ein Array aller Absender
     *
     * @return Address[], Liste aller Absender
     */
    public List<String> getSenders() {
        return this.senderList;
    }

    /**
     * Methode liefert den Betreff
     *
     * @return String, E-Mail-Betreff
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * Konstruktor, der Message-Objekt in FTMessage wandelt.
     *
     * @param javaMessage zu wandelndes Message-Objekt.
     */
    public Email(final MimeMessage javaMessage) {
        attachments = new HashMap<>();
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
                    Part part = content.getBodyPart(j);
                    is = part.getInputStream();

                    if (part.getDisposition().equals(Part.INLINE)) {
                        encoding = part.getContentType();
                        encoding = getCharSetForEncoding(encoding);
                        try {
                            messageText = IOUtils.toString(is, encoding).replaceAll("\r", "");
                        } catch (IllegalCharsetNameException e) {
                            log().error("Unable to encode input stream", e);
                        }
                    } else if (part.getDisposition().equals(Part.ATTACHMENT)) {
                        String fileName = part.getFileName();
                        attachments.put(fileName, is);
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
     * Get charset for encoding from Mail, use UTF-8 as default
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

    /**
     * Saves the given attachment
     *
     * @param fileName Name of attachment
     */
    public void saveAttachment(String fileName) throws IOException {
        File file = new File(fileName);
        // Reset the stream to the correct position to ensure the file is created properly
        getAttachment(fileName).reset();
        FileUtils.copyInputStreamToFile(this.getAttachment(fileName), file);
    }

    /**
     * gets the given attachments
     *
     * @return the attachments
     */
    public Map<String, InputStream> getAttachments() {
        return attachments;
    }

    /**
     * Gets the content of the given attachment
     *
     * @param fileName Name of attachment
     * @return attachment
     */
    public InputStream getAttachment(String fileName) throws IOException {
        return attachments.get(fileName);
    }

    /**
     * Gibt Textinhalt der E-Mail zurück.
     *
     * @return Textinhalt der E-Mail als String
     */
    public String getMessageText() {
        return messageText;
    }

    /**
     * @return the sentDate
     */
    public Date getSentDate() {
        return sentDate;
    }

    /**
     * @param sentDate the sentDate to set
     */
    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    /**
     * @return the messageID
     */
    public String getMessageID() {
        return messageID;
    }

    /**
     * @param messageID the messageID to set
     */
    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
}
