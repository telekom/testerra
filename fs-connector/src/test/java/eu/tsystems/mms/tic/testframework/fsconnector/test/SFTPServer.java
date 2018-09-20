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
package eu.tsystems.mms.tic.testframework.fsconnector.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.*;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;

public class SFTPServer {

    private static final int FTP_PORT = 2221;
    private static final String DEFAULT_LISTENER = "default";
    private static final List<Authority> ADMIN_AUTHORITIES;

    private static final String DEFAULT_USER_DIR = System.getProperty("user.dir") + "/target/ftpserver/";

    public final static int MAX_CONCURRENT_LOGINS = 1;
    public final static int MAX_CONCURRENT_LOGINS_PER_IP = 1;

    private static FtpServer ftpServer;
    private static UserManager userManager;
    private static FtpServerFactory ftpServerFactory;
    private static ListenerFactory listenerFactory;

    static {
        // Admin Authorities
        ADMIN_AUTHORITIES = new ArrayList<Authority>();
        ADMIN_AUTHORITIES.add(new WritePermission());
        ADMIN_AUTHORITIES.add(new ConcurrentLoginPermission(MAX_CONCURRENT_LOGINS, MAX_CONCURRENT_LOGINS_PER_IP));
        ADMIN_AUTHORITIES.add(new TransferRatePermission(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    public static void init() throws FtpException {
        ftpServerFactory = new FtpServerFactory();
        listenerFactory = new ListenerFactory();
        listenerFactory.setPort(FTP_PORT);

        Listener listener = listenerFactory.createListener();
        ftpServerFactory.addListener(DEFAULT_LISTENER, listener);
        ftpServerFactory.getFtplets().put(FTPLetImpl.class.getName(), new FTPLetImpl());

        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());
        userManager = userManagerFactory.createUserManager();

        ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
        connectionConfigFactory.setAnonymousLoginEnabled(true);

        ftpServerFactory.setUserManager(userManager);
        createAdminUser();
        // test user, writing permission
        ArrayList<Authority> userAuthorities = new ArrayList<Authority>();
        userAuthorities.add(new WritePermission());
        SFTPServer.addUser(FSConnectorFTPTest.FTPUSER, FSConnectorFTPTest.FTPPASSWD, userAuthorities);
        // anonymous User, no writing permission
        SFTPServer.addUser("Anonymous", "", new ArrayList<Authority>());

        ftpServer = ftpServerFactory.createServer();
        ftpServer.start();
    }

    private static UserManager createAdminUser() throws FtpException {
        UserManager userManager = ftpServerFactory.getUserManager();
        String adminName = userManager.getAdminName();

        if (!userManager.doesExist(adminName)) {
            BaseUser adminUser = new BaseUser();
            adminUser.setName(adminName);
            adminUser.setPassword(adminName);
            adminUser.setEnabled(true);
            adminUser.setAuthorities(ADMIN_AUTHORITIES);
            adminUser.setHomeDirectory(DEFAULT_USER_DIR);
            adminUser.setMaxIdleTime(0);
            userManager.save(adminUser);
        }

        return userManager;
    }

    public static void addUser(String username, String password, ArrayList<Authority> userAuthorities) throws FtpException {
        BaseUser user = new BaseUser();
        user.setName(username);
        user.setPassword(password);
        user.setHomeDirectory(DEFAULT_USER_DIR);


        user.setAuthorities(userAuthorities);
        user.setEnabled(true);

        ftpServerFactory.getUserManager().save(user);
    }

    public static void restartFTP() throws FtpException {
        if (ftpServer != null) {
            ftpServer.stop();
            try {
                Thread.sleep(1000 * 3);
            } catch (InterruptedException e) {
            }
            ftpServer.start();
        }
    }

    public static void stopFTP() throws FtpException {
        if (ftpServer != null) {
            ftpServer.stop();
        }
    }

    public static void pauseFTP() throws FtpException {
        if (ftpServer != null) {
            ftpServer.suspend();
        }
    }

    public static void resumeFTP() throws FtpException {
        if (ftpServer != null) {
            ftpServer.resume();
        }
    }

    public static void start() throws FtpException {
        init();
    }

}
