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
package eu.tsystems.mms.tic.testframework.mailconnector.pop3;

import eu.tsystems.mms.tic.testframework.mailconnector.util.AbstractInboxConnector;

import java.util.Properties;

/**
 * MailConnector using the POP3 Protocol. Creates a session with values from mailconnection.properties.
 *
 * @author pele, mrgi
 */
public class POP3MailConnector extends AbstractInboxConnector {

    /**
     * Constructor, creates a POP3MailConnector Object.
     */
    public POP3MailConnector() {
        super();
        this.init();
    }

    /**
     * Called from constructor. Initializes the ImapMailConnector.
     */
    private void init() {
        setServer(PROPERTY_MANAGER.getProperty("POP3_SERVER", null));
        setPort(PROPERTY_MANAGER.getProperty("POP3_SERVER_PORT", null));
        setInboxFolder(PROPERTY_MANAGER.getProperty("POP3_FOLDER_INBOX", null));
        setUsername(PROPERTY_MANAGER.getProperty("POP3_USERNAME", null));
        setPassword(PROPERTY_MANAGER.getProperty("POP3_PASSWORD", null));

        // Password may needs to be encoded
        setDebug(PROPERTY_MANAGER.getBooleanProperty("DEBUG_SETTING", false));
        setSslEnabled(PROPERTY_MANAGER.getBooleanProperty("POP3_SSL_ENABLED", true));
    }

    /**
     * Open a new POP Session and save in session object.
     * @see {https://eclipse-ee4j.github.io/mail/docs/api/com/sun/mail/pop3/package-summary.html}
     */
    @Override
    protected void openSession() {
        final Properties mailprops = new Properties();

        if (isSslEnabled()) {
            mailprops.put("mail.store.protocol", "pop3s");
        } else {
            mailprops.put("mail.store.protocol", "pop3");
        }

        setSession(createDefaultSession(mailprops, mailprops.getProperty("mail.store.protocol")));
    }

}
