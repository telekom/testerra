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

import com.sun.mail.util.MailSSLSocketFactory;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.CertUtils;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.function.Consumer;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

/**
 * abstract class to handle mail connector
 *
 * @author sepr
 */
public abstract class AbstractMailConnector implements Loggable {

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

    protected Consumer<Properties> sessionPropertiesConfigurationConsumer;

    public AbstractMailConnector() {
        PropertyManager.loadProperties("mailconnection.properties");
    }

    /**
     * Sets a consumer for configuring properties right before {@link #openSession()}
     */
    public AbstractMailConnector configureSessionProperties(Consumer<Properties> consumer) {
        this.sessionPropertiesConfigurationConsumer = consumer;
        return this;
    }

    protected Session createDefaultSession(Properties mailProperties, String protocol) {
        mailProperties.put("mail."+protocol+".host", getServer());
        mailProperties.put("mail."+protocol+".port", getPort());

        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            CertUtils certUtils = new CertUtils();
            if (certUtils.isTrustAllHosts()) {
                log().warn("Trusting all hosts");
                sf.setTrustAllHosts(true);
                mailProperties.put("mail."+protocol+".ssl.trust", "*");
            } else {
                String[] hostsToTrust = certUtils.getTrustedHosts();
                if (hostsToTrust != null) {
                    String trustedHostsString = String.join(" ", hostsToTrust);
                    log().info("Trusting hosts: " + trustedHostsString);
                    mailProperties.put("mail." + protocol + ".ssl.trust", trustedHostsString);
                    sf.setTrustedHosts(hostsToTrust);
                }
            }
            mailProperties.put("mail."+protocol+".ssl.socketFactory", sf);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Error opening session", e);
        }

        mailProperties.put("mail.debug", isDebug());
        mailProperties.put("mail.debug.auth", isDebug());

        if (this.sessionPropertiesConfigurationConsumer != null) {
            this.sessionPropertiesConfigurationConsumer.accept(mailProperties);
        }

        Session session = Session.getInstance(
                mailProperties,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(getUsername(), getPassword());
                    }
                }
        );
        session.setDebug(isDebug());
        return session;
    }

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
}
