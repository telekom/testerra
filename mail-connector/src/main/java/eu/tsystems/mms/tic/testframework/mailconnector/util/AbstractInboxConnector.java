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

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.RecipientTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.SubjectTerm;
import org.apache.commons.lang3.ArrayUtils;

/**
 * abstract class to handle mail connector
 *
 * @author sepr
 */
public abstract class AbstractInboxConnector extends AbstractMailConnector implements Loggable {
    /**
     * Wait until messages with search criteria are received.
     *
     * @param searchCriterias The subject which message should contain.
     * @return The message.
     *
     * @throws AddressException thrown if an error by waiting for the message occurs.
     * @deprecated Use {@link #query(EmailQuery)} instead
     */
    @Deprecated
    public List<Email> waitForMails(List<SearchCriteria> searchCriterias) throws AddressException {
        EmailQuery query = new EmailQuery();
        return waitForMails(searchCriterias, query.getRetryCount(), Math.round(query.getPauseMs()/1000f));
    }

    /**
     * Wait until messages with search term are received.
     *
     * @param searchTerm The criterias which the message should contain.
     * @return The message.
     * @deprecated Use {@link #query(EmailQuery)} instead
     */
    @Deprecated
    public List<Email> waitForMails(final SearchTerm searchTerm) {
        EmailQuery query = new EmailQuery();
        return waitForMails(searchTerm, query.getRetryCount(), Math.round(query.getPauseMs()/1000f));
    }

    /**
     * Wait until messages with search term are received in the given folder.
     * @param searchTerm
     * @param folderName
     * @return
     * @deprecated Use {@link #query(EmailQuery)} instead
     */
    @Deprecated
    public List<Email> waitForMails(final SearchTerm searchTerm, final String folderName) {
        EmailQuery query = new EmailQuery();
        return waitForMails(searchTerm, query.getRetryCount(), Math.round(query.getPauseMs()/1000f), folderName);
    }

    /**
     * Wait until messages with search criteria are received.
     *
     * @param searchCriterias     The subject which message should contain.
     * @param maxReadTries
     * @param pollingTimerSeconds
     * @return The message.
     *
     * @throws AddressException thrown if an error by waiting for the message occurs.
     * @deprecated Use {@link #query(EmailQuery)} instead
     */
    @Deprecated
    public List<Email> waitForMails(List<SearchCriteria> searchCriterias, int maxReadTries, int pollingTimerSeconds) throws AddressException {
        final SearchTerm searchTerm = translateSearchCriterias(searchCriterias);
        return waitForMails(searchTerm, maxReadTries, pollingTimerSeconds);
    }

    /**
     * translate Array of SearchCriteria to Array of SearchTerms
     * @param searchCriterias
     * @return
     *
     * @throws AddressException thrown if an error occurred in the translation of the searchCriterias to SearchTerm.
     */
    private SearchTerm[] getSearchTermsFromSearchCriterias(final SearchCriteria[] searchCriterias) throws AddressException {
        SearchTerm[] searchTerms = new SearchTerm[searchCriterias.length];
        for (int i = 0; i < searchCriterias.length; i++) {
            searchTerms[i] = translateSearchCriterias(Collections.singletonList(searchCriterias[i]));
        }

        return searchTerms;
    }

    /**
     * translate a given List of SearchCriteria to a SearchTerm
     * @param searchCriterias
     * @return
     *
     * @throws AddressException thrown if an error occurred in the translation of the searchCriterias to SearchTerm.
     */
    private SearchTerm translateSearchCriterias(final List<SearchCriteria> searchCriterias) throws AddressException {

        final int searchCriteriasSize = searchCriterias.size();
        final SearchTerm searchTerm;

        switch (searchCriteriasSize) {
            case 0:
                searchTerm = null;
                break;
            case 1:
                searchTerm = pTranslateSearchCriteriaToSearchTerm(searchCriterias.get(0));
                break;
            default:
                final SearchTerm[] searchTerms = new SearchTerm[searchCriteriasSize];
                for (int i = 0; i < searchCriteriasSize; i++) {
                    searchTerms[i] = pTranslateSearchCriteriaToSearchTerm(searchCriterias.get(i));
                }
                searchTerm = new AndTerm(searchTerms);
                break;
        }

        return searchTerm;
    }

