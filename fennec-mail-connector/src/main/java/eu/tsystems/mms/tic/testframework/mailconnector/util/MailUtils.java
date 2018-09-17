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
 * Created on 27.06.2012
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.mailconnector.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import net.iharder.Base64;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.asn1.smime.SMIMEEncryptionKeyPreferenceAttribute;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.mail.smime.examples.ExampleUtils;
import org.bouncycastle.operator.OutputEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.util.BASE64DecoderStream;

import eu.tsystems.mms.tic.testframework.exceptions.fennecSystemException;

/** Helper class, contains some utility methods. */
public final class MailUtils {

    /** The Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MailUtils.class);

    /** Private constructor to hide the public one since this is a static only class. */
    private MailUtils() {
    }

    /**
     * Encodes the String as base64.
     * 
     * @param password The String to encode.
     * @return The encoded String.
     */
    public static String encodeBase64(final String password) {
        return pEncodeBase64(password);
    }

    /**
     * Encodes the String as base64.
     * 
     * @param password The String to encode.
     * @return The encoded String.
     */
    private static String pEncodeBase64(final String password) {
        String encode = Base64.encodeBytes(password.getBytes());
        encode = encode.replaceAll("\n", "").replaceAll("\r", "");
        return encode;
    }

    /**
     * This method compares the Content of a sent and received email. It will be differ between an instance of
     * BASE64DecoderStream, MimeMultipart and String which is processed differently.
     * 
     * @param sent The sent message.
     * @param received The received message.
     * 
     * @return FALSE if the Content is not equal, the Content Types differs or the Content cannot be read.
     * @throws fennecSystemException thrown if exception during comparing is thrown.
     */
    public static boolean compareSentAndReceivedEmailContents(final MimeMessage sent, final fennecMail received)
            throws fennecSystemException {
        return pCompareSentAndReceivedEmailContents(sent, received);
    }

    /**
     * This method compares the Content of a sent and received email. It will be differ between an instance of
     * BASE64DecoderStream, MimeMultipart and String which is processed differently.
     * 
     * @param sent The sent message.
     * @param received The received message.
     * 
     * @return FALSE if the Content is not equal, the Content Types differs or the Content cannot be read.
     * @throws fennecSystemException thrown if exception during comparing is thrown.
     */
    public static boolean compareSentAndReceivedEmailContents(final MimeMessage sent, final Message received)
            throws fennecSystemException {
        return pCompareSentAndReceivedEmailContents(sent, received);
    }

    /**
     * This method compares the Content of a sent and received email. It will be differ between an instance of
     * BASE64DecoderStream, MimeMultipart and String which is processed differently.
     * 
     * @param sent The sent message.
     * @param received The received message.
     * 
     * @return FALSE if the Content is not equal, the Content Types differs or the Content cannot be read.
     * @throws fennecSystemException thrown if exception during comparing is thrown.
     */
    private static boolean pCompareSentAndReceivedEmailContents(final MimeMessage sent, final fennecMail received)
            throws fennecSystemException {

        try {
            // Checks if Content Type are equal.
            if (!sent.getContentType().equals(received.getMessage().getContentType())) {
                LOGGER.error("Content types not equal: " + sent.getContentType() + "<>"
                        + received.getMessage().getContentType());
                return false;
            }

            // Checks if the Content is an instance of BASE64DecoderStream
            else if (sent.getContent() instanceof BASE64DecoderStream) {
                LOGGER.info("Content is an instance of BASE64DecoderStream");

                final InputStream sentB64 = (InputStream) sent.getContent();
                final InputStream receivedB64 = (InputStream) received.getMessage().getContent();

                return org.apache.commons.io.IOUtils.contentEquals(sentB64, receivedB64);
            }

            // Checks if the Content is an instance of MimeMultipart
            else if (sent.getContent() instanceof MimeMultipart) {
                LOGGER.info("Content is an instance of MimeMultipart");

                /*
                 * Here the MimeMultipart has to be converted into an ImputStream, which can be read by the
                 * contentEquals-Method (org.apache.commons.io.IOUtils)
                 */

                final MimeMultipart sentMulti = (MimeMultipart) sent.getContent();
                final MimeMultipart receivedMulti = (MimeMultipart) received.getMessage().getContent();

                final ByteArrayOutputStream sentOutStream = new ByteArrayOutputStream();
                final ByteArrayOutputStream received2OutStream = new ByteArrayOutputStream();

                sentMulti.writeTo(sentOutStream);
                receivedMulti.writeTo(received2OutStream);

                final byte[] sentBytes = sentOutStream.toByteArray();
                final byte[] receivedBytes = received2OutStream.toByteArray();

                final ByteArrayInputStream sentInStream = new ByteArrayInputStream(sentBytes);
                final ByteArrayInputStream receivedInStream = new ByteArrayInputStream(receivedBytes);

                return org.apache.commons.io.IOUtils.contentEquals(sentInStream, receivedInStream);
            }

            // Checks if the Content is an instance of String
            else if (sent.getContent() instanceof String) {
                LOGGER.info("Content is an instance of String");

                final String sentStr = sent.getContent().toString().trim();
                final String receivedStr = received.getMessageText().trim();

                String contentType = sent.getContentType();
                contentType = contentType.split("charset=")[1];

                return java.util.Arrays.equals(sentStr.getBytes(contentType),
                        receivedStr.getBytes(contentType));
            }
            // The Content is unknown.
            LOGGER.error("Content type is unknown: " + sent.getContent());
        } catch (final Exception e) {
            throw new fennecSystemException("Error comparing messages.", e);
        }
        return false;
    }

