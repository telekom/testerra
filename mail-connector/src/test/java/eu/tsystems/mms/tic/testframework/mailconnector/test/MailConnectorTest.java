/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.mailconnector.test;

import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.mailconnector.imap.ImapMailConnector;
import eu.tsystems.mms.tic.testframework.mailconnector.pop3.POP3MailConnector;
import eu.tsystems.mms.tic.testframework.mailconnector.smtp.SMTPMailConnector;
import eu.tsystems.mms.tic.testframework.mailconnector.util.AbstractInboxConnector;
import eu.tsystems.mms.tic.testframework.mailconnector.util.Email;
import eu.tsystems.mms.tic.testframework.mailconnector.util.EmailAttachment;
import eu.tsystems.mms.tic.testframework.mailconnector.util.EmailQuery;
import eu.tsystems.mms.tic.testframework.mailconnector.util.MailUtils;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Address;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.AndTerm;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.FromTerm;
import jakarta.mail.search.RecipientTerm;
import jakarta.mail.search.SearchTerm;
import jakarta.mail.search.SentDateTerm;
import jakarta.mail.search.SubjectTerm;

/**
 * Integration Tests for TesterraMailConnector.
 *
 * @author mrgi, tbmi
 */
public class MailConnectorTest extends TesterraTest implements Loggable, PropertyManagerProvider, AssertProvider {

    // CONSTANTS
    /**
     * Constant PATH_HOME
     */
    private static final String PATH_HOME = System.getProperty("user.dir");

    /**
     * Constant STR_MAIL_SUBJECT
     */
    private static final String STR_MAIL_SUBJECT = "Test mail for TC:";

    /**
     * Constant STR_MAIL_TEXT
     */
    private static final String STR_MAIL_TEXT = "I am a test mail!";

    /**
     * Constant ERR_CONTENT_DIFFERS
     */
    private static final String ERR_CONTENT_DIFFERS = "Content of sent and received message is not the same!";

    /**
     * Constant ERR_HEADERS_DIFFER
     */
    private static final String ERR_HEADERS_DIFFER = "Headers of sent and received message are not the same!";

    /**
     * Constant ERR_NO_MSG_RECEIVED
     */
    private static final String ERR_NO_MSG_RECEIVED = "No Message was received!";

    /**
     * Constant ERR_NO_ATTACHMENT
     */
    private static final String ERR_NO_ATTACHMENT = "Message contains no attachment!";

    /**
     * Constant RECIPIENT
     */
    private static final String RECIPIENT = "test@localhost.com";

    /**
     * Constant SENDER
     */
    private static final String SENDER = "secret@host";

    // REFERENCES
    /**
     * SMTPMailConnector
     */
    private SMTPMailConnector smtp; // Mail connector using the SMTP protocol

    /**
     * POP3MailConnector
     */
    private POP3MailConnector pop3; // Mail connector using the POP3 protocol
    private ImapMailConnector imap; // Mail connector using the IMAP protocol

    private GreenMail mailServerAll; // Mail Server for Smtp and Pop3 including SSL

    /**
     * session
     */
    private Session session;

    /**
     * After every test-case the mail-box will be cleared
     */
    @AfterMethod
    public void clearMailBox(Method method) {

        pop3.deleteAllMessages();
        imap.deleteAllMessages();

        final boolean inboxPop3Empty = (pop3.getMessageCount() == 0);
        final boolean inboxImapEmpty = (imap.getMessageCount() == 0);

        Assert.assertTrue(inboxPop3Empty, "POP3 Mail box is empty");
        Assert.assertTrue(inboxImapEmpty, "Imap Mail box is empty");
    }

    @AfterClass
    public void shutDownServer() {
        mailServerAll.stop();
    }

    /**
     * Loads the mail connection properties and initializes the mail connector fields, as well as the smtp session.
     */
    @BeforeClass
    public void initProperties() {

        PROPERTY_MANAGER.loadProperties("mailconnection.properties");

        Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
        mailServerAll = new GreenMail(ServerSetupTest.ALL);
        mailServerAll.setUser(RECIPIENT, PROPERTY_MANAGER.getProperty("SMTP_USERNAME"), PROPERTY_MANAGER.getProperty("SMTP_PASSWORD"));
        mailServerAll.start();

        pop3 = new POP3MailConnector();
        imap = new ImapMailConnector();
        smtp = new SMTPMailConnector();

        session = smtp.getSession();
    }