    private SearchTerm pTranslateSearchCriteriaToSearchTerm(final SearchCriteria searchCriteria) throws AddressException {

        final SearchCriteriaType searchCriteriaType = searchCriteria.getSearchCriteriaType();

        switch (searchCriteriaType) {
                case SENDER:
                    final String sender = searchCriteria.getStringValue();
                    return new FromTerm(new InternetAddress(sender));

                case RECIPIENT:
                    final String recipient = searchCriteria.getStringValue();
                    return new RecipientTerm(Message.RecipientType.TO, new InternetAddress(recipient));

                case SUBJECT:
                    final String subject = searchCriteria.getStringValue();
                    return new SubjectTerm(subject);

                case AFTER_DATE:
                    final Date expectedDate = (Date) searchCriteria.getValue();
                    return new SentDateTerm(ComparisonTerm.GE, expectedDate);

                case MESSAGEID:
                    final String messageId = searchCriteria.getStringValue();
                    return new MessageIDTerm(messageId);

                default:
                    throw new TesterraSystemException("Not yet implemented: " + searchCriteriaType);
            }
        }

    /**
     * Wait until messages with the specified search term are received.
     *
     * @param searchTerm     The search term which the message should contain.
     * @param maxReadTries
     * @param pollingTimerSeconds
     *
     * @return The message.
     * @deprecated Use {@link #query(EmailQuery)} instead
     */
    public List<Email> waitForMails(final SearchTerm searchTerm, int maxReadTries, int pollingTimerSeconds) {
        return waitForMails(searchTerm, maxReadTries, pollingTimerSeconds, getInboxFolder());
    }

    /**
     * Wait until messages with the specified search term are received in the given folder.
     * @param searchTerm
     * @param maxReadTries
     * @param pollingTimerSeconds
     * @param folderName
     * @return
     * @deprecated Use {@link #query(EmailQuery)} instead
     * @throws RuntimeException When there are no emails present.
     */
    public List<Email> waitForMails(final SearchTerm searchTerm, int maxReadTries, int pollingTimerSeconds, final String folderName) {
        EmailQuery query = new EmailQuery()
                .setSearchTerm(searchTerm)
                .setRetryCount(maxReadTries)
                .setPauseMs(pollingTimerSeconds*1000)
                .setFolderName(folderName);

        List<Email> emailList = query(query).collect(Collectors.toList());
        if (emailList.isEmpty()) {
            throw new RuntimeException(String.format(
                    "No messages found after %s seconds.",
                    pollingTimerSeconds * maxReadTries)
            );
        }
        return emailList;
    }

    /**
     * Queries emails by given {@link EmailQuery}
     */
    public Stream<Email> query(EmailQuery query) {
        String folderName = query.getFolderName();
        if (folderName==null) folderName = getInboxFolder();

        Stream<Email> emailStream;
        try {
            Stream<MimeMessage> messageStream = waitForMessages(query.getSearchTerm(), query.getRetryCount(), query.getPauseMs(), folderName);
            emailStream = messageStream.map(Email::new);
            Predicate<Email> filter = query.getFilter();
            if (filter != null) emailStream = emailStream.filter(filter);
        } catch (MessagingException e) {
            throw new RuntimeException("Could not query emails", e);
        }
        return emailStream;
    }

    /**
     * Waits for messages and returns a stream
     * @throws MessagingException
     */
    private Stream<MimeMessage> waitForMessages(final SearchTerm searchTerm, int maxReadTries, long pauseMs, final String foldername) throws MessagingException {
        Stream<MimeMessage> mimeMessages = Stream.empty();

        if (maxReadTries < 1) {
            maxReadTries = 1;
        }

        for (int i = 0; i < maxReadTries; i++) {
            Session session = getSession();
            Store store = session.getStore();
            store.connect();

            Folder folder = store.getFolder(foldername);
            folder.open(Folder.READ_ONLY);

            Runnable closeStoreAndFolder = () -> {
                try {
                    folder.close();
                    store.close();
                } catch (MessagingException e) {
                    log().error("Unable to close", e);
                }
            };

            Message[] messages;

            if (searchTerm != null) {
                messages = folder.search(searchTerm);
            } else {
                messages = folder.getMessages();
            }

            if (messages.length == 0) {
                TimerUtils.sleep(Long.valueOf(pauseMs).intValue(), "waiting for emails (try: " + (i+1) +"/" + maxReadTries + ")");
                closeStoreAndFolder.run();
            } else {
                mimeMessages = Stream.of(messages)
                        .onClose(closeStoreAndFolder)
                        .map(message -> {
                            MimeMessage mimeMessage = null;
                            try {
                                mimeMessage = new MimeMessage((MimeMessage) message);
                            } catch (MessagingException e) {
                                log().warn("Unable to create " + MimeMessage.class.getSimpleName(), e);
                            }
                            return Optional.ofNullable(mimeMessage);
                        })
                        .filter(Optional::isPresent)
                        .map(Optional::get);
                break;
            }
        }
        return mimeMessages;
    }

