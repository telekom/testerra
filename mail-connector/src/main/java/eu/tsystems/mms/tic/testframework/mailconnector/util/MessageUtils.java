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
 * Created on 17.09.2012
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.mailconnector.util;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

/**
 * Class containing utility functions for MimeMessage handling.
 * 
 * @author sepr
 * 
 */
public final class MessageUtils {

    /** The Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageUtils.class);

    /**
     * Hide constructor of utility class.
     */
    private MessageUtils() {
    }

    /**
     * Get Email headers (as String[]) from a mime message.
     * 
     * @param message The message to get the headers.
     * @return A string array containing the headers.
     * @throws TesterraSystemException thrown if email headers can't read.
     */
    public static String[] getEmailHeaders(final MimeMessage message) throws TesterraSystemException {
        return pGetEmailHeaders(message);
    }

    /**
     * Get Email headers (as String[]) from a mime message.
     * 
     * @param message The message to get the headers.
     * @return A string array containing the headers.
     * @throws TesterraSystemException thrown if email headers can't read.
     */
    private static String[] pGetEmailHeaders(final MimeMessage message) throws TesterraSystemException {
        ArrayList<String> headersAsStrings;
        try {
            multiPartBugfix(message);
            final Enumeration<?> headers = message.getAllHeaderLines();
            headersAsStrings = new ArrayList<String>();
            while (headers.hasMoreElements()) {
                headersAsStrings.add((String) headers.nextElement());
            }
        } catch (final Exception e) {
            throw new TesterraSystemException(e);
        }
        return headersAsStrings.toArray(new String[headersAsStrings.size()]);
    }

    /**
     * Bugfix for MultiPart messages. Any method must called.
     * 
     * @param message The message to fix.
     */
    public static void multiPartBugfix(final Message message) {
        pMultiPartBugfix(message);
    }

    /**
     * Bugfix for MultiPart messages. Any method must called.
     * 
     * @param message The message to fix.
     */
    private static void pMultiPartBugfix(final Message message) {
        try {
            final Object content = message.getContent();
            if (content instanceof Multipart) {
                LOGGER.debug("_MultiPartBugfix: mp getCount:"
                        + ((Multipart) content).getCount());
            }
        } catch (final Exception e) {
            LOGGER.warn("_MultiPartBugfix: " + e);
        }
    }

    /**
     * Messages filter. Filter criteria are: subject and from
     * 
     * @param map The treemap containing subjects or from values. e.g. TreeMap<"subject", "Our Meeting"> or
     *            TreeMap<"from","info@example.org">
     * @param messages An array containing all the messages to search.
     * 
     * @return An array containing only the filtered messages.
     * @throws TesterraSystemException thrown if messages can't be filtered.
     */
    public static Message[] messagesFilter(final Map<String, String> map,
            final Message[] messages) throws TesterraSystemException {
        return pMessagesFilter(map, messages);
    }

    /**
     * Messages filter. Filter criteria are: subject and from
     * 
     * @param map The treemap containing subjects or from values. e.g. TreeMap<"subject", "Our Meeting"> or
     *            TreeMap<"from","info@example.org">
     * @param messages An array containing all the messages to search.
     * 
     * @return An array containing only the filtered messages.
     * @throws TesterraSystemException thrown if messages can't be filtered.
     */
    private static Message[] pMessagesFilter(final Map<String, String> map,
            final Message[] messages) throws TesterraSystemException {
        final ArrayList<Message> messageList = new ArrayList<Message>();
        for (final Message m : messages) {
            try {
                Boolean equals = true;
                if ((!m.getSubject().equalsIgnoreCase(map.get("subject")))) {
                    equals = false;
                }
                if (equals && !Arrays.toString(m.getFrom()).equalsIgnoreCase(map.get("from"))) {
                    equals = false;
                }
                if (equals) {
                    messageList.add(m);
                }
            } catch (final MessagingException e) {
                throw new TesterraSystemException(e);
            }
        }
        final Message[] ret = new Message[messageList.size()];
        messageList.toArray(ret);
        return ret;
    }

    /**
     * Sets the header for the message.
     * 
     * @param message The message to set the header.
     * @param subject The subject to set.
     * @param fromAddresses The FROM Address/Addresses.
     * @param sender The sender value.
     * @param toAddresses The TO Address/Addresses.
     * @param ccAddresses The CC Address/Addresses.
     * @param bccAddresses The BCC Address/Addresses.
     * @return The message set with headers.
     */
    // CHECKSTYLE:OFF
    public static MimeMessage setMimeMessageHeaders(final MimeMessage message, final String subject,
            final Address[] fromAddresses, final String sender,
            final Address[] toAddresses, final Address[] ccAddresses, final Address[] bccAddresses) {
        return pSetMimeMessageHeaders(message, subject, fromAddresses, sender, toAddresses, ccAddresses, bccAddresses);
    }

    /**
     * Sets the header for the message.
     * 
     * @param message The message to set the header.
     * @param subject The subject to set.
     * @param fromAddresses The FROM Address/Addresses.
     * @param sender The sender value.
     * @param toAddresses The TO Address/Addresses.
     * @param ccAddresses The CC Address/Addresses.
     * @param bccAddresses The BCC Address/Addresses.
     * @return The message set with headers.
     */
    private static MimeMessage pSetMimeMessageHeaders(final MimeMessage message, final String subject,
            final Address[] fromAddresses, final String sender,
            final Address[] toAddresses, final Address[] ccAddresses, final Address[] bccAddresses) {
        // CHECKSTYLE:ON
        try {
            if (subject != null && subject.length() > 0) {
                message.setSubject(subject);
            }

            // from, to, cc, bcc
            if (fromAddresses != null && fromAddresses.length != 0) {
                message.removeHeader("From");
                message.addFrom(fromAddresses);
            }

            if (sender != null && !"".equals(sender)) {
                message.setHeader("Sender", sender);
            }

            // TO
            if (toAddresses != null && toAddresses.length > 0) {
                message.removeHeader("To");
                message.addRecipients(Message.RecipientType.TO, toAddresses);
            }

            if (ccAddresses != null && ccAddresses.length > 0) {
                message.addRecipients(Message.RecipientType.CC, ccAddresses);
            }
            if (bccAddresses != null && bccAddresses.length > 0) {
                message.addRecipients(Message.RecipientType.BCC, bccAddresses);
            }

            message.saveChanges();

        } catch (final MessagingException e) {
            LOGGER.error(e.getMessage());
        }
        return message;
    }
}