    /**
     * Saves a message to file, reloads it from that file and ensures that headers and content of the saved message and
     * loaded message are equal.
     *
     * @throws Exception if there was an error while retrieving the message content
     */
    @Test
    public void testT01_saveAndLoadMessage() throws Exception {

        final String subject = STR_MAIL_SUBJECT + "testT01_saveAndLoadMessage";
        final String pathMail = PATH_HOME + "/out/test/resources/mail.eml";

        // SETUP - Create message.
        final MimeMessage msg = createDefaultMessage(session, subject);

        // EXECUTION - Save and reload.
        MailUtils.saveEmail(msg, pathMail);
        final MimeMessage loadedMsg = MailUtils.loadEmailFile(pathMail);

        // TEST - Compare message headers for equality.
        final String[] headersSavedMsg = MailUtils.getEmailHeaders(msg);
        final String[] headersLoadedMsg = MailUtils.getEmailHeaders(loadedMsg);

        Assert.assertEquals(headersLoadedMsg, headersSavedMsg);
        Assert.assertEquals(loadedMsg.getContent(), msg.getContent());
    }

    /**
     * Tests the correct sending with an SMTPMailConnector (values from mailconnection.properties) and reading with a
     * POP3MailConnector.
     */
    @Test
    public void testT02_sendAndWaitForMessageWithoutAttachment() throws MessagingException {

        final String subject = STR_MAIL_SUBJECT + "testT02_sendAndWaitForMessage";

        // SETUP - Create message.
        final MimeMessage msg = createDefaultMessage(session, subject);

        // EXECUTION - Send and receive message.
        smtp.sendMessage(msg);
        final Email receivedMsg = waitForMessage(subject);

        // TEST - Compare sent message with received message (content & headers).
        boolean areContentsEqual = MailUtils.compareSentAndReceivedEmailContents(msg, receivedMsg);
        boolean areHeadersEqual = MailUtils.compareSentAndReceivedEmailHeaders(msg, receivedMsg.getMessage());

        Assert.assertTrue(areContentsEqual, ERR_CONTENT_DIFFERS);
        Assert.assertTrue(areHeadersEqual, ERR_HEADERS_DIFFER);

        // CLEAN UP - Delete message.
        deleteMessage(receivedMsg, pop3);
    }

    /**
     * Tests the correct creating and sending of mails with a plain text attachment.
     *
     * @throws Exception if there was an error while sending/receiving the messages.
     */
    @Test
    public void testT03_sendAndWaitForMessageWithPlainTextAttachment() throws Exception {
        final String fileName = "attachment.txt";
        final String subject = STR_MAIL_SUBJECT + "testT03_sendAndWaitForMessageWithPlainTextAttachment";
        final File attachmentFile = FileUtils.getResourceFile(fileName);
        String fileContent = FileUtils.readFileToString(attachmentFile, StandardCharsets.US_ASCII);

        // SETUP - Create message, add attachment.
        final MimeMessage msg = this.createDefaultMessage(session, subject);
        final MimeBodyPart attachment = smtp.createAttachment(attachmentFile);
        final MimeBodyPart[] attachments = {attachment};
        smtp.addAttachmentsToMessage(attachments, msg);

        // EXECUTION - Send and receive message.
        smtp.sendMessage(msg);
        final Email receivedMsg = waitForMessage(subject);

        // TEST 1 - Fail, if the message contains no attachment (content is plain text).
        if (!(receivedMsg.getMessage().getContent() instanceof MimeMultipart)) {
            Assert.fail(ERR_NO_ATTACHMENT);
        }

        // TEST 2 - Check email text and attachment content.
        String messageText = receivedMsg.getMessageText();
        EmailAttachment receivedAttachment = receivedMsg.getAttachment(fileName);

        Assert.assertEquals(messageText, STR_MAIL_TEXT);
        Assert.assertEquals(receivedAttachment.getContent(), fileContent);

        // CLEAN UP - Delete message.
        deleteMessage(receivedMsg, pop3);
    }