    /**
     * This method compares the Content of a sent and received email. It will be differ between an instance of
     * BASE64DecoderStream, MimeMultipart and String which is processed differently.
     * 
     * @param sent The sent message.
     * @param received The received message.
     * 
     * @return FALSE if the Content is not equal, the Content Types differs or the Content cannot be read.
     * @throws fennecSystemException thrown if exception during comparing is thrown.
     */
    private static boolean pCompareSentAndReceivedEmailContents(final MimeMessage sent, final Message received)
            throws fennecSystemException {
        try {
            // Checks if Content Type are equal.
            if (!sent.getContentType().equals(received.getContentType())) {
                LOGGER.error("Content types not equal: " + sent.getContentType() + "<>" + received.getContentType());
                return false;
            }

            // Checks if the Content is an instance of BASE64DecoderStream
            else if (sent.getContent() instanceof BASE64DecoderStream) {
                LOGGER.info("Content is an instance of BASE64DecoderStream");

                final InputStream sentB64 = (InputStream) sent.getContent();
                final InputStream receivedB64 = (InputStream) received.getContent();

                return org.apache.commons.io.IOUtils.contentEquals(sentB64, receivedB64);
            }

            // Checks if the Content is an instance of MimeMultipart
            else if (sent.getContent() instanceof MimeMultipart) {
                LOGGER.info("Content is an instance of MimeMultipart");

                /*
                 * Here the MimeMultipart has to be converted into an ImputStream, which can be read by the
                 * contentEquals-Method (org.apache.commons.io.IOUtils)
                 */

                final MimeMultipart sentMulti = (MimeMultipart) sent.getContent();
                final MimeMultipart receivedMulti = (MimeMultipart) received.getContent();

                final ByteArrayOutputStream sentOutStream = new ByteArrayOutputStream();
                final ByteArrayOutputStream received2OutStream = new ByteArrayOutputStream();

                sentMulti.writeTo(sentOutStream);
                receivedMulti.writeTo(received2OutStream);

                final byte[] sentBytes = sentOutStream.toByteArray();
                final byte[] receivedBytes = received2OutStream.toByteArray();

                final ByteArrayInputStream sentInStream = new ByteArrayInputStream(sentBytes);
                final ByteArrayInputStream receivedInStream = new ByteArrayInputStream(receivedBytes);

                return org.apache.commons.io.IOUtils.contentEquals(sentInStream, receivedInStream);
            }

            // Checks if the Content is an instance of String
            else if (sent.getContent() instanceof String) {
                LOGGER.info("Content is an instance of String");

                final String sentStr = sent.getContent().toString().trim();
                final String receivedStr = received.getContent().toString().trim();

                String contentType = sent.getContentType();
                contentType = contentType.split("charset=")[1];

                return java.util.Arrays.equals(sentStr.getBytes(contentType), receivedStr.getBytes(contentType));
            }
            // The Content is unknown.
            LOGGER.error("Content type is unknown: " + sent.getContent());
        } catch (final Exception e) {
            throw new fennecSystemException("Error comparing messages.", e);
        }
        return false;
    }

