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
package eu.tsystems.mms.tic.testframework.mailconnector.pop3;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.mailconnector.util.AbstractMailConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

/**
 * MailConnector using the POP3 Protocol. Creates a session with values from mailconnection.properties.
 *
 * @author pele, mrgi
 */
public class POP3MailConnector extends AbstractMailConnector {

    /**
     * The Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(POP3MailConnector.class);

    /**
     * Constructor, creates a POP3MailConnector Object.
     */
    public POP3MailConnector() {

        this.init();
    }

    /**
     * Called from constructor. Initializes the ImapMailConnector.
     */
    private void init() {

        PropertyManager.loadProperties("mailconnection.properties");
        setServer(PropertyManager.getProperty("POP3_SERVER", null));
        setPort(PropertyManager.getProperty("POP3_SERVER_PORT", null));
        setInboxFolder(PropertyManager.getProperty("POP3_FOLDER_INBOX", null));
        setUsername(PropertyManager.getProperty("POP3_USERNAME", null));
        setPassword(PropertyManager.getProperty("POP3_PASSWORD", null));
        // Password may needs to be encoded
        setDebug(PropertyManager.getBooleanProperty("DEBUG_SETTING", false));
        setSslEnabled(PropertyManager.getBooleanProperty("POP3_SSL_ENABLED", false));
    }

    /**
     * Open a new POP Session and save in session object.
     */
    @Override
    protected void openSession() {

        final Properties mailprops = new Properties();
        LOGGER.info("Setting host: " + getServer());
        LOGGER.info("Setting port: " + getPort());
        LOGGER.info("Setting sslEnabled: " + isSslEnabled());

        if (isSslEnabled()) {
            mailprops.put("mail.pop3s.host", getServer());
            mailprops.put("mail.pop3s.port", getPort());
            mailprops.put("mail.store.protocol", "pop3s");
            mailprops.put("mail.pop3.socketFactory.port", getPort());
            mailprops.put("mail.pop3s.socketFactory.class",
                    "eu.tsystems.mms.tic.testframework.mailconnector.FennecSSLSocketFactory");
        } else {
            mailprops.put("mail.pop3.host", getServer());
            mailprops.put("mail.pop3.port", getPort());
            mailprops.put("mail.store.protocol", "pop3");
        }

        mailprops.put("mail.user", getUsername());
        mailprops.put("mail.debug", isDebug());

        LOGGER.info("building session");
        setSession(Session.getInstance(mailprops,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {

                        return new PasswordAuthentication(getUsername(), getPassword());
                    }
                }));
        getSession().setDebug(isDebug());
    }

}
