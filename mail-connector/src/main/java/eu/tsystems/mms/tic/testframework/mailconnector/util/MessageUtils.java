/*
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
