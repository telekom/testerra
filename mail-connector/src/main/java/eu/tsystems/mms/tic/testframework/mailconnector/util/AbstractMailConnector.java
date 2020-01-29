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
/*
 * Created on 08.11.2012
 *
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.mailconnector.util;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * abstract class to handle mail connector
 *
 * @author sepr
 */
public abstract class AbstractMailConnector {

    /**
     * LOGGER.
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Key for pollingfrequency Property.
     */
    private static final String POLLING_TIMER_SECONDS_PROPERTY = "POLLING_TIMER_SECONDS";
    /**
     * Key for max_read_tries Property.
     */
    private static final String MAX_READ_TRIES_PROPERTY = "MAX_READ_TRIES";

    /**
     * Default value.
     */
    private static final String SLEEP_SECONDS_DEFAULT = "10";
    /**
     * Default value.
     */
    private static final String MAX_READ_TRIES_DEFAULT = "20";

    private static final int SLEEP_SECONDS = Integer.valueOf(PropertyManager.getProperty(POLLING_TIMER_SECONDS_PROPERTY, SLEEP_SECONDS_DEFAULT));
    private static final int MAX_READ_TRIES = Integer.valueOf(PropertyManager.getProperty(MAX_READ_TRIES_PROPERTY, MAX_READ_TRIES_DEFAULT));

    /**
     * Mail Server Name.
     */
    private String server;
    /**
     * Mail Server Port.
     */
    private String port;
    /**
     * Mail Folder.
     */
    private String inboxFolder;
    /**
     * Mail username.
     */
    private String username;
    /**
     * Mail password.
     */
    private String password;
    /**
     * Set Console Output to debug.
     */
    private boolean debug;
    /**
     * Use SSL.
     */
    private boolean sslEnabled;
    /**
     * The Mail Session.
     */
    private Session session;

    protected abstract void openSession();

    /**
     * gets the session
     *
     * @return the session
     */
    public Session getSession() {

        if (session == null) {
            openSession();
        }

        return session;
    }

    /**
     * gets the server
     *
     * @return the server
     */
    public String getServer() {

        return server;
    }

    /**
     * sets the server
     *
     * @param server the server to set
     */
    public void setServer(final String server) {

        this.server = server;
    }

    /**
     * gets the port
     *
     * @return the port
     */
    public String getPort() {

        return port;
    }

    /**
     * sets the port
     *
     * @param port the port to set
     */
    public void setPort(final String port) {

        this.port = port;
    }

    /**
     * gets the inboxFolder
     *
     * @return the inboxFolder
     */
    public String getInboxFolder() {

        return inboxFolder;
    }

    /**
     * sets the inboxFolder
     *
     * @param inboxFolder the inboxFolder to set
     */
    public void setInboxFolder(final String inboxFolder) {

        this.inboxFolder = inboxFolder;
    }

    /**
     * gets the username
     *
     * @return the username
     */
    public String getUsername() {

        return username;
    }

    /**
     * sets the username
     *
     * @param username the username to set
     */
    public void setUsername(final String username) {

        this.username = username;
    }

    /**
     * gets the password
     *
     * @return the password
     */
    public String getPassword() {

        return password;
    }

    /**
     * sets the password
     *
     * @param password the password to set
     */
    public void setPassword(final String password) {

        this.password = password;
    }

    /**
     * checks if the debug is true or false
     *
     * @return the debug
     */
    public boolean isDebug() {

        return debug;
    }

    /**
     * sets the debug
     *
     * @param debug the debug to set
     */
    public void setDebug(final boolean debug) {

        this.debug = debug;
    }

    /**
     * checks the sslEnabled if it is true or false
     *
     * @return the sslEnabled
     */
    public boolean isSslEnabled() {

        return sslEnabled;
    }

    /**
     * sets the sslEnabled
     *
     * @param sslEnabled the sslEnabled to set
     */
    public void setSslEnabled(final boolean sslEnabled) {

        this.sslEnabled = sslEnabled;
    }

    /**
     * sets the session
     *
     * @param session the session to set
     */
    public void setSession(final Session session) {

        this.session = session;
    }