    @DataProvider(name = "binaryAttachmentFiles")
    public Object[][] dataProvider() {
        return new Object[][] {
                {"attachment.pdf"},
                {"attachment.jpg"},
                {"attachment.png"},
                {"attachment.zip"}
        };
    }

    /**
     * Tests the correct creating and sending of mails with binary attachments.
     *
     * @param fileName name of the resource file
     * @throws Exception if there was an error while sending/receiving the messages.
     */
    @Test(dataProvider = "binaryAttachmentFiles")
    public void testT14_sendAndWaitForMessageWithBinaryAttachment(String fileName) throws Exception {
        final String subject = STR_MAIL_SUBJECT + "testT14_sendAndWaitForMessageWithBinaryAttachment";
        final File sentAttachmentFile = FileUtils.getResourceFile(fileName);

        // SETUP - Create message, add attachment.
        final MimeMessage msg = this.createDefaultMessage(session, subject);
        final MimeBodyPart attachment = smtp.createAttachment(sentAttachmentFile);
        final MimeBodyPart[] attachments = {attachment};
        smtp.addAttachmentsToMessage(attachments, msg);

        // EXECUTION - Send and receive message.
        smtp.sendMessage(msg);
        final Email receivedMsg = waitForMessage(subject);

        // TEST 1 - Fail, if the message contains no attachment (content is plain text).
        if (!(receivedMsg.getMessage().getContent() instanceof MimeMultipart)) {
            Assert.fail(ERR_NO_ATTACHMENT);
        }

        // TEST 2 - Check email text and attachment file.
        String text = receivedMsg.getMessageText();
        EmailAttachment receivedAttachment = receivedMsg.getAttachment(fileName);
        File savedAttachment = receivedAttachment.saveAsFile();

        Assert.assertEquals(text, STR_MAIL_TEXT);
        Assert.assertTrue(FileUtils.contentEquals(sentAttachmentFile, savedAttachment));

        // CLEAN UP - Delete saved file and message.
        if (!savedAttachment.delete()) {
            log().warn(String.format("File >%s< couldn't be deleted. Please remove file manually.",
                    savedAttachment.getAbsolutePath()));
        }
        deleteMessage(receivedMsg, pop3);
    }

    /**
     * Tests the correct creating and sending of mails, encrypted with a key store file.
     */
    @Test
    public void testT04_sendAndWaitForMessageEncryptedWithKeyStore() throws Exception {

        final String subject = STR_MAIL_SUBJECT + "testT04_sendAndWaitForMessageEncryptedWithKeyStore";
        final File resourceFile = FileUtils.getResourceFile("cacert.p12");
        final String pahtKeyStore = resourceFile.getAbsolutePath();
        final String password = "123456";

        final MimeMessage msg = createDefaultMessage(session, subject);

        // EXECUTION 1 - Encrypt message.
        final MimeMessage encryptedMsg = MailUtils.encryptMessageWithKeystore(msg, session, pahtKeyStore, password);

        // TEST 1 - Check encryption.
        final String expContentType = "application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data";
        final String actContentType = encryptedMsg.getContentType();
        Assert.assertEquals(actContentType, expContentType);

        // EXECUTION 2 - Send and retrieve encrypted message.
        smtp.sendMessage(encryptedMsg);
        final Email receivedMsg = waitForMessage(subject);

        // TEST 2 - Content should be encrypted (not equal to original message).
        final boolean areContentsEqual = MailUtils.compareSentAndReceivedEmailContents(msg, receivedMsg);
        Assert.assertFalse(areContentsEqual);

        // EXECUTION 3 - Decrypt message.
        final MimeMessage decryptedMsg = MailUtils.decryptMessageWithKeystore(encryptedMsg, session, pahtKeyStore,
                password);

        // TEST 3 - Check decryption.
        final String actualText = ((Multipart) decryptedMsg.getContent()).getBodyPart(0).getContent().toString();
        Assert.assertEquals(actualText, STR_MAIL_TEXT);
        Assert.assertTrue(decryptedMsg.getContentType().contains("multipart/mixed"));

        // CLEAN UP - Delete message.
        deleteMessage(receivedMsg, pop3);
    }