    /**
     * Compares the header of two emails.
     * 
     * @param sent The sent message.
     * @param received The received message.
     * @return True if Headers match, else false.
     * @throws fennecSystemException thrown if exception during comparing is thrown.
     */
    public static boolean compareSentAndReceivedEmailHeaders(final Message sent, final Message received)
            throws fennecSystemException {
        return pCompareSentAndReceivedEmailHeaders(sent, received);
    }

    /**
     * Compares the header of two emails.
     * 
     * @param sent The sent message.
     * @param received The received message.
     * @return True if Headers match, else false.
     * @throws fennecSystemException thrown if exception during comparing is thrown.
     */
    private static boolean pCompareSentAndReceivedEmailHeaders(final Message sent, final Message received)
            throws fennecSystemException {
        try {
            final MimeMessage sendedMessage = (MimeMessage) sent;
            final MimeMessage receivedMessage = (MimeMessage) received;
            LOGGER.info("compareEmailwithSentEmailHeaders");
            final String[] relevantHeaders = { "From:", "To:", "Message-ID:", "Subject:",
                    "MIME-Version:", "Content-Type:", "Content-Transfer-Encoding:" };
            final Enumeration<?> headersSendedMessage = sendedMessage.getMatchingHeaderLines(relevantHeaders);
            final Enumeration<?> headersReceivedMessage = receivedMessage.getMatchingHeaderLines(relevantHeaders);
            while (headersSendedMessage.hasMoreElements() && headersReceivedMessage.hasMoreElements()) {
                final String headerSendedMessage = (String) headersSendedMessage.nextElement();
                final String headerReceivedMessage = (String) headersReceivedMessage.nextElement();
                LOGGER.info("Compare " + headerSendedMessage + " and " + headerReceivedMessage);
                if (headerSendedMessage != null && headerReceivedMessage != null) {
                    if (!headerSendedMessage.equals(headerReceivedMessage)) {
                        LOGGER.error("Headers are different. " + headerSendedMessage
                                + " doesn't match " + headerReceivedMessage);
                        return false;
                    }
                } else {
                    if (!headerSendedMessage.equals(headerReceivedMessage)) {
                        return false;
                    }
                }
            }
        } catch (final Exception e) {
            throw new fennecSystemException("Error comparing message headers.", e);
        }
        return true;
    }

    /**
     * Load an email file.
     * 
     * @param filename The path to the file to load.
     * @throws fennecSystemException thrown if mail can't load.
     * @return The loaded MimeMessage.
     */
    public static MimeMessage loadEmailFile(final String filename) throws fennecSystemException {
        return pLoadEmailFile(filename);
    }

    /**
     * Load an email file.
     * 
     * @param filename The path to the file to load.
     * @throws fennecSystemException thrown if mail can't load.
     * @return The loaded MimeMessage.
     */
    private static MimeMessage pLoadEmailFile(final String filename) throws fennecSystemException {
        MimeMessage message = null;
        try {
            final Session mailSession = Session.getInstance(new Properties());
            message = new MimeMessage(mailSession, new FileInputStream(filename));
            MessageUtils.multiPartBugfix(message);
        } catch (final Exception e) {
            throw new fennecSystemException("Error loading email.", e);
        }
        return message;
    }

    /**
     * Saves an email to file.
     * 
     * @param message The message to save.
     * @param filename The path to write the message.
     * @throws fennecSystemException thrown if message can't be saved.
     */
    public static void saveEmail(final Message message, final String filename) throws fennecSystemException {
        pSaveEmail(message, filename);
    }

    /**
     * Saves an email to file.
     * 
     * @param message The message to save.
     * @param filename The path to write the message.
     * @throws fennecSystemException thrown if message can't be saved.
     */
    private static void pSaveEmail(final Message message, final String filename) throws fennecSystemException {
        if (new File(filename).exists()) {
            new File(filename).delete();
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(filename);
            LOGGER.info("Writing email to file: " + filename);
            message.writeTo(fos);
            fos.close();
        } catch (final Exception e) {
            throw new fennecSystemException("Can't save Email.", e);
        }
    }