    public List<Email> waitForEMails(List<SearchCriteria> searchCriterias) {
        return waitForEMails(searchCriterias, MAX_READ_TRIES, SLEEP_SECONDS);
    }

    /**
     * Wait until messages with search criteria are received.
     *
     * @param searchCriterias     The subject which message should contain.
     * @param maxReadTries
     * @param pollingTimerSeconds
     *
     * @return The message.
     *
     * @throws TesterraSystemException thrown if an error by waiting for the message occurs.
     * @deprecated Use {@link #waitForEMails(List)} instead
     */
    public List<Email> waitForEMails(List<SearchCriteria> searchCriterias, int maxReadTries, int pollingTimerSeconds) {
        List<MimeMessage> messages = pWaitForMessage(searchCriterias, maxReadTries, pollingTimerSeconds);
        List<Email> out = new LinkedList<>();
        for (MimeMessage message : messages) {
            out.add(new Email(message));
        }
        return out;
    }

    private List<MimeMessage> pWaitForMessage(List<SearchCriteria> searchCriterias, int maxReadTries, int pollingTimerSeconds) throws TesterraSystemException {

        String msg = "Wait for message with criterias: ";
        for (SearchCriteria searchCriteria : searchCriterias) {
            msg += "\n " + searchCriteria.getSearchCriteriaType() + " contains " + searchCriteria.getValue();
        }
        logger.info(msg);

        Store store = null;

        List<MimeMessage> out = new LinkedList<>();

        if (maxReadTries < 1) {
            maxReadTries = 1;
            logger.info("Changing read tries to min value: 1");
        }
        if (pollingTimerSeconds < 10) {
            pollingTimerSeconds = 10;
            logger.info("Changing poller timer to min value: 10s");
        }

        try {
            int lastMessageCount = 0;
            int newMessageCount;
            int noScanCounter = 0;

            for (int i = 0; i < maxReadTries; i++) {
                store = getSession().getStore();
                store.connect();
                Folder folder = store.getFolder(getInboxFolder());
                folder.open(Folder.READ_ONLY);
                newMessageCount = folder.getMessageCount();
                if (newMessageCount < lastMessageCount || (noScanCounter != 0 && noScanCounter % 3 == 0)) {
                    lastMessageCount = 0;
                } else if (newMessageCount == lastMessageCount) {
                    noScanCounter++;
                }

                if (newMessageCount > lastMessageCount) {
                    // cause folder.getMessages starts with 1
                    if (lastMessageCount == 0) {
                        lastMessageCount = 1;
                    }

                    /*
                     * find the message
                     */
                    final Message[] messages = folder.getMessages(lastMessageCount, newMessageCount);
                    for (int j = messages.length - 1; j >= 0; j--) {
                        boolean notMatched = false;

                        for (SearchCriteria searchCriteria : searchCriterias) {
                            notMatched = !criteriaMatchesMessage(searchCriteria, messages[j]);
                            if (notMatched) {
                                break;
                            }
                        }

                        if (notMatched) {
                            continue; // try the next mail
                        }

                        /*
                         being here means all checks are gone true and the mail was found
                        */
                        // copy message to fetch all data, bc we close the store at the end and then everything must be fetched
                        MimeMessage mimeMessage = new MimeMessage((MimeMessage) messages[j]);
                        MessageUtils.multiPartBugfix(mimeMessage);

                        out.add(mimeMessage);
                    }
                }

                lastMessageCount = newMessageCount;

                if (out.size() > 0) {
                    return out;
                }

                // sleep for pollingTimerSeconds
                TimerUtils.sleep(pollingTimerSeconds * 1000);
            }

        } catch (final Exception e) {
            logger.error("Error searching for message", e);
            throw new TesterraSystemException(e);
        } finally {
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException e) {
                    logger.warn("Error closing connection", e);
                }
            }
        }

        if (out.size() == 0) {
            throw new TesterraSystemException(String.format("No messages found after %s seconds.",
                    pollingTimerSeconds * maxReadTries));
        }

        return out;
    }

    private boolean criteriaMatchesMessage(SearchCriteria searchCriteria, Message message) throws MessagingException {
        boolean matches = true;
        final SearchCriteriaType searchCriteriaType = searchCriteria.getSearchCriteriaType();
        switch (searchCriteriaType) {
            case SENDER:
                Address[] from = message.getFrom();
                if (from == null || from.length == 0) {
                    // then it could not match and this is exit criteria
                    matches = false;
                    break;
                }

                // check senders
                String expectedSender = searchCriteria.getStringValue();
                boolean found = false;
                for (Address address : from) {
                    if (address != null && address.toString().contains(expectedSender)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    logger.info("Found expectedSender: " + expectedSender);
                } else {
                    logger.info("Sender does not match: " + expectedSender);
                    // not matched needs to be set, otherwise its considered a match
                    matches = false;
                }
                break;
            case RECIPIENT:
                Address[] recipientsTO = message.getRecipients(Message.RecipientType.TO);
                Address[] recipientsCC = message.getRecipients(Message.RecipientType.CC);
                Address[] recipientsBCC = message.getRecipients(Message.RecipientType.BCC);

                // combine allRecipients recispients
                Object[] combine = ArrayUtils.addAll(recipientsTO, recipientsCC);
                combine = ArrayUtils.addAll(combine, recipientsBCC);
                Address[] allRecipients = (Address[]) combine;

                // check
                if (allRecipients == null || allRecipients.length == 0) {
                    // then it could not match and this is exit criteria
                    matches = false;
                    break;
                }

                // check senders
                String expectedRecipient = searchCriteria.getStringValue();
                boolean foundRecipient = false;
                for (Address address : allRecipients) {
                    if (address != null && address.toString().contains(expectedRecipient)) {
                        foundRecipient = true;
                        break;
                    }
                }

                if (foundRecipient) {
                    logger.info("Found expected Recipient: " + expectedRecipient);
                } else {
                    logger.info("Recipient does not match: " + expectedRecipient);
                    matches = false;
                }
                break;
            case SUBJECT:
                if (message.getSubject() != null) {
                    String expectedSubject = searchCriteria.getStringValue();
                    String subject = message.getSubject();
                    if (!StringUtils.isStringEmpty(subject) && subject.contains(expectedSubject)) {
                        logger.info("Found subject part: " + expectedSubject);
                    } else {
                        logger.info("Expected subject " + expectedSubject + " does not match: " + subject);
                        matches = false;
                    }
                }
                break;
            case AFTER_DATE:
                if (message.getSentDate() != null) {

                    if (!(searchCriteria.getValue() instanceof Date)) {
                        throw new TesterraRuntimeException(
                                "Values of SearchCriteriaType AFTER_DATE must be of type java.util.Date");
                    }

                    Date expectedDate = (Date) searchCriteria.getValue();
                    DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM,
                            FormatStyle.MEDIUM);
                    Date sentDate = message.getSentDate();

                    String sentDateString = dtf.format(sentDate.toInstant().atZone(ZoneId.systemDefault()));
                    String expectedDateString = dtf.format(expectedDate.toInstant().atZone(ZoneId.systemDefault()));

                    if (sentDate.after(expectedDate)) {
                        logger.info("Sent Date {} after criteria {}", sentDateString, expectedDateString);
                    } else {
                        matches = false;
                        logger.info("Sent Date {} not after criteria {}", sentDateString, expectedDateString);
                    }
                }
                break;
            case MESSAGEID:
                String[] messageId = message.getHeader("Message-ID");
                if (messageId != null) {
                    Object value = searchCriteria.getValue();
                    if (value != null && value instanceof String) {
                        matches = Arrays.asList(messageId).contains(value);
                    } else {
                        matches = false;
                        logger.info("Could not get message id criteria value. Message does not match.");
                    }
                } else {
                    matches = false;
                    logger.info("Could not get message id of message. Message does not match.");
                }
                break;
            default:
                throw new TesterraSystemException("Not yet implemented: " + searchCriteriaType);
        }
        return matches;
    }

    /**
     * Get the message count from pop3 server.
     *
     * @return The number of new messages.
     *
     * @throws TesterraSystemException thrown if message count can't be ascertained.
     */
    public int getMessageCount() throws TesterraSystemException {

        return this.pGetMessageCount();
    }

    /**
     * Get the message count from pop3 server.
     *
     * @return The number of new messages.
     *
     * @throws TesterraSystemException thrown if message count can't be ascertained.
     */
    private int pGetMessageCount() throws TesterraSystemException {

        Store store;
        int nrOfMessages = 0;
        try {
            store = getSession().getStore();
            store.connect(getServer(), getUsername(), getPassword());
            final Folder root = store.getDefaultFolder();
            final Folder folder = root.getFolder(getInboxFolder());
            folder.open(Folder.READ_ONLY);
            nrOfMessages = folder.getMessageCount();
            store.close();
        } catch (final NoSuchProviderException e) {
            logger.error(e.getMessage());
            throw new TesterraSystemException(e);
        } catch (final MessagingException e) {
            logger.error("Error in getMessageCount", e);
            throw new TesterraSystemException(e);
        }

        return nrOfMessages;
    }

    /**
     * Get all messages.
     *
     * @return An array containing the messages.
     *
     * @throws TesterraSystemException thrown if messages couldn't read.
     */
    public MimeMessage[] getMessages() throws TesterraSystemException {

        return this.pGetMessages();
    }

    /**
     * Get all messages.
     *
     * @return An array containing the messages.
     *
     * @throws TesterraSystemException thrown if messages couldn't read.
     */
    private MimeMessage[] pGetMessages() throws TesterraSystemException {

        Store store = null;
        ArrayList<MimeMessage> mimes = null;
        try {
            store = getSession().getStore();
            store.connect();
            final Folder folder = store.getFolder(getInboxFolder());
            folder.open(Folder.READ_ONLY);
            final Message[] messages = folder.getMessages();
            mimes = new ArrayList<MimeMessage>();
            logger.info("Fetched messages from " + getInboxFolder() + ":");
            if (messages.length > 0) {
                for (final Message message : messages) {
                    MessageUtils.multiPartBugfix(message);
                    mimes.add((MimeMessage) message);
                }
            } else {
                logger.info("None.");
            }
            // folder.close(true); // leads to error "folder not open" when reading message content
            store.close();
        } catch (final MessagingException e) {
            throw new TesterraSystemException(e);
        }
        return mimes.toArray(new MimeMessage[mimes.size()]);
    }

    /**
     * Deletes a message.
     *
     * @param recipient     The recipient String. Can be null.
     * @param recipientType The type of the recipient.
     * @param subject       The subject of the mail. Can be null if mail has no subject.
     * @param messageId     The id of the message. Can be null.
     *
     * @return true if message was deleted, else false
     *
     * @throws TesterraSystemException thrown if an message can't deleted.
     */
    public boolean deleteMessage(final String recipient, final Message.RecipientType recipientType,
                                 final String subject, final String messageId) throws TesterraSystemException {

        return this.pDeleteMessage(recipient, recipientType, subject, messageId);
    }

    /**
     * deletes tt. mail by it's message id from inbox.
     *
     * @param mail {@link Email} object with messageId set.
     *
     * @return true if message has been deleted.
     */
    public boolean deleteMessage(Email mail) {
        return deleteMessage(null, Message.RecipientType.TO, null, mail.getMessageID());
    }

    /**
     * deletes messages by given search criterias
     *
     * @param messagesCriterias List of search criteria list - inner list represents searchcriterias to identify one
     *            message
     *
     * @return true if messages were deleted
     */
    public boolean deleteMessage(List<List<SearchCriteria>> messagesCriterias) {

        boolean isDeleted = false;

        for (List<SearchCriteria> messageCriterias : messagesCriterias) {
            String recipient = null;
            String subject = null;
            String messageId = null;
            for (SearchCriteria criteria : messageCriterias) {
                switch (criteria.getSearchCriteriaType()) {
                    case SENDER:
                        logger.warn("sender cannot be used to delete messages");
                        break;
                    case RECIPIENT:
                        recipient = criteria.getStringValue();
                        break;
                    case MESSAGEID:
                        messageId = criteria.getStringValue();
                        break;
                    case SUBJECT:
                        subject = criteria.getStringValue();
                        break;
                    default:
                        throw new TesterraRuntimeException("SearchCriteriaType not supported");
                }
                isDeleted = deleteMessage(recipient, Message.RecipientType.TO, subject, messageId);
            }
        }
        return isDeleted;
    }

    /**
     * move given message into folder with given name.
     *
     * @param targetFolder Name of folder to move into.
     * @param message {@link Email} to move (compared by messageId)
     *
     * @return true if moved.
     */
    public boolean moveMessage(String targetFolder, Email message) {
        SearchCriteria searchCriteria = new SearchCriteria(SearchCriteriaType.MESSAGEID, message.getMessageID());
        return pMoveMessage(targetFolder, searchCriteria) == 1;
    }

    /**
     * move messages by given search criterias into folder with given name.
     *
     * @param targetFolder Name of folder to move into.
     * @param messagesCriterias List of search criteria list - inner list represents searchcriterias to identify one
     *            message
     *
     * @return count of moved mails.
     */
    public int moveMessage(String targetFolder, SearchCriteria... messagesCriterias) {
        return pMoveMessage(targetFolder, messagesCriterias);
    }

    private int pMoveMessage(String targetFolderName, SearchCriteria... messagesCriterias) {
        Store store;
        int count = 0;
        try {
            store = getSession().getStore();
            store.connect();

            final Folder targetFolder = store.getFolder(targetFolderName);
            if (!targetFolder.exists()) {
                targetFolder.create(Folder.HOLDS_MESSAGES);
            }

            final Folder folder = store.getFolder(getInboxFolder());
            folder.open(Folder.READ_WRITE);
            final Message[] messages = folder.getMessages();
            logger.info("Checking messages from " + getInboxFolder() + " for MessageID:");
            ArrayList<Message> copyList = new ArrayList<>();
            for (int j = messages.length - 1; j >= 0; j--) {
                boolean notMatched = false;
                // if no criterias given, all messages are moved.
                for (SearchCriteria searchCriteria : messagesCriterias) {
                    notMatched = !criteriaMatchesMessage(searchCriteria, messages[j]);
                    if (notMatched) {
                        break;
                    }
                }
                if (notMatched) {
                    continue; // try the next mail
                }

                /*
                 * being here means all checks are gone true and the mail was found
                 */
                copyList.add(messages[j]);
            }

            Message[] msgArray = copyList.toArray(new Message[copyList.size()]);
            folder.copyMessages(msgArray, targetFolder);

            count = msgArray.length;

            folder.setFlags(msgArray, new Flags(Flags.Flag.DELETED), true);

            folder.close(true);
            store.close();

        } catch (final NoSuchProviderException e) {
            logger.error(e.getMessage());
            throw new TesterraSystemException(e);
        } catch (final MessagingException e) {
            logger.error(e.getMessage());
            throw new TesterraSystemException(e);
        }
        return count;
    }

    /**
     * deletes messages with fitting parameters
     *
     * @param deleteCriteriaValues String List containing the desired values
     * @param deleteCriteriaType   Delete Criteria Type - Recipient, Subject or MessageID
     *
     * @return boolean - true if messages were deleted
     */
    public boolean deleteMessages(List<String> deleteCriteriaValues, DeleteCriteriaType deleteCriteriaType) {

        List<Boolean> booleanValues = new ArrayList<>();

        for (String deleteCriteriaValue : deleteCriteriaValues) {
            boolean isDeleted = false;
            switch (deleteCriteriaType) {
                case RECIPIENT:
                    isDeleted = deleteMessage(deleteCriteriaValue, Message.RecipientType.TO, null, null);
                    break;
                case SUBJECT:
                    isDeleted = deleteMessage(null, Message.RecipientType.TO, deleteCriteriaValue, null);
                    break;
                case MESSAGEID:
                    isDeleted = deleteMessage(null, Message.RecipientType.TO, null, deleteCriteriaValue);
                    break;
                default:
                    throw new TesterraSystemException("Not supported: " + deleteCriteriaType);
            }
            booleanValues.add(isDeleted);
        }

        for (Boolean booleanValue : booleanValues) {
            if (!booleanValue) {
                return false;
            }
        }

        return true;
    }

    @Deprecated
    public boolean deleteMessage(List<String> deleteCriteriaValues, DeleteCriteriaType deleteCriteriaType) {

        return deleteMessages(deleteCriteriaValues, deleteCriteriaType);
    }

    public boolean deleteAllMessages() {

        return deleteMessage(null, null, null, null);
    }

    /**
     * Deletes a message.
     *
     * @param recipient     The recipient String. Can be null.
     * @param recipientType The type of the recipient.
     * @param subject       The subject of the mail. Can be null if mail has no subject.
     * @param messageId     The id of the message. Can be null.
     *
     * @return true if message was deleted, else false
     *
     * @throws TesterraSystemException thrown if an message can't deleted.
     */
    private boolean pDeleteMessage(final String recipient, final Message.RecipientType recipientType,
                                   final String subject, final String messageId) throws TesterraSystemException {

        boolean deleted = false;

        Store store;
        try {
            store = getSession().getStore();
            store.connect();
            final Folder folder = store.getFolder(getInboxFolder());
            folder.open(Folder.READ_WRITE);
            final Message[] messages = folder.getMessages();
            logger.info("Checking messages from " + getInboxFolder() + " for MessageID:");

            if (messages.length > 0) {
                for (final Message message : messages) {
                    deleted = compareMessageAndDelete(message, recipient, recipientType, subject, messageId);
                }
            } else {
                logger.info("None.");
            }
            // leads to error "folder not open" when reading message content
            folder.close(true);
            store.close();

        } catch (final NoSuchProviderException e) {
            logger.error(e.getMessage());
            throw new TesterraSystemException(e);
        } catch (final MessagingException e) {
            logger.error(e.getMessage());
            throw new TesterraSystemException(e);
        }
        return deleted;
    }

    /**
     * Check if mail is on server, than delete.
     *
     * @param message       .
     * @param recipient     .
     * @param recipientType .
     * @param subject       .
     * @param messageId     .
     *
     * @return True if message was found and deleted, else false.
     *
     * @throws TesterraSystemException If some error by reading the messages occurred.
     */
    private boolean compareMessageAndDelete(final Message message, final String recipient,
                                            final Message.RecipientType recipientType, final String subject,
                                            final String messageId) throws TesterraSystemException {

        try {
            MessageUtils.multiPartBugfix(message);
            boolean found = false;

            if ((subject == null || message.getSubject() == null) && recipient != null) {
                // compare by recipient
                final InternetAddress recipientAddress = new InternetAddress(recipient);
                found = ArrayUtils.contains(message.getRecipients(recipientType),
                        recipientAddress);
            } else if (subject != null && message.getSubject() != null && recipient == null) {
                // compare by subject
                found = message.getSubject().equals(subject);
            } else if (subject != null && recipient != null) {
                // compare by subject and recipient
                final InternetAddress recipientAddress = new InternetAddress(recipient);
                found = message.getSubject().equals(subject)
                        && ArrayUtils.contains(message.getRecipients(recipientType),
                        recipientAddress);
            } else {
                // whatever
                found = true;
            }

            // additional check of message id
            if (((MimeMessage) message).getMessageID() != null && messageId != null) {
                found = found && ((MimeMessage) message).getMessageID().equals(messageId);
            }

            // away with it
            if (found) {
                String msg = "Message found, DELETING";
                if (message.getSubject() != null) {
                    msg += ": " + message.getSubject();
                }
                logger.info(msg);
                message.setFlag(Flags.Flag.DELETED, true);
                return true;
            } else {
                return false;
            }
        } catch (final MessageRemovedException e) {
            logger.error("Error deleting message: " + e.getMessage());
            return false;
        } catch (final MessagingException e) {
            logger.error("Error handling message", e);
            throw new TesterraSystemException(e);
        }
    }
}
