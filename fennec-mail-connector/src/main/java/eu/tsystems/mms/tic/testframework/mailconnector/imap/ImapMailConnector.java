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
 * Created on 12.04.2013
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.mailconnector.imap;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import eu.tsystems.mms.tic.testframework.mailconnector.util.AbstractMailConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

/**
 * MailConnector using the IMAP Protocol. Creates a session with values from mailconnection.properties.
 * 
 * @author mibu
 */
public class ImapMailConnector extends AbstractMailConnector {

    /** The Logger. */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ImapMailConnector.class);

    /**
     * Constructor, creates a ImapMailConnector object.
     */
    public ImapMailConnector() {
        this.init();
    }

    /**
     * Called from constructor. Initializes the ImapMailConnector.
     */
    private void init() {
        PropertyManager.loadProperties("mailconnection.properties");
        setServer(PropertyManager.getProperty("IMAP_SERVER", null));
        setPort(PropertyManager.getProperty("IMAP_SERVER_PORT",
                null));
        setInboxFolder(PropertyManager.getProperty(
                "IMAP_FOLDER_INBOX", null));
        setUsername(PropertyManager.getProperty("IMAP_USERNAME",
                null));
        setPassword(PropertyManager.getProperty("IMAP_PASSWORD",
                null));

        setDebug(PropertyManager.getBooleanProperty("DEBUG_SETTING", false));
        setSslEnabled(PropertyManager.getBooleanProperty(
                "IMAP_SSL_ENABLED", false));
    }

    /**
     * Open a new IMAP Session and save in session object.
     */
    @Override
    protected void openSession() {
        final Properties mailprops = new Properties();
        LOGGER.info("Setting host: " + getServer());
        LOGGER.info("Setting port: " + getPort());
        LOGGER.info("Setting sslEnabled: " + isSslEnabled());

        if (isSslEnabled()) {
            mailprops.put("mail.imaps.host", getServer());
            mailprops.put("mail.imaps.port", getPort());
            mailprops.put("mail.imaps.protocol", "imaps");
            mailprops.put("mail.imaps.socketFactory.port", getPort());
            mailprops.put("mail.imaps.socketFactory.class",
                    "eu.tsystems.mms.tic.testframework.mailconnector.FennecSSLSocketFactory");
            mailprops.put("mail.store.protocol", "imaps");
        } else {
            mailprops.put("mail.imap.host", getServer());
            mailprops.put("mail.imap.port", getPort());
            mailprops.put("mail.store.protocol", "imap");
        }

        mailprops.put("mail.user", getUsername());
        mailprops.put("mail.debug", isDebug());

        LOGGER.info("building session");
        setSession(Session.getInstance(mailprops,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(getUsername(),
                                getPassword());
                    }
                }));
        getSession().setDebug(isDebug());
    }

    /**
     * Marks all messages in inbox as seen.
     * 
     * @throws FennecSystemException Error connecting with Server.
     */
    public void markAllMailsAsSeen() throws FennecSystemException {
        this.pMarkAllMailsAsSeen();
    }

    /**
     * Marks all messages in inbox as seen.
     * 
     * @throws FennecSystemException Error connecting with Server.
     */
    private void pMarkAllMailsAsSeen() throws FennecSystemException {
        Store store;
        try {
            store = getSession().getStore();
            store.connect(getServer(), getUsername(), getPassword());
            final Folder root = store.getDefaultFolder();
            final Folder folder = root.getFolder(getInboxFolder());
            folder.open(Folder.READ_WRITE);
            final Message[] messages = folder.getMessages();
            for (Message message : messages) {
                message.setFlag(Flag.SEEN, true);
            }
            store.close();
        } catch (final NoSuchProviderException e) {
            LOGGER.error(e.getMessage());
            throw new FennecSystemException(e);
        } catch (final MessagingException e) {
            LOGGER.error(e.getMessage());
            throw new FennecSystemException(e);
        }
    }
}