    /**
     * Saves an email to file.
     * 
     * @param message fennecMail The message to save.
     * @param filename The path to write the message.
     * @throws fennecSystemException thrown if message can't be saved.
     */
    public static void saveEmail(final fennecMail message, final String filename) throws fennecSystemException {
        pSaveEmailX(message, filename);
    }

    /**
     * Saves an email to file.
     * 
     * @param message fennecMail The message to save.
     * @param filename The path to write the message.
     * @throws fennecSystemException thrown if message can't be saved.
     */
    private static void pSaveEmailX(final fennecMail message, final String filename) throws fennecSystemException {
        if (new File(filename).exists()) {
            new File(filename).delete();
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(filename);
            LOGGER.info("Writing email to file: " + filename);
            message.getMessage().writeTo(fos);
            fos.close();
        } catch (final Exception e) {
            throw new fennecSystemException("Can't save Email.", e);
        }
    }

    /**
     * Loads an email with InputStream.
     * 
     * @param inputStream The InputStream to load the email.
     * @return The loaded message.
     * @throws fennecSystemException thrown if mail can't load.
     */
    public static MimeMessage loadEmail(final InputStream inputStream) throws fennecSystemException {
        return pLoadEmail(inputStream);
    }

    /**
     * Loads an email with InputStream.
     * 
     * @param inputStream The InputStream to load the email.
     * @return The loaded message.
     * @throws fennecSystemException thrown if mail can't load.
     */
    private static MimeMessage pLoadEmail(final InputStream inputStream) throws fennecSystemException {
        MimeMessage message = null;
        try {
            final Session mailSession = Session.getInstance(new Properties());
            message = new MimeMessage(mailSession, inputStream);
            MessageUtils.multiPartBugfix(message);
        } catch (final Exception e) {
            throw new fennecSystemException("Error loading email.", e);
        }
        return message;
    }

    /**
     * Displays the message.
     * 
     * @param message The message to display.
     * @throws fennecSystemException thrown if content of message can't read.
     */
    public static void displayMail(final Message message) throws fennecSystemException {
        pDisplayMail(message);
    }

    /**
     * Displays the message from file.
     * 
     * @param filename The path to message.
     * @throws fennecSystemException thrown if content of message can't read.
     */
    public static void displayMail(final String filename) throws fennecSystemException {
        pDisplayMail(loadEmailFile(filename));
    }

    /**
     * Displays the message.
     * 
     * @param message The message to display.
     * @throws fennecSystemException thrown if content of message can't read.
     */
    private static void pDisplayMail(final Message message) throws fennecSystemException {
        final MimeMessage mimeMessage = (MimeMessage) message;
        MessageUtils.multiPartBugfix(mimeMessage);
        try {
            final Enumeration<?> headerLines = mimeMessage.getAllHeaderLines();
            while (headerLines.hasMoreElements()) {
                LOGGER.info("Header: " + headerLines.nextElement().toString());
            }
            LOGGER.info("Content: " + mimeMessage.getContent().toString());
        } catch (final Exception e) {
            throw new fennecSystemException("Error get headers and content from message.", e);
        }
    }

    /**
     * Encrypt a MimeMessage with certification file.
     * 
     * @param message The message to encrypt.
     * @param mailSession The session for the message.
     * @param certFile The path to certification file.
     * 
     * @return The encrypted MimeMessage.
     * @throws fennecSystemException thrown if message can't encrypted
     */
    public static MimeMessage encryptMessageWithCert(final MimeMessage message, final Session mailSession,
            final String certFile) throws fennecSystemException {
        return pEncryptMessageWithCert(message, mailSession, certFile);
    }

