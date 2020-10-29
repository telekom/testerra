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
package eu.tsystems.mms.tic.testframework.mailconnector.imap;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.mailconnector.util.AbstractInboxConnector;
import java.util.Properties;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Store;

/**
 * MailConnector using the IMAP Protocol. Creates a session with values from mailconnection.properties.
 *
 * @author mibu
 */
public class ImapMailConnector extends AbstractInboxConnector implements Loggable {

    /**
     * Constructor, creates a ImapMailConnector object.
     */
    public ImapMailConnector() {
        super();
        init();
    }

    /**
     * Called from constructor. Initializes the ImapMailConnector.
     */
    private void init() {
        setServer(PropertyManager.getProperty("IMAP_SERVER", null));
        setPort(PropertyManager.getProperty("IMAP_SERVER_PORT", null));
        setInboxFolder(PropertyManager.getProperty("IMAP_FOLDER_INBOX", null));
        setUsername(PropertyManager.getProperty("IMAP_USERNAME", null));
        setPassword(PropertyManager.getProperty("IMAP_PASSWORD", null));
        setSslEnabled(PropertyManager.getBooleanProperty("IMAP_SSL_ENABLED", true));

        setDebug(PropertyManager.getBooleanProperty("DEBUG_SETTING", false));
    }

    /**
     * Open a new IMAP Session and save in session object.
     * @see {https://eclipse-ee4j.github.io/mail/docs/api/com/sun/mail/imap/package-summary.html}
     */
    @Override
    protected void openSession() {
        final Properties mailprops = new Properties();

        if (isSslEnabled()) {
            mailprops.put("mail.store.protocol", "imaps");
        } else {
            mailprops.put("mail.store.protocol", "imap");
        }

        setSession(createDefaultSession(mailprops, mailprops.getProperty("mail.store.protocol")));
    }

    /**
     * Marks all messages in inbox as seen.
     *
     * @throws TesterraSystemException Error connecting with Server.
     */
    public void markAllMailsAsSeen() throws TesterraSystemException {
        this.pMarkAllMailsAsSeen();
    }

    /**
     * Marks all messages in inbox as seen.
     *
     * @throws TesterraSystemException Error connecting with Server.
     */
    private void pMarkAllMailsAsSeen() throws TesterraSystemException {
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
            log().error(e.getMessage());
            throw new TesterraSystemException(e);
        } catch (final MessagingException e) {
            log().error(e.getMessage());
            throw new TesterraSystemException(e);
        }
    }
}