    /**
     * Tests the correct creating and sending of mails, encrypted with a certificate.
     *
     * @throws Exception if there was an error while sending/receiving the messages.
     */
    @Test(enabled = false)
    public void testT05_sendAndWaitForMessageEncryptedWithCertificate() throws Exception {

        final String subject = STR_MAIL_SUBJECT + "testT05_sendAndWaitForMessageEncryptedWithCertificate";

        final File testCertificateFile = FileUtils.getResourceFile("test_certificate.cer");

        // SETUP - Create message.
        final MimeMessage msg = createDefaultMessage(session, subject);

        // EXECUTION 1 - Encrypt message.
        final MimeMessage encryptedMsg = MailUtils.encryptMessageWithCert(msg, session, testCertificateFile.getAbsolutePath());

        // TEST 1 - Check encryption.
        final String expContentType = "application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data";
        final String actContentType = encryptedMsg.getContentType();
        Assert.assertEquals(actContentType, expContentType);

        // EXECUTION 2 - Send and retrieve encrypted message.
        smtp.sendMessage(encryptedMsg);
        final Email receivedMsg = waitForMessage(subject);

        // TEST 2 - Content should be encrypted (not equal to original message).
        final boolean areContentsEqual = MailUtils.compareSentAndReceivedEmailContents(msg, receivedMsg);
        Assert.assertFalse(areContentsEqual);

        // CLEAN UP - Delete message.
        deleteMessage(receivedMsg, pop3);
        // Decryption functionality has not been implemented, thus cannot be tested.
    }

    /**
     * Tests the correct sending with SMTPMailConnector (Values from mailconnection.properties) and reading with
     * POP3MailConnector.
     */
    @Test
    public void testT06_sendAndWaitFromSSLMessage() throws MessagingException {

        final String subject = STR_MAIL_SUBJECT + "testT06_sendAndWaitForSSLMessage";
        final String sslPortPop3 = PROPERTY_MANAGER.getProperty("POP3_SERVER_PORT_SSL", null);
        final String sslPortSmtp = PROPERTY_MANAGER.getProperty("SMTP_SERVER_PORT_SSL", null);

        // SETUP 1 - Create SSL connectors and message.
        final SMTPMailConnector smtpSSL = new SMTPMailConnector();
        smtpSSL.setSslEnabled(true);
        smtpSSL.setPort(sslPortSmtp);

        System.setProperty("SMTP_SERVER_PORT", sslPortSmtp);
        System.setProperty("SMTP_SSL_ENABLED", "true");

        POP3MailConnector pop3SSL = new POP3MailConnector();
        pop3SSL.setPort(sslPortPop3);
        pop3SSL.setSslEnabled(true);

        System.setProperty("POP3_SERVER_PORT", sslPortPop3);
        System.setProperty("POP3_SSL_ENABLED", "true");

        // SETUP 2 - Create message.
        final MimeMessage msg = this.createDefaultMessage(smtpSSL.getSession(), subject);

        // EXECUTION - Send and receive message.
        smtpSSL.sendMessage(msg);
        final Email receivedMsg = waitForMessage(subject);

        // TEST - Compare sent message with received message (content & headers).
        final boolean areContentsEqual = MailUtils.compareSentAndReceivedEmailContents(msg, receivedMsg);
        final boolean areHeadersEqual = MailUtils.compareSentAndReceivedEmailHeaders(msg,
                receivedMsg.getMessage());

        Assert.assertTrue(areContentsEqual, ERR_CONTENT_DIFFERS);
        Assert.assertTrue(areHeadersEqual, ERR_HEADERS_DIFFER);

        // CLEAN UP 1 - Delete message.
        deleteMessage(receivedMsg, pop3SSL);

        // CLEAN UP 2 - Reset Configuration.
        System.clearProperty("SMTP_SERVER_PORT");
        System.clearProperty("SMTP_SSL_ENABLED");
        System.clearProperty("POP3_SERVER_PORT");
        System.clearProperty("POP3_SSL_ENABLED");
    }

