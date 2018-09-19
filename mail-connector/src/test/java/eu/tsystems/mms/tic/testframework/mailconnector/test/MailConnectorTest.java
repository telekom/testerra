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
 * Created on 14.08.2012
 *
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.mailconnector.test;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.mailconnector.pop3.POP3MailConnector;
import eu.tsystems.mms.tic.testframework.mailconnector.smtp.SMTPMailConnector;
import eu.tsystems.mms.tic.testframework.mailconnector.util.MailUtils;
import eu.tsystems.mms.tic.testframework.mailconnector.util.MessageUtils;
import eu.tsystems.mms.tic.testframework.mailconnector.util.SearchCriteria;
import eu.tsystems.mms.tic.testframework.mailconnector.util.SearchCriteriaType;
import eu.tsystems.mms.tic.testframework.mailconnector.util.FennecMail;
import eu.tsystems.mms.tic.testframework.testing.FennecTest;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Integration Tests for FennecMailConnector.
 * 
 * @author mrgi, tbmi
 */
public class MailConnectorTest extends FennecTest {

    /*
    TODO: komplett überarbeiten mit lokalem Mail Server!!! pele - 21.12.2016
    Tests deaktiviert.
     */

    // CONSTANTS
    /** Constant PATH_HOME */
    private static final String PATH_HOME = System.getProperty("user.dir");
    /** Constant PATH_RES */
    private static final String PATH_RES = PATH_HOME +  /*"/fennec-mailconnector" +*/ "/src/test/resources/";
    /** Constant STR_MAIL_SUBJECT */
    private static final String STR_MAIL_SUBJECT = "Test mail for TC: ";
    /** Constant STR_MAIL_TEXT */
    private static final String STR_MAIL_TEXT = "I am a test mail!";
    /** Constant ERR_CONTENT_DIFFERS */
    private static final String ERR_CONTENT_DIFFERS = "Content of sent and received message is not the same!";
    /** Constant ERR_HEADERS_DIFFER */
    private static final String ERR_HEADERS_DIFFER = "Headers of sent and received message are not the same!";
    /** Constant ERR_NO_MSG_RECEIVED */
    private static final String ERR_NO_MSG_RECEIVED = "No Message was received!";
    /** Constant ERR_NO_ATTACHMENT */
    private static final String ERR_NO_ATTACHMENT = "Message contains no attachment!";
    /** Constant RECIPIENT */
    private static final String RECIPIENT = "test@192.168.60.239";
    /** Constant SENDER */
    private static final String SENDER = "secret@host";

    // REFERENCES
    /** LOGGER */
    private static final Logger LOGGER = LoggerFactory.getLogger(MailConnectorTest.class);
    /** SMTPMailConnector */
    private SMTPMailConnector smtp; // Mail connector using the SMTP protocol
    /** POP3MailConnector */
    private POP3MailConnector pop3; // Mail connector using the POP3 protocol
    /** session */
    private Session session;

    /**
     * After every test-case the mail-box will be cleared
     */
    @AfterMethod
    public void clearMailBox(Method method) {
        pop3.deleteAllMessages();
        final boolean inboxEmpty = (pop3.getMessageCount() == 0);
        Assert.assertTrue(inboxEmpty, "Mail box is empty");

    }

    // Leeren des kompletten Postfaches

    // SETUP
    /**
     * Loads the mail connection properties and initializes the mail connector fields, as well as the smtp session.
     */
    @BeforeClass
    public void initProperties() {
        PropertyManager.loadProperties("mailconnection.properties");
        smtp = new SMTPMailConnector();
        pop3 = new POP3MailConnector();
        session = smtp.getSession();
    }

