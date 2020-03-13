
package eu.tsystems.mms.tic.testframework.mailconnector.test;

import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.mailconnector.pop3.POP3MailConnector;
import eu.tsystems.mms.tic.testframework.mailconnector.smtp.SMTPMailConnector;
import eu.tsystems.mms.tic.testframework.mailconnector.util.MailUtils;
import eu.tsystems.mms.tic.testframework.mailconnector.util.MessageUtils;
import eu.tsystems.mms.tic.testframework.mailconnector.util.SearchCriteria;
import eu.tsystems.mms.tic.testframework.mailconnector.util.SearchCriteriaType;
import eu.tsystems.mms.tic.testframework.mailconnector.util.Email;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
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
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Integration Tests for TesterraMailConnector.
 *
 * @author mrgi, tbmi
 */
public class MailConnectorTest extends TesterraTest {

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
     * LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MailConnectorTest.class);

    /**
     * SMTPMailConnector
     */
    private SMTPMailConnector smtp; // Mail connector using the SMTP protocol

    /**
     * POP3MailConnector
     */
    private POP3MailConnector pop3; // Mail connector using the POP3 protocol

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

        final boolean inboxEmpty = (pop3.getMessageCount() == 0);
        Assert.assertTrue(inboxEmpty, "Mail box is empty");
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

        PropertyManager.loadProperties("mailconnection.properties");

        Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
        mailServerAll = new GreenMail(ServerSetupTest.ALL);
        mailServerAll.setUser(RECIPIENT, PropertyManager.getProperty("SMTP_USERNAME"), PropertyManager.getProperty("SMTP_PASSWORD"));
        mailServerAll.start();

        pop3 = new POP3MailConnector();
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
     */
    @Test
    public void testT02_sendAndWaitForMessageWithoutAttachement() {

        final String subject = STR_MAIL_SUBJECT + "testT02_sendAndWaitForMessage";

        // SETUP - Create message.
        MimeMessage msg = createDefaultMessage(session, subject);

        // EXECUTION - Send and receive message.
        smtp.sendMessage(msg);
        Email receivedMsg = waitForMessage(subject);

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
        final File attachmentFile = FileUtils.getResourceFile("attachment.txt");

        // SETUP - Create message, add attachment.
        MimeMessage msg = this.createDefaultMessage(session, subject);
        MimeBodyPart attachment = smtp.createAttachment(attachmentFile);
        MimeBodyPart[] attachments = {attachment};
        smtp.addAttachmentsToMessage(attachments, msg);

        // EXECUTION - Send and receive message.
        smtp.sendMessage(msg);

        Email receivedMsg = waitForMessage(subject);

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
        Assert.assertEquals(attachmentFileName, attachmentFile.getName());

        // CLEAN UP - Delete message.

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
        MimeMessage msg = createDefaultMessage(session, subject);

        // EXECUTION 1 - Encrypt message.
        MimeMessage encryptedMsg = MailUtils.encryptMessageWithKeystore(msg, session, pahtKeyStore, password);

        // TEST 1 - Check encryption.
        final String expContentType = "application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data";
        final String actContentType = encryptedMsg.getContentType();
        Assert.assertEquals(actContentType, expContentType);

        // EXECUTION 2 - Send and retrieve encrypted message.
        smtp.sendMessage(encryptedMsg);
        Email receivedMsg = waitForMessage(subject);

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

        final File testCertificateFile = FileUtils.getResourceFile("test_certificate.cer");

        // SETUP - Create message.
        MimeMessage msg = createDefaultMessage(session, subject);

        // EXECUTION 1 - Encrypt message.
        MimeMessage encryptedMsg = MailUtils.encryptMessageWithCert(msg, session, testCertificateFile.getAbsolutePath());

        // TEST 1 - Check encryption.
        final String expContentType = "application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data";
        final String actContentType = encryptedMsg.getContentType();
        Assert.assertEquals(actContentType, expContentType);

        // EXECUTION 2 - Send and retrieve encrypted message.
        smtp.sendMessage(encryptedMsg);
        Email receivedMsg = waitForMessage(subject);

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
     */
    @Test
    public void testT06_sendAndWaitForSSLMessage() {

        final String subject = STR_MAIL_SUBJECT + "testT06_sendAndWaitForSSLMessage";
        final String sslPortPop3 = PropertyManager.getProperty("POP3_SERVER_PORT_SSL", null);
        final String sslPortSmtp = PropertyManager.getProperty("SMTP_SERVER_PORT_SSL", null);

        // SETUP 1 - Create SSL connectors and message.
        SMTPMailConnector smtpSSL = new SMTPMailConnector();
        smtpSSL.setSslEnabled(true);
        smtpSSL.setPort(sslPortSmtp);

        System.setProperty("SMTP_SERVER_PORT", sslPortSmtp);
        System.setProperty("SMTP_SSL_ENABLED", "true");

        POP3MailConnector pop3SSL = new POP3MailConnector();
        pop3SSL.setPort(sslPortPop3);
        pop3SSL.setSslEnabled(true);

        System.setProperty("POP3_SERVER_PORT", "3995");
        System.setProperty("POP3_SSL_ENABLED", "true");

        // SETUP 2 - Create message.
        MimeMessage msg = this.createDefaultMessage(smtpSSL.getSession(), subject);

        // EXECUTION - Send and receive message.
        smtpSSL.sendMessage(msg);
        Email receivedMsg = waitForMessage(subject);

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
     */
    @Test
    public void testT07_signMessage() {

        final File resourceFile = FileUtils.getResourceFile("cacert.p12");
        final String pathKeyStore = resourceFile.getAbsolutePath();
        final String password = "123456";

        try {
            // SETUP - Create message.
            MimeMessage msg = this.createDefaultMessage(session, STR_MAIL_SUBJECT + "testT07_signMessage");

            // EXECUTION - Sign message.
            MimeMessage signedMsg = MailUtils.signMessageWithKeystore(msg, session, pathKeyStore, password);

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
            Assert.fail(e.getMessage());
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

        final List<SearchCriteria> searchCriterias = new ArrayList<>();
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

        final List<SearchCriteria> searchCriterias = new ArrayList<>();
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

        final List<SearchCriteria> searchCriterias = new ArrayList<>();
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

        final List<SearchCriteria> searchCriterias = new ArrayList<>();
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
        Email receivedMsg = waitForMessage(searchCriterias);

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
     * @param subject     Subject of mail.
     * @return MimeMessage object.
     */
    private MimeMessage createDefaultMessage(Session mailSession, String subject) {

        MimeMessage msg = new MimeMessage(mailSession);
        try {
            msg.addRecipients(RecipientType.TO, RECIPIENT);
            msg.addFrom(new Address[]{new InternetAddress(SENDER)});
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
     * @return the received TesterraMail-message
     * @throws AssertionError in case no message was received at all
     */
    private Email waitForMessage(String subject) throws AssertionError {

        final List<SearchCriteria> searchCriteria = new ArrayList<>();
        searchCriteria.add(new SearchCriteria(SearchCriteriaType.SUBJECT, subject));
        return waitForMessage(searchCriteria);
    }

    /**
     * Waits until a message with a given subject was received.
     *
     * @param searchCriterias .
     * @return the received TesterraMail-message
     * @throws AssertionError in case no message was received at all
     */
    private Email waitForMessage(final List<SearchCriteria> searchCriterias) throws AssertionError {

        Email receivedMsg = null;

        // TEST - Fail, if no message was received.
        try {
            receivedMsg = pop3.waitForMails(searchCriterias).get(0);
        } catch (Exception e) {
            Assert.fail(ERR_NO_MSG_RECEIVED);
        }

        return receivedMsg;
    }

    /**
     * Clean up method which deletes the message, which is passed as first parameter.
     *
     * @param msg          the TesterraMail-message to delete
     * @param pop3Instance mailclient to use.
     * @throws AssertionError if the inbox is not empty after deleting the message
     */
    private void deleteMessage(Email msg, POP3MailConnector pop3Instance) throws AssertionError {
        RecipientType to = RecipientType.TO;
        String recipient = msg.getRecipients().get(0);
        String subject = msg.getSubject();
        String messageId = null;

        pop3Instance.deleteMessage(recipient, to, subject, messageId);

        final boolean inboxEmpty = (pop3Instance.getMessageCount() == 0);
        Assert.assertTrue(inboxEmpty, "Mail box is empty");
    }
}