    /**
     * Encrypt a MimeMessage with certification file.
     * 
     * @param message The message to encrypt.
     * @param mailSession The session for the message.
     * @param certFile The path to certification file.
     * 
     * @return The encrypted MimeMessage.
     * @throws fennecSystemException thrown if message can't encrypted
     */
    private static MimeMessage pEncryptMessageWithCert(final MimeMessage message, final Session mailSession,
            final String certFile) throws fennecSystemException {
        MessageUtils.multiPartBugfix(message);
        // load old mail headers
        Enumeration<?> headerEnum;

        MimeMessage newMessage = null;
        try {
            headerEnum = message.getAllHeaderLines();
            LOGGER.info("Encrypt message with certificate: " + certFile);
            // load cert
            final InputStream inputStream = new FileInputStream(certFile);
            final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            final X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(inputStream);
            inputStream.close();

            final SMIMEEnvelopedGenerator smeg = new SMIMEEnvelopedGenerator();
            final RecipientInfoGenerator recipientInfoGen = new JceKeyTransRecipientInfoGenerator(cert);
            smeg.addRecipientInfoGenerator(recipientInfoGen);
            message.saveChanges();
            final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            message.writeTo(byteOutputStream);
            final ByteArrayInputStream bis = new ByteArrayInputStream(byteOutputStream.toByteArray());
            final MimeBodyPart mbp = new MimeBodyPart(bis);

            final OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(
                    CMSAlgorithm.AES256_CBC).build();
            final MimeBodyPart encrypted = smeg.generate(mbp, encryptor);
            newMessage = new MimeMessage(
                    Session.getDefaultInstance(new Properties()));

            // set new mail content
            newMessage.setContent(encrypted.getContent(),
                    encrypted.getContentType());
            newMessage.saveChanges();

            final Enumeration<?> compEnum = newMessage.getAllHeaderLines();
            final ArrayList<String> headers = new ArrayList<String>();
            while (compEnum.hasMoreElements()) {
                headers.add((String) compEnum.nextElement());
            }
            while (headerEnum.hasMoreElements()) {
                final String headerline = (String) headerEnum.nextElement();
                final String hpart = headerline.split(":")[0];
                Boolean setHeader = true;
                for (final String s : headers) {
                    if (s.contains(hpart)) {
                        setHeader = false;
                        break;
                    }
                }
                if (setHeader) {
                    newMessage.addHeaderLine(headerline);
                    LOGGER.info("Setting " + hpart);
                } else {
                    LOGGER.info("Not setting " + hpart);
                }
            }
            newMessage.saveChanges();
        } catch (final Exception e) {
            throw new fennecSystemException("Can't encrypt message.", e);
        }
        return newMessage;
    }

    /**
     * Encrypts the message with certification file and saves them to storage.
     * 
     * @param message The message to encrypt and save.
     * @param filename The path to file, where message should be stored.
     * @param certfile The path to certfile to encrypt the message.
     * @throws fennecSystemException thrown if message can't encrypted, or some other error.
     */
    public static void encryptAndSaveEmail(final MimeMessage message, final String filename, final String certfile)
            throws fennecSystemException {
        pEncryptAndSaveEmail(message, filename, certfile);
    }

    /**
     * Encrypts the message with certification file and saves them to storage.
     * 
     * @param message The message to encrypt and save.
     * @param filename The path to file, where message should be stored.
     * @param certfile The path to certfile to encrypt the message.
     * @throws fennecSystemException thrown if message can't encrypted, or some other error.
     */
    private static void pEncryptAndSaveEmail(final MimeMessage message, final String filename, final String certfile)
            throws fennecSystemException {
        final MimeMessage encM = encryptMessageWithCert(message, Session.getDefaultInstance(
                new Properties()), certfile);
        saveEmail(encM, filename);
    }

    /**
     * Encrypts the message with keystore.
     * 
     * @param message The message to encrypt.
     * @param mailSession The session for the message.
     * @param keyfile The path to the keyfile.
     * @param password The password of the keyfile.
     * 
     * @return The encrypted MimeMessage.
     * 
     * @throws fennecSystemException thrown if message can't encrypted.
     */
    public static MimeMessage encryptMessageWithKeystore(final MimeMessage message, final Session mailSession,
            final String keyfile, final String password) throws fennecSystemException {
        return pEncryptMessageWithKeystore(message, mailSession, keyfile, password);
    }