    // TEST CASES
    /**
     * Saves a message to file, reloads it from that file and ensures that headers and content of the saved message and
     * loaded message are equal.
     * 
     * @throws Exception if there was an error while retrieving the message content
     */
    @Test
    public void testT01_saveAndLoadMessage() throws Exception {
        final String subject = STR_MAIL_SUBJECT + "testT01_saveAndLoadMessage";
        final String pathMail = PATH_HOME + "/target/mail.eml";

        // SETUP - Create message.
        MimeMessage msg = createDefaultMessage(session, subject);

        // EXECUTION - Save and reload.
        MailUtils.saveEmail(msg, pathMail);
        MimeMessage loadedMsg = MailUtils.loadEmailFile(pathMail);

        // TEST - Compare message headers for equality.
        String[] headersSavedMsg = MessageUtils.getEmailHeaders(msg);
        String[] headersLoadedMsg = MessageUtils.getEmailHeaders(loadedMsg);

        Assert.assertEquals(headersLoadedMsg, headersSavedMsg);
        Assert.assertEquals(loadedMsg.getContent(), msg.getContent());
    }

    /**
     * Tests the correct sending with an SMTPMailConnector (values from mailconnection.properties) and reading with a
     * POP3MailConnector.
     * 
     * @throws Exception if there is en error during clean up
     */
    @Test
    public void testT02_sendAndWaitForMessageWithoutAttachement() throws Exception {
        final String subject = STR_MAIL_SUBJECT + "testT02_sendAndWaitForMessage";

        // SETUP - Create message.
        MimeMessage msg = createDefaultMessage(session, subject);

        // EXECUTION - Send and receive message.
        smtp.sendMessage(msg);
        FennecMail receivedMsg = waitForMessage(subject);

        // TEST - Compare sent message with received message (content & headers).

        boolean areContentsEqual = MailUtils.compareSentAndReceivedEmailContents(msg, receivedMsg);
        boolean areHeadersEqual = MailUtils.compareSentAndReceivedEmailHeaders(msg, receivedMsg.getMessage());

        Assert.assertTrue(areContentsEqual, ERR_CONTENT_DIFFERS);
        Assert.assertTrue(areHeadersEqual, ERR_HEADERS_DIFFER);

        // CLEAN UP - Delete message.
        deleteMessage(receivedMsg, pop3);
    }

    /**
     * Tests the correct creating and sending of mails with attachment.
     * 
     * @throws Exception if there was an error while sending/receiving the messages.
     */
    @Test
    public void testT03_sendAndWaitForMessageWithAttachment() throws Exception {
        final String subject = STR_MAIL_SUBJECT + "testT03_sendAndWaitForMessageWithAttachment";
        final String attachmentFile = "attachment.txt";

        // SETUP - Create message, add attachment.
        MimeMessage msg = this.createDefaultMessage(session, subject);
        MimeBodyPart attachment = smtp.createAttachment(new File(PATH_RES + attachmentFile));
        MimeBodyPart[] attachments = { attachment };
        smtp.addAttachmentsToMessage(attachments, msg);

        // EXECUTION - Send and receive message.
        smtp.sendMessage(msg);

        FennecMail receivedMsg = waitForMessage(subject);

        // TEST 1 - Fail, if the message contains no attachment (content is plain text).
        if (!(receivedMsg.getMessage().getContent() instanceof MimeMultipart)) {
            Assert.fail(ERR_NO_ATTACHMENT);
        }

        // TEST 2 - Check email text and attachment file name.
        Multipart content = (Multipart) receivedMsg.getMessage().getContent();
        int contentCnt = content.getCount();
        String attachmentFileName = null;
        String text = null;

        for (int i = 0; i < contentCnt; i++) {
            Part part = content.getBodyPart(i);

            // Retrieve email text.
            if (part.getDisposition().equals(Part.INLINE)) {
                text = part.getContent().toString();
            }

            // Retrieve attachment.
            else if (part.getDisposition().equals(Part.ATTACHMENT)) {
                attachmentFileName = part.getFileName();
            }
        }

        Assert.assertEquals(text, STR_MAIL_TEXT);
        Assert.assertEquals(attachmentFileName, attachmentFile);

        // CLEAN UP - Delete message.

        deleteMessage(receivedMsg, pop3);
    }

