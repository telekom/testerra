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
 *     Peter Lehmann
 *     pele
 */

package eu.tsystems.mms.tic.testframework.mailconnector.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.IllegalCharsetNameException;
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
public class Email {

    /**
     * Logger.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(Email.class);

    /**
     * Liste von Anhängen der Mail.
     */
    private final Map<String, String> attachments;

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
     * Speichert Textinhalt und Anhänge in den lokalen Feldern.
     */
    private void readMessageContents() {
        InputStream is;
        String encoding;
        try {
            // unterscheiden zwischen Multipart-Mail oder nicht
            if (message.getContentType().startsWith("multipart")) {
                Multipart multipart = (Multipart) message.getContent();

                for (int j = 0; j < multipart.getCount(); j++) {
                    is = ((Multipart) message.getContent()).getBodyPart(j).getInputStream();
                    encoding = ((Multipart) message.getContent()).getBodyPart(j).getContentType();

                    encoding = getCharSetForEncoding(encoding);

                    String mail;
                    String attachmentName;
                    try {
                        mail = IOUtils.toString(is, encoding).replaceAll("\r", "");
                    } catch (IllegalCharsetNameException e) {
                        LOGGER.info(
                                "Der Inhalt des Anhangs konnte nicht ermittelt werden, "
                                        + "es wird der Name zur\u00fcckgegeben!");
                        mail = null;
                    }
                    attachmentName = ((Multipart) message.getContent()).getBodyPart(j).getFileName();

                    if (j == 0) {
                        messageText = mail;
                    } else {
                        attachments.put(attachmentName, mail);
                    }
                } // end for
            } else {
                is = message.getInputStream();
                encoding = message.getContentType();
                encoding = getCharSetForEncoding(encoding);

                messageText = IOUtils.toString(is, encoding).replaceAll("\r", "");
            }
        } catch (MessagingException | IOException ex) {
            LOGGER.error("error reading details from mail", ex);
        }
    }

    /**
     * get charset for encoding from Mail, use UTF-8 as default
     *
     * @param encoding
     *
     * @return
     */
    private String getCharSetForEncoding(String encoding) {
        final int indexOfEncoding = encoding.lastIndexOf("charset=");

        if (indexOfEncoding > -1) {
            // das eigentliche encoding aus dem Content Type extrahieren
            // CSOFF: MagicNumber
            encoding = encoding.substring(indexOfEncoding + 8);
            // CSON: MagicNumber
            final int index = encoding.indexOf(';');

            if (index > 0) {
                encoding = encoding.substring(0, encoding.indexOf(';'));
            }
        } else {
            LOGGER.warn("No Encoding found in Mail. Using 'UTF-8' instead.");
            encoding = "UTF-8";
        }

        return encoding;
    }

    /**
     * gets the given attachments
     *
     * @return the attachments
     */
    public Map<String, String> getAttachments() {
        return attachments;
    }

    /**
     * Gets the content of the given attachment
     *
     * @param fileName Name of attachment
     *
     * @return content of attachment
     */
    public String getAttachmentsContent(String fileName) {
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