    /**
     * Tests the signing of a message with keystore.
     */
    @Test
    public void testT07_signMessage() {

        final File resourceFile = FileUtils.getResourceFile("cacert.p12");
        final String pathKeyStore = resourceFile.getAbsolutePath();
        final String password = "123456";

        try {
            // SETUP - Create message.
            final MimeMessage msg = this.createDefaultMessage(session, STR_MAIL_SUBJECT + "testT07_signMessage");

            // EXECUTION - Sign message.
            final MimeMessage signedMsg = MailUtils.signMessageWithKeystore(msg, session, pathKeyStore, password);

            // TEST 1 - Check content.
            final String expectedContent = "multipart/signed; protocol=\"application/pkcs7-signature\"; micalg=sha-1;";
            final boolean msgContainsExpContent = signedMsg.getContentType().contains(expectedContent);
            Assert.assertTrue(msgContainsExpContent, "Message Content as is expected: " + expectedContent + " but actual is: "
                    + signedMsg.getContentType());

            // TEST 2 - Check individual parts.
            final Multipart content = (Multipart) signedMsg.getContent();
            for (int i = 0; i < content.getCount(); i++) {
                final Part part = content.getBodyPart(i);
                if (part.getFileName() != null) {
                    Assert.assertEquals(part.getFileName(), "smime.p7s");
                }
            }
        } catch (final Exception e) {

            log().error(e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test for mailconnector.
     *
     * @throws Exception Exception by mail submission
     */
    @Test
    public void testT08_sendAndWaitForMessageWithoutAttachment_SubjectSenderRecipient() throws Exception {

        final String subject = "testT08_sendAndWaitForMessageWithoutAttachment_SubjectSenderRecipient"
                + RandomStringUtils.random(5, true, false);

        final SearchTerm searchTerm = new AndTerm(new SearchTerm[]{
                new SubjectTerm(subject),
                new FromTerm(new InternetAddress(SENDER)),
                new RecipientTerm(RecipientType.TO, new InternetAddress(RECIPIENT))
        });

        sendAndWaitForMessageWithoutAttachment(subject, searchTerm);
    }

    /**
     * Test for mailconnector.
     *
     * @throws Exception Exception by mail submission
     */
    @Test
    public void testT09_sendAndWaitForMessageWithoutAttachment_SubjectRecipient() throws Exception {

        final String subject = "testT09_sendAndWaitForMessageWithoutAttachment_SubjectRecipient"
                + RandomStringUtils.random(5, true, false);

        final SearchTerm searchTerm = new AndTerm(
                new SubjectTerm(subject),
                new FromTerm(new InternetAddress(SENDER)));

        sendAndWaitForMessageWithoutAttachment(subject, searchTerm);
    }

    /**
     * Test for mailconnector.
     *
     * @throws Exception Exception by mail submission
     */
    @Test
    public void testT10_sendAndWaitForMessageWithoutAttachment_SubjectSender() throws Exception {

        final String subject = "testT10_sendAndWaitForMessageWithoutAttachment_SubjectSender"
                + RandomStringUtils.random(5, true, false);

        final SearchTerm searchTerm = new AndTerm(
                new SubjectTerm(subject),
                new FromTerm(new InternetAddress(SENDER)));

        sendAndWaitForMessageWithoutAttachment(subject, searchTerm);
    }

    /**
     * Test for mailconnector.
     *
     * @throws Exception Exception by mail submission
     */
    @Test
    public void testT12_sendAndWaitForMessageWithoutAttachment_SubjectSentDate() throws Exception {

        final String subject = "testT12_sendAndWaitForMessageWithoutAttachment_SubjectSentDate";

        Date now = new Date();
        Date aMinuteBefore = new Date(now.getTime() - (1000 * 60));
        Assert.assertTrue(aMinuteBefore.before(now), "SentDate is not before now");

        final SearchTerm searchTerm = new AndTerm(
                new SubjectTerm(subject),
                new SentDateTerm(ComparisonTerm.GE, aMinuteBefore)
        );

        sendAndWaitForMessageWithoutAttachment(subject, searchTerm);
    }

    /**
     * T11_deleteAllMessages
     * <p/>
     * Description: T11 deleteAllMessages
     */
    @Test
    public void testT11_deleteAllMessages() {
        final String subjectFirstMessage = STR_MAIL_SUBJECT + "testT11_deleteAllMessages_FirstMail";
        final String subjectSecondMessage = STR_MAIL_SUBJECT + "testT11_deleteAllMessages_SecondMail";

        // create messages
        final MimeMessage msg1 = this.createDefaultMessage(session, subjectFirstMessage);
        final MimeMessage msg2 = this.createDefaultMessage(session, subjectSecondMessage);

        // send messages
        smtp.sendMessage(msg1);
        smtp.sendMessage(msg2);

        // verify Mail Box contains 2 Mails for deletion
        Assert.assertTrue(pop3.getMessageCount() > 1 , "Mail box contains mails for deletion");

        // delete all messages
        pop3.deleteAllMessages();

        // verify empty inbox
        final boolean inboxEmpty = (pop3.getMessageCount() == 0);
        Assert.assertTrue(inboxEmpty, "Mail box is empty");
    }

    @Test
    public void testT13_moveMessage() throws MessagingException {

        final String targetFolder = "MOVED";
        final String mailSubject = "testT12_moveMessage";

        // send Mail
        final MimeMessage msg = this.createDefaultMessage(smtp.getSession(), mailSubject);
        smtp.sendMessage(msg);

        // verify receive
        final Email receivedMsg = waitForMessage(mailSubject, imap);

        // move Mail
        final int amountOfMovedMessages = imap.moveMessage(targetFolder, new SubjectTerm(mailSubject));

        // moved exactly one mail
        Assert.assertEquals(amountOfMovedMessages, 1, "Mail moved");

        final Email movedMessage = waitForMessage(new SubjectTerm(mailSubject), imap, targetFolder);

        final boolean areContentsEqual = MailUtils.compareSentAndReceivedEmailContents(receivedMsg.getMessage(), movedMessage);
        Assert.assertTrue(areContentsEqual, ERR_CONTENT_DIFFERS);

        deleteMessage(receivedMsg, imap, targetFolder);
    }

    private void sendAndWaitForMessageWithoutAttachment(final String testname,
                                                        final SearchTerm searchTerm) throws MessagingException, IOException {

        final String subject = STR_MAIL_SUBJECT + testname;

        // SETUP - Create message.
        final MimeMessage msg = createDefaultMessage(session, subject);

        // EXECUTION - Send and receive message.
        smtp.sendMessage(msg);
        final Email receivedMsg = waitForMessage(searchTerm);

        // get content
        receivedMsg.getMessage().getContent();

        // TEST - Compare sent message with received message (content & headers).
        final boolean areContentsEqual = MailUtils.compareSentAndReceivedEmailContents(msg, receivedMsg);
        final boolean areHeadersEqual = MailUtils.compareSentAndReceivedEmailHeaders(msg,
                receivedMsg.getMessage());

        Assert.assertTrue(areContentsEqual, ERR_CONTENT_DIFFERS);
        Assert.assertTrue(areHeadersEqual, ERR_HEADERS_DIFFER);

        // CLEAN UP - Delete message.
        deleteMessage(receivedMsg, pop3);
    }

    /**
     * Creates a default test message.
     *
     * @param mailSession Session to be used for Mail.
     * @param subject Subject of mail.
     * @return MimeMessage object.
     */
    private MimeMessage createDefaultMessage(Session mailSession, String subject) {

        final MimeMessage msg = new MimeMessage(mailSession);
        try {
            msg.addRecipients(RecipientType.TO, RECIPIENT);
            msg.addFrom(new Address[]{new InternetAddress(SENDER)});
            msg.setSubject(subject);
            msg.setText(STR_MAIL_TEXT);
        } catch (MessagingException e) {
            log().error(e.toString());
        }

        return msg;
    }

    /**
     * Waits until a message with a given subject was received.
     *
     * @param subject the subject to look for
     * @return the received TesterraMail-message
     * @throws AssertionError in case no message was received at all
     */
    private Email waitForMessage(String subject) throws AssertionError {

        final SearchTerm searchTerm = new SubjectTerm(subject);
        return waitForMessage(searchTerm, pop3);
    }

    private Email waitForMessage(final String subject, final AbstractInboxConnector abstractInboxConnector) throws AssertionError {

        final SearchTerm searchTerm = new SubjectTerm(subject);
        return waitForMessage(searchTerm, abstractInboxConnector);
    }

    /**
     * Waits until a message with a given subject was received.
     *
     * @param searchTerm
     * @return the received TesterraMail-message
     * @throws AssertionError in case no message was received at all
     */
    private Email waitForMessage(final SearchTerm searchTerm) throws AssertionError {
        return waitForMessage(searchTerm, pop3);
    }

    private Email waitForMessage(final SearchTerm searchTerm, final AbstractInboxConnector abstractInboxConnector, final String folderName) throws AssertionError {

        // TEST - Fail, if no message was received.
        final EmailQuery query = new EmailQuery()
                .setSearchTerm(searchTerm)
                .setFolderName(folderName);
        final List<Email> emailList = abstractInboxConnector.query(query).collect(Collectors.toList());
        ASSERT.assertTrue(!emailList.isEmpty(), "messages found.");

        return emailList.get(0);
    }

    private Email waitForMessage(final SearchTerm searchTerm, final AbstractInboxConnector abstractInboxConnector) throws AssertionError {
        return waitForMessage(searchTerm, abstractInboxConnector, abstractInboxConnector.getInboxFolder());
    }

    /**
     * Clean up method which deletes the message, which is passed as first parameter.
     *
     * @param msg the TesterraMail-message to delete
     * @param abstractInboxConnector mailclient to use.
     * @throws AssertionError if the inbox is not empty after deleting the message
     */
    private void deleteMessage(final Email msg, final AbstractInboxConnector abstractInboxConnector) throws AssertionError, AddressException {

        final String recipient = msg.getRecipients().get(0);
        final String subject = msg.getSubject();

        final SearchTerm searchTerm = new AndTerm(
                new SubjectTerm(subject),
                new RecipientTerm(RecipientType.TO, new InternetAddress(recipient)));

        final boolean messageDeleted = abstractInboxConnector.deleteMessage(searchTerm);
        Assert.assertTrue(messageDeleted, "Message deleted");
    }

    private void deleteMessage(final Email msg, final AbstractInboxConnector abstractInboxConnector, final String folderName) throws AssertionError, AddressException {
        final String recipient = msg.getRecipients().get(0);
        final String subject = msg.getSubject();

        final SearchTerm searchTerm = new AndTerm(
                new SubjectTerm(subject),
                new RecipientTerm(RecipientType.TO, new InternetAddress(recipient)));

        final boolean messageDeleted = abstractInboxConnector.deleteMessage(searchTerm, folderName);
        Assert.assertTrue(messageDeleted, "Message deleted");
    }

    @Test
    public void testPollingTimeout() {
        int initPause = 400;
        int initRetry = 3;
        EmailQuery query = new EmailQuery()
                .setPauseMs(initPause)
                .setRetryCount(initRetry)
                .setSearchTerm(new SubjectTerm("404: Not Found"));

        Assert.assertEquals(query.getPauseMs(), initPause);
        Assert.assertEquals(query.getRetryCount(), initRetry);

        long startTime = System.currentTimeMillis();
        Stream<Email> emails = this.imap.query(query);
        Assert.assertEquals(emails.count(), 0);
        long endTime = System.currentTimeMillis() - startTime;

        ASSERT.assertGreaterEqualThan(new BigDecimal(endTime),
                new BigDecimal(initPause * initRetry), "Invalid polling time");
    }
}