    /**
     * Tests the correct creating and sending of mails, encrypted with a key store file.
     * 
     * @throws Exception if there was an error while sending/receiving the messages.
     */
    @Test(enabled = false)
    public void testT04_sendAndWaitForMessageEncryptedWithKeyStore() throws Exception {
        final String subject = STR_MAIL_SUBJECT + "testT04_sendAndWaitForMessageEncryptedWithKeyStore";
        final String pahtKeyStore = PATH_RES + "cacert.p12";
        final String password = "mastest";
        MimeMessage msg = createDefaultMessage(session, subject);

        // EXECUTION 1 - Encrypt message.
        MimeMessage encryptedMsg = MailUtils.encryptMessageWithKeystore(msg, session, pahtKeyStore, password);

        // TEST 1 - Check encryption.
        final String expContentType = "application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data";
        final String actContentType = encryptedMsg.getContentType();
        Assert.assertEquals(actContentType, expContentType);

        // EXECUTION 2 - Send and retrieve encrypted message.
        smtp.sendMessage(encryptedMsg);
        FennecMail receivedMsg = waitForMessage(subject);

        // TEST 2 - Content should be encrypted (not equal to original message).
        boolean areContentsEqual = MailUtils.compareSentAndReceivedEmailContents(msg, receivedMsg);
        Assert.assertFalse(areContentsEqual);

        // EXECUTION 3 - Decrypt message.
        MimeMessage decryptedMsg = MailUtils.decryptMessageWithKeystore(encryptedMsg, session, pahtKeyStore,
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
        final String pathCertificate = PATH_RES + "test_certificate.cer";

        // SETUP - Create message.
        MimeMessage msg = createDefaultMessage(session, subject);

        // EXECUTION 1 - Encrypt message.
        MimeMessage encryptedMsg = MailUtils.encryptMessageWithCert(msg, session, pathCertificate);

        // TEST 1 - Check encryption.
        final String expContentType = "application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data";
        final String actContentType = encryptedMsg.getContentType();
        Assert.assertEquals(actContentType, expContentType);

        // EXECUTION 2 - Send and retrieve encrypted message.
        smtp.sendMessage(encryptedMsg);
        FennecMail receivedMsg = waitForMessage(subject);

        // TEST 2 - Content should be encrypted (not equal to original message).
        boolean areContentsEqual = MailUtils.compareSentAndReceivedEmailContents(msg, receivedMsg);
        Assert.assertFalse(areContentsEqual);

        // CLEAN UP - Delete message.
        deleteMessage(receivedMsg, pop3);

        // Decryption functionality has not been implemented, thus cannot be tested.
    }

    /**
     * Tests the correct sending with SMTPMailConnector (Values from mailconnection.properties) and reading with
     * POP3MailConnector.
     * 
     * @throws Exception Messages could not be sent or retrieved. loading or saving Mail to file.
     */
    @Test
    public void testT06_sendAndWaitForSSLMessage() throws Exception {
        final String subject = STR_MAIL_SUBJECT + "testT06_sendAndWaitForSSLMessage";

        // SETUP 1 - Create SSL connectors and message.
        SMTPMailConnector smtpSSL = new SMTPMailConnector();
        smtpSSL.setSslEnabled(true);
        System.setProperty("SMTP_SERVER_PORT", "465");
        System.setProperty("SMTP_SSL_ENABLED", "true");

        POP3MailConnector pop3SSL = new POP3MailConnector();
        pop3SSL.setPort("995");
        pop3SSL.setSslEnabled(true);
        System.setProperty("POP3_SERVER_PORT", "995");
        System.setProperty("POP3_SSL_ENABLED", "true");

        // SETUP 2 - Create message.
        MimeMessage msg = this.createDefaultMessage(smtpSSL.getSession(), subject);

        // EXECUTION - Send and receive message.
        smtpSSL.sendMessage(msg);
        FennecMail receivedMsg = waitForMessage(subject);

        // TEST - Compare sent message with received message (content & headers).
        boolean areContentsEqual = MailUtils.compareSentAndReceivedEmailContents(msg, receivedMsg);
        boolean areHeadersEqual = MailUtils.compareSentAndReceivedEmailHeaders(msg,
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
     * 
     * @throws Exception Message could not be signed.
     */
    @Test
    public void testT07_signMessage() throws Exception {
        final String pathKeyStore = PATH_RES + "cacert.p12";
        final String pasword = "mastest";

        try {
            // SETUP - Create message.
            MimeMessage msg = this.createDefaultMessage(session, STR_MAIL_SUBJECT + "testT07_signMessage");

            // EXECUTION - Sign message.
            MimeMessage signedMsg = MailUtils.signMessageWithKeystore(msg, session, pathKeyStore, pasword);

            // TEST 1 - Check content.
            String expectedContent = "multipart/signed; protocol=\"application/pkcs7-signature\"; micalg=sha-1;";
            boolean msgContainsExpContent = signedMsg.getContentType().contains(expectedContent);
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
            LOGGER.error(e.getMessage());
            Assert.assertTrue(false, e.getMessage());
        }
    }

    /**
     * Test for mailconnector.
     * 
     * @throws Exception Exception by mail submission
     */
    @Test
    public void testT08_sendAndWaitForMessageWithoutAttachement_SubjectSenderRecipient() throws Exception {
        String subject = "testT08_sendAndWaitForMessageWithoutAttachement_SubjectSenderRecipient"
                + StringUtils.getRandomStringWithLength(5);

        final List<SearchCriteria> searchCriterias = new ArrayList<SearchCriteria>();
        searchCriterias.add(new SearchCriteria(SearchCriteriaType.SUBJECT, subject));
        searchCriterias.add(new SearchCriteria(SearchCriteriaType.SENDER, SENDER));
        searchCriterias.add(new SearchCriteria(SearchCriteriaType.RECIPIENT, RECIPIENT));

        sendAndWaitForMessageWithoutAttachement(subject, searchCriterias);
    }

    /**
     * Test for mailconnector.
     * 
     * @throws Exception Exception by mail submission
     */
    @Test
    public void testT09_sendAndWaitForMessageWithoutAttachement_SubjectRecipient() throws Exception {
        String subject = "testT09_sendAndWaitForMessageWithoutAttachement_SubjectRecipient"
                + StringUtils.getRandomStringWithLength(5);

        final List<SearchCriteria> searchCriterias = new ArrayList<SearchCriteria>();
        searchCriterias.add(new SearchCriteria(SearchCriteriaType.SUBJECT, subject));
        searchCriterias.add(new SearchCriteria(SearchCriteriaType.SENDER, SENDER));

        sendAndWaitForMessageWithoutAttachement(subject, searchCriterias);
    }

    /**
     * Test for mailconnector.
     * 
     * @throws Exception Exception by mail submission
     */
    @Test
    public void testT10_sendAndWaitForMessageWithoutAttachement_SubjectSender() throws Exception {
        String subject = "testT10_sendAndWaitForMessageWithoutAttachement_SubjectSender"
                + StringUtils.getRandomStringWithLength(5);

        final List<SearchCriteria> searchCriterias = new ArrayList<SearchCriteria>();
        searchCriterias.add(new SearchCriteria(SearchCriteriaType.SUBJECT, subject));
        searchCriterias.add(new SearchCriteria(SearchCriteriaType.SENDER, SENDER));

        sendAndWaitForMessageWithoutAttachement(subject, searchCriterias);
    }

    /**
     * Test for mailconnector.
     * 
     * @throws Exception Exception by mail submission
     */
    @Test
    public void testT12_sendAndWaitForMessageWithoutAttachement_SubjectSentDate() throws Exception {
        String subject = "testT10_sendAndWaitForMessageWithoutAttachement_SubjectSender";

        final List<SearchCriteria> searchCriterias = new ArrayList<SearchCriteria>();
        searchCriterias.add(new SearchCriteria(SearchCriteriaType.SUBJECT, subject));
        searchCriterias.add(new SearchCriteria(SearchCriteriaType.AFTER_DATE, new Date()));

        sendAndWaitForMessageWithoutAttachement(subject, searchCriterias);
    }

    /**
     * T11_deleteAllMessages
     * <p/>
     * Description: T11 deleteAllMessages
     */
    @Test
    public void testT11_deleteAllMessages() {
        pop3.deleteMessage(null, null, null, null);
        final boolean inboxEmpty = (pop3.getMessageCount() == 0);
        Assert.assertTrue(inboxEmpty, "Mail box is empty");
    }

    private void sendAndWaitForMessageWithoutAttachement(final String testname,
                                                         final List<SearchCriteria> searchCriterias) throws MessagingException, IOException {
        final String subject = STR_MAIL_SUBJECT + testname;

        // SETUP - Create message.
        MimeMessage msg = createDefaultMessage(session, subject);

        // EXECUTION - Send and receive message.
        smtp.sendMessage(msg);
        FennecMail receivedMsg = waitForMessage(searchCriterias);

        // get content
        receivedMsg.getMessage().getContent();

        // TEST - Compare sent message with received message (content & headers).
        boolean areContentsEqual = MailUtils.compareSentAndReceivedEmailContents(msg, receivedMsg);
        boolean areHeadersEqual = MailUtils.compareSentAndReceivedEmailHeaders(msg,
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
        MimeMessage msg = new MimeMessage(mailSession);
        try {
            msg.addRecipients(RecipientType.TO, RECIPIENT);
            msg.addFrom(new Address[] { new InternetAddress(SENDER) });
            msg.setSubject(subject);
            msg.setText(STR_MAIL_TEXT);
        } catch (MessagingException e) {
            LOGGER.error(e.toString());
        }

        return msg;
    }

    /**
     * Waits until a message with a given subject was received.
     * 
     * @param subject the subject to look for
     * @throws AssertionError in case no message was received at all
     * @return the received FennecMail-message
     */
    private FennecMail waitForMessage(String subject) throws AssertionError {
        FennecMail receivedMsg = null;

        // TEST - Fail, if no message was received.
        try {
            receivedMsg = pop3.waitForFennecMail(subject);
        } catch (Exception e) {
            Assert.fail(ERR_NO_MSG_RECEIVED);
        }

        return receivedMsg;
    }

    /**
     * Waits until a message with a given subject was received.
     * 
     * @param searchCriterias .
     * @throws AssertionError in case no message was received at all
     * @return the received FennecMail-message
     */
    private FennecMail waitForMessage(final List<SearchCriteria> searchCriterias) throws AssertionError {
        FennecMail receivedMsg = null;

        // TEST - Fail, if no message was received.
        try {
            receivedMsg = pop3.waitForFennecMail(searchCriterias);
        } catch (Exception e) {
            Assert.fail(ERR_NO_MSG_RECEIVED);
        }

        return receivedMsg;
    }

    /**
     * Clean up method which deletes the message, which is passed as first parameter.
     * 
     * @param msg the FennecMail-message to delete
     * @param pop3Instance mailclient to use.
     * @throws AssertionError if the inbox is not empty after deleting the message
     * @throws MessagingException if there is an error while retrieving the recipient or subject
     */
    private void deleteMessage(FennecMail msg, POP3MailConnector pop3Instance) throws AssertionError, MessagingException {
        RecipientType to = RecipientType.TO;
        String recipient = msg.getRecipients().get(0);
        String subject = msg.getSubject();
        String messageId = null;

        pop3Instance.deleteMessage(recipient, to, subject, messageId);

        final boolean inboxEmpty = (pop3Instance.getMessageCount() == 0);
        Assert.assertTrue(inboxEmpty, "Mail box is empty");
    }
}