    /**
     * Get the message count from {@link #getInboxFolder()}.
     */
    public long getMessageCount() {
        return this.pGetMessageCount(getInboxFolder());
    }

    /**
     * Get the message count from a specified folder name.
     */
    public long getMessageCount(String folderName) {
        return this.pGetMessageCount(folderName);
    }

    /**
     * Get the message count from pop3 server.
     *
     * @return The number of new messages.
     */
    private int pGetMessageCount(String folderName) {

        Store store;
        int nrOfMessages;
        try {
            store = getSession().getStore();
            store.connect(getServer(), getUsername(), getPassword());
            final Folder root = store.getDefaultFolder();
            final Folder folder = root.getFolder(folderName);
            folder.open(Folder.READ_ONLY);
            nrOfMessages = folder.getMessageCount();
            store.close();
        } catch (final NoSuchProviderException e) {
            log().error(e.getMessage());
            throw new RuntimeException(e);
        } catch (final MessagingException e) {
            log().error("Error in getMessageCount", e);
            throw new RuntimeException(e);
        }

        return nrOfMessages;
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
     */
    public boolean deleteMessage(
            final String recipient,
            final Message.RecipientType recipientType,
            final String subject,
            final String messageId
    ) {
        return this.pDeleteMessage(recipient, recipientType, subject, messageId);
    }

    /**
     * deletes tt. mail by it's message id from inbox.
     *
     * @param mail {@link Email} object with messageId set.
     *
     * @return true if message has been deleted.
     */
    public boolean deleteMessage(final Email mail) {
        return deleteMessage(new MessageIDTerm(mail.getMessageID()));
    }

    /**
     * deletes messages by given search criterias
     *
     * @param messagesCriterias List of search criteria list - inner list represents searchcriterias to identify one
     *            message
     *
     * @return true if messages were deleted
     *
     * @throws AddressException thrown if an error occurred in the translation of the searchCriterias to SearchTerm.
     */
    @Deprecated
    public boolean deleteMessage(List<List<SearchCriteria>> messagesCriterias) throws AddressException {

        boolean isDeleted = false;

        for (List<SearchCriteria> messageCriterias : messagesCriterias) {
            final SearchTerm searchTerm = translateSearchCriterias(messageCriterias);
            isDeleted = deleteMessage(searchTerm);

        }
        return isDeleted;
    }

    /**
     * deletes messages by given search criterias
     *
     * @param searchTerms List of search criteria list - inner list represents searchcriterias to identify one
     *            message
     *
     * @return true if messages were deleted
     */
    public boolean deleteMessages(final List<SearchTerm> searchTerms) {

        boolean isDeleted = false;

        for (final SearchTerm searchTerm : searchTerms) {
            isDeleted = deleteMessage(searchTerm);
        }

        return isDeleted;
    }

    /**
     * delete message by given SearchTerm in the InboxFolder
     * @param searchTerm
     * @return
     */
    public boolean deleteMessage(final SearchTerm searchTerm) {
        return this.pDeleteMessage(searchTerm);
    }

    /**
     * delete message by given SearchTerm in the given folder
     * @param searchTerm
     * @param folderName
     * @return
     */
    public boolean deleteMessage(final SearchTerm searchTerm, final String folderName) {
        return this.pDeleteMessage(searchTerm, folderName);
    }

    /**
     * move given message into folder with given name.
     *
     * @param targetFolder Name of folder to move into.
     * @param message {@link Email} to move (compared by messageId)
     *
     * @return true if moved.
     */
    public boolean moveMessage(final String targetFolder, final Email message) {
        final MessageIDTerm messageIDTerm = new MessageIDTerm(message.getMessageID());
        return pMoveMessage(targetFolder, messageIDTerm) == 1;
    }

    /**
     * move messages by given search terms into folder with given name.
     *
     * @param targetFolder Name of folder to move into.
     * @param searchTerms List of search term list - inner list represents searchcriterias to identify one
     *            message
     *
     * @return count of moved mails.
     */
    public int moveMessage(final String targetFolder, final SearchTerm... searchTerms) {
        return pMoveMessage(targetFolder, searchTerms);
    }

    /**
     * move messages by given search criterias into folder with given name.
     *
     * @param targetFolder Name of folder to move into.
     * @param searchCriterias List of search criteria list - inner list represents searchcriterias to identify one
     *            message
     *
     * @return count of moved mails.
     *
     * @throws AddressException thrown if an error occurred in the translation of the searchCriterias to SearchTerm.
     */
    public int moveMessage(String targetFolder, SearchCriteria... searchCriterias) throws AddressException {

        final SearchTerm[] searchTerms = getSearchTermsFromSearchCriterias(searchCriterias);
        return moveMessage(targetFolder, searchTerms);
    }

    private int pMoveMessage(final String targetFolderName, final SearchTerm... searchTerms) {
        Store store;
        int count;
        try {
            store = getSession().getStore();
            store.connect();

            final Folder targetFolder = store.getFolder(targetFolderName);
            if (!targetFolder.exists()) {
                targetFolder.create(Folder.HOLDS_MESSAGES);
            }

            final Folder folder = store.getFolder(getInboxFolder());
            folder.open(Folder.READ_WRITE);

            log().info("Checking messages from " + getInboxFolder() + " for MessageID:");
            ArrayList<Message> copyList = new ArrayList<>();
            for (final SearchTerm searchTerm : searchTerms) {
                final Message[] messages = folder.search(searchTerm);

                copyList.addAll(Arrays.asList(messages));

            }

            final Message[] msgArray = copyList.toArray(new Message[0]);
            folder.copyMessages(msgArray, targetFolder);

            count = msgArray.length;

            folder.setFlags(msgArray, new Flags(Flags.Flag.DELETED), true);

            folder.close(true);
            store.close();

        } catch (final MessagingException e) {
            log().error(e.getMessage());
            throw new RuntimeException(e);
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
    @Deprecated
    public boolean deleteMessages(List<String> deleteCriteriaValues, DeleteCriteriaType deleteCriteriaType) {

        List<Boolean> booleanValues = new ArrayList<>();

        for (String deleteCriteriaValue : deleteCriteriaValues) {
            boolean isDeleted;
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

    /**
     * @deprecated Use {@link #deleteMessage(SearchTerm)} instead
     */
    @Deprecated
    public boolean deleteMessage(List<String> deleteCriteriaValues, DeleteCriteriaType deleteCriteriaType) {
        return deleteMessages(deleteCriteriaValues, deleteCriteriaType);
    }

    /**
     * delete all message in the InboxFolder
     * @return
     */
    public boolean deleteAllMessages() {
        return pDeleteMessage(null);
    }

    private boolean pDeleteMessage(final SearchTerm searchTerm) {
        return pDeleteMessage(searchTerm, getInboxFolder());
    }

    private boolean pDeleteMessage(final SearchTerm searchTerm, final String folderName) {

        boolean deleted = false;

        Store store;
        try {
            store = getSession().getStore();
            store.connect();
            final Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_WRITE);

            final Message[] messages;

            if (searchTerm != null) {
                messages = folder.search(searchTerm);
            } else {
                messages = folder.getMessages();
            }

            log().info("Checking messages from " + getInboxFolder() + " for MessageID:");

            if (messages.length > 0) {
                String msg = "Message found, DELETING";
                for (final Message message : messages) {

                    if (message.getSubject() != null) {
                        msg += ": " + message.getSubject();
                    }
                    log().info(msg);
                    message.setFlag(Flags.Flag.DELETED, true);
                }
            } else {
                log().info("None.");
            }
            // leads to error "folder not open" when reading message content
            folder.close(true);
            store.close();

        } catch (final MessagingException e) {
            log().error(e.getMessage());
            throw new RuntimeException(e);
        }
        return deleted;
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
     */
    @Deprecated
    private boolean pDeleteMessage(final String recipient, final Message.RecipientType recipientType,
                                   final String subject, final String messageId) {

        boolean deleted = false;

        Store store;
        try {
            store = getSession().getStore();
            store.connect();
            final Folder folder = store.getFolder(getInboxFolder());
            folder.open(Folder.READ_WRITE);
            final Message[] messages = folder.getMessages();
            log().info("Checking messages from " + getInboxFolder() + " for MessageID:");

            if (messages.length > 0) {
                for (final Message message : messages) {
                    deleted = compareMessageAndDelete(message, recipient, recipientType, subject, messageId);
                }
            } else {
                log().info("None.");
            }
            // leads to error "folder not open" when reading message content
            folder.close(true);
            store.close();

        } catch (final MessagingException e) {
            log().error(e.getMessage());
            throw new RuntimeException(e);
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
     */
    private boolean compareMessageAndDelete(final Message message, final String recipient,
                                            final Message.RecipientType recipientType, final String subject,
                                            final String messageId) {

        try {
            boolean found;

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
                log().info(msg);
                message.setFlag(Flags.Flag.DELETED, true);
                return true;
            } else {
                return false;
            }
        } catch (final MessageRemovedException e) {
            log().error("Error deleting message: " + e.getMessage());
            return false;
        } catch (final MessagingException e) {
            log().error("Error handling message", e);
            throw new RuntimeException(e);
        }
    }
}