    /**
     * Encrypts the message with keystore.
     * 
     * @param message The message to encrypt.
     * @param mailSession The session for the message.
     * @param keyfile The path to the keyfile.
     * @param password The password of the keyfile.
     * 
     * @return The encrypted MimeMessage.
     * 
     * @throws fennecSystemException thrown if message can't encrypted.
     */
    private static MimeMessage pEncryptMessageWithKeystore(final MimeMessage message, final Session mailSession,
            final String keyfile, final String password) throws fennecSystemException {
        MessageUtils.multiPartBugfix(message);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        MimeMessage smimeEncryptedMsg = null;
        try {
            final KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
            final String keyAlias = ExampleUtils.findKeyAlias(keyStore, keyfile, password.toCharArray());
            final Certificate[] chain = keyStore.getCertificateChain(keyAlias);

            final SMIMEEnvelopedGenerator smeg = new SMIMEEnvelopedGenerator();
            final RecipientInfoGenerator recipientInfoGen = new JceKeyTransRecipientInfoGenerator(
                    (X509Certificate) chain[0]);
            smeg.addRecipientInfoGenerator(recipientInfoGen);
            final OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.RC2_CBC).build();

            final MimeBodyPart mbp = smeg.generate(message, encryptor);
            smimeEncryptedMsg = new MimeMessage(mailSession);
            final Enumeration<?> hdrLines = message.getAllHeaderLines();
            while (hdrLines.hasMoreElements()) {
                smimeEncryptedMsg.addHeaderLine((String) hdrLines.nextElement());
            }
            smimeEncryptedMsg.setContent(mbp.getContent(), mbp.getContentType());
            smimeEncryptedMsg.saveChanges();
        } catch (final Exception e) {
            throw new fennecSystemException("Can't encrypt message.", e);
        }
        return smimeEncryptedMsg;
    }

    /**
     * Signs a message with keystore.
     * 
     * @param message The message to sign.
     * @param mailSession The session of the message.
     * @param keyfile The path to keystore file to sign the message.
     * @param password The keystore file password.
     * 
     * @return The signed message.
     * @throws fennecSystemException thrown if message can't sign.
     */
    public static MimeMessage signMessageWithKeystore(final MimeMessage message,
            final Session mailSession, final String keyfile, final String password) throws fennecSystemException {
        return pSignMessageWithKeystore(message, mailSession, keyfile, password);
    }

    /**
     * Signs a message with keystore.
     * 
     * @param message The message to sign.
     * @param mailSession The session of the message.
     * @param keyfile The path to keystore file to sign the message.
     * @param password The keystore file password.
     * 
     * @return The signed message.
     * @throws fennecSystemException thrown if message can't sign.
     */
    private static MimeMessage pSignMessageWithKeystore(final MimeMessage message,
            final Session mailSession, final String keyfile, final String password) throws fennecSystemException {
        MessageUtils.multiPartBugfix(message);

        MimeMessage signedMessage = null;
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        try {
            final KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
            final String keyAlias = ExampleUtils.findKeyAlias(keyStore, keyfile, password.toCharArray());
            final Certificate[] chain = keyStore.getCertificateChain(keyAlias);

            final SMIMECapabilityVector capabilities = new SMIMECapabilityVector();
            capabilities.addCapability(SMIMECapability.dES_EDE3_CBC);
            capabilities.addCapability(SMIMECapability.rC2_CBC, 128);
            capabilities.addCapability(SMIMECapability.dES_CBC);

            final ASN1EncodableVector attributes = new ASN1EncodableVector();
            final X500Name name = new X500Name(((X509Certificate) chain[0]).getIssuerDN().getName());
            final BigInteger serial = ((X509Certificate) chain[0]).getSerialNumber();

            final IssuerAndSerialNumber issAndSer = new IssuerAndSerialNumber(name, serial);
            attributes.add(new SMIMEEncryptionKeyPreferenceAttribute(issAndSer));
            attributes.add(new SMIMECapabilitiesAttribute(capabilities));

            final SMIMESignedGenerator signer = new SMIMESignedGenerator();
            final PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, password.toCharArray());

            signer.addSignerInfoGenerator(new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").
                    setSignedAttributeGenerator(new AttributeTable(attributes)).build(
                            "SHA1withRSA", privateKey, (X509Certificate) chain[0]));

            final MimeMultipart mimeMultiPart = signer.generate(message); // , "BC");
            signedMessage = new MimeMessage(mailSession);

            /* Set all original MIME headers in the signed message */
            final Enumeration<?> headers = message.getAllHeaderLines();
            while (headers.hasMoreElements()) {
                signedMessage.addHeaderLine((String) headers.nextElement());
            }

            /* Set the content of the signed message */
            signedMessage.setContent(mimeMultiPart);
            signedMessage.saveChanges();
        } catch (final Exception e) {
            throw new fennecSystemException("Can't sign message.", e);
        }
        return signedMessage;
    }

    /**
     * Decrypt message from keystore.
     * 
     * @param message The message to decrypt.
     * @param mailSession The session of the message.
     * @param keyfile The path to the keystore file.
     * @param password The password of the keystore file.
     * 
     * @return The decrypted message.
     * @throws fennecSystemException thrown if message can't decrypt.
     */
    public static MimeMessage decryptMessageWithKeystore(final MimeMessage message,
            final Session mailSession, final String keyfile, final String password) throws fennecSystemException {
        return pDecryptMessageWithKeystore(message, mailSession, keyfile, password);
    }

    /**
     * Decrypt message from keystore.
     * 
     * @param message The message to decrypt.
     * @param mailSession The session of the message.
     * @param keyfile The path to the keystore file.
     * @param password The password of the keystore file.
     * 
     * @return The decrypted message.
     * @throws fennecSystemException thrown if message can't decrypt.
     */
    private static MimeMessage pDecryptMessageWithKeystore(final MimeMessage message,
            final Session mailSession, final String keyfile, final String password) throws fennecSystemException {
        try {
            MessageUtils.multiPartBugfix(message);
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            final KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
            final String keyAlias = ExampleUtils.findKeyAlias(keyStore, keyfile, password.toCharArray());
            final Certificate[] chain = keyStore.getCertificateChain(keyAlias);

            final X509Certificate cert = (X509Certificate) chain[0];
            final JceKeyTransRecipientId recId = new JceKeyTransRecipientId(cert);

            final Key privateKey = keyStore.getKey(keyAlias, password.toCharArray());

//            recId.setSerialNumber(cert.getSerialNumber());
//            recId.setIssuer(cert.getIssuerX500Principal().getEncoded());

            final SMIMEEnveloped smimeEnveloped = new SMIMEEnveloped(message);
            final RecipientInformationStore recipients = smimeEnveloped.getRecipientInfos();
            final RecipientInformation recipient = recipients.get(recId);

            if (recipient == null) {
                throw new MessagingException("no recipient");
            }
            final MimeBodyPart mbp = SMIMEUtil.toMimeBodyPart(recipient.getContent(
                    new JceKeyTransEnvelopedRecipient((PrivateKey) privateKey).setProvider("BC")));

            final MimeMessage decryptedMessage = new MimeMessage(mailSession);

            final Enumeration<?> hLineEnum = message.getAllHeaderLines();
            while (hLineEnum.hasMoreElements()) {
                decryptedMessage.addHeaderLine((String) hLineEnum.nextElement());
            }
            final MimeMultipart multiPart = new MimeMultipart();
            multiPart.addBodyPart(mbp);

            decryptedMessage.setContent(multiPart);
            decryptedMessage.saveChanges();

            return decryptedMessage;

        } catch (final Exception e) {
            throw new fennecSystemException("Can't decrypt message", e);
        }
    }

    /**
     * Encrypts the message with keystore and saves them to storage.
     * 
     * @param message The message.
     * @param mailsession The session.
     * @param keyfile The path to keystore file.
     * @param password The password for the keyfile.
     * @param filename The filename/path to save the message.
     * @throws fennecSystemException thrown if message can't encrypt or save.
     */
    public static void encryptAndSaveEmail(final MimeMessage message, final Session mailsession, final String keyfile,
            final String password, final String filename) throws fennecSystemException {
        pEncryptAndSaveEmail(message, mailsession, keyfile, password, filename);
    }

    /**
     * Encrypts the message with keystore and saves them to storage.
     * 
     * @param message The message.
     * @param mailsession The session.
     * @param keyfile The path to keystore file.
     * @param password The password for the keyfile.
     * @param filename The filename/path to save the message.
     * @throws fennecSystemException thrown if message can't encrypt or save.
     */
    private static void pEncryptAndSaveEmail(final MimeMessage message, final Session mailsession,
            final String keyfile,
            final String password, final String filename) throws fennecSystemException {
        final MimeMessage mimeMessage = pEncryptMessageWithKeystore(message, mailsession, keyfile, password);
        pSaveEmail(mimeMessage, filename);
    }
}
