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
 * Created on 24.07.2014
 * 
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
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
import java.util.*;

/**
 * E-Mail Objekt, das alle Inhalte eines javax.mail.Message-Objekts ausliest.
 * 
 * @author sepr
 */
public class TesterraMail {

    /** Logger. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(TesterraMail.class);

    /** Liste von Anhängen der Mail. */
    private final Map<String, String> attachments;

    /** Textinhalt der Mail. */
    private String messageText;

    /** Message-Object */
    private final MimeMessage message;

    /** Liste der Empfänger (CC,BCC,TO) */
    private final List<String> recipientList = new LinkedList<String>();

    /** Liste der Absender */
    private final List<String> senderList = new LinkedList<String>();

    /** Betreff der Nachricht */
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
     * 
     * */
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
    public TesterraMail(final MimeMessage javaMessage) {
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

                    // das eigentliche encoding aus dem Content Type extrahieren
                    // CSOFF: MagicNumber
                    encoding = encoding.substring(encoding.lastIndexOf("charset=") + 8);

                    // CSON: MagicNumber
                    int index = encoding.indexOf(';');

                    if (index > 0) {
                        encoding = encoding.substring(0, encoding.indexOf(';'));
                    }

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
                // CSOFF: MagicNumber
                encoding = encoding.substring(encoding.lastIndexOf("charset=") + 8);

                // CSON: MagicNumber
                int index = encoding.indexOf(';');

                if (index > 0) {
                    encoding = encoding.substring(0, encoding.indexOf(';'));
                }

                messageText = IOUtils.toString(is, encoding).replaceAll("\r", "");
            }
        } catch (MessagingException ex) {
            LOGGER.error("error reading details from mail", ex);
        } catch (IOException e) {
            LOGGER.error("error reading details from mail", e);
        }
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
     * @return content of attachment
     * 
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
