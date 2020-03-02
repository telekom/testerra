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
 *     Peter Lehmann
 *     pele
 */

package eu.tsystems.mms.tic.testframework;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * Connector class to communicate via telnet with a server
 *
 * @author wakr
 */
public class TelnetConnector {
    private TelnetClient telnetClient = new TelnetClient();
    private InputStream in;
    private PrintStream out;
    private String prompt;
    private String usernamePrompt = "login: ";
    private String passwordPrompt = "password: ";


    /**
     * Constructor of the connection
     *
     * @param host     you are connecting to
     * @param username which is allowed to connect
     * @param password .
     */
    public TelnetConnector(String host, String username, String password) {
        this(host, 23, username, password);
    }

    /**
     * checks if auto login of telnet connector works
     * @param host
     * @param port
     * @param username
     * @param password
     * @param autoLogin
     */
    public TelnetConnector(String host, int port, String username, String password, boolean autoLogin) {
        if (autoLogin) {
            login(host, port, username, password);
        }
    }

    /**
     * connects to telnet
     * @param host .
     * @param port .
     * @param username .
     * @param password .
     */
    public TelnetConnector(String host, int port, String username, String password) {
        login(host, port, username, password);
    }

    /**
     * executes the login
     * @param host .
     * @param port .
     * @param username .
     * @param password .
     */
    public void login(String host, int port, String username, String password) {
        try {
            // Connect to the specified host
            telnetClient.connect(host, port);

            // Get input and output stream references
            in = telnetClient.getInputStream();
            out = new PrintStream(telnetClient.getOutputStream());

            // Log the username on
            readUntil(usernamePrompt);
            write(username);
            readUntil(passwordPrompt);
            write(password);
            setPrompt(generatePrompt());

        } catch (Exception e) {
            throw new TesterraSystemException("Error while connecting to telnet", e);
        }
    }

    void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /**
     * Utility-method to read until a given sequence appears
     *
     * @param pattern the String the method is waiting to stop reading
     * @return String of the answers of the server
     */
    public String readUntil(String pattern) {
        try {
            char lastChar = pattern.charAt(pattern.length() - 1);
            StringBuilder sb = new StringBuilder();
            char ch = (char) in.read();
            while (true) {
                sb.append(ch);
                if (ch == lastChar) {
                    if (sb.toString().endsWith(pattern)) {
                        return sb.toString();
                    }
                }
                ch = (char) in.read();
            }
        } catch (Exception e) {
            throw new TesterraSystemException("Error while waiting for prompt", e);
        }
    }

    /**
     * Waits for an Answer of the server
     *
     * @param command that is sent
     * @return the answer
     */
    String getAnswer(String command) {
        byte[] buffer = null;
        String output = "";
        String test = "";
        try {
            buffer = new byte[in.available()];

            in.read(buffer);
            // in.close();
            output = new String(buffer);
            test = escapeLineSeperatorsAdvanced(output);
            test = getFormattedString(test, command);
        } catch (Exception e) {
            throw new TesterraSystemException("Error while waiting for answer", e);
        }
        return test;
    }

    /**
     * Deletes the prompt and command sequence from the answer of the server
     *
     * @param unformattedString
     * @param command
     * @return a String with correct formatted String
     */
    String getFormattedString(String unformattedString, String command) {
        try {
            unformattedString =
                    unformattedString.substring(unformattedString.indexOf(command), unformattedString.length());
            unformattedString = unformattedString.replace(command, "");
            return unformattedString;
        } catch (StringIndexOutOfBoundsException e) {
            return unformattedString;
        }
    }

    /**
     * Sends an echo command to the server and generates the prompt sequence of the user connected
     *
     * @return prompt String
     */
    public String generatePrompt() {
        write("echo Test");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new TesterraSystemException("Error while waiting to execute command !");
        }
        String prompt = getAnswer("echo Test");
        while (prompt.contains("\n")) {
            prompt = prompt.replace("\n", "");
        }
        prompt = prompt.trim();
        return prompt;
    }

    /**
     * Method converts a String in the correct formatted way
     *
     * @param line server answer
     * @return correct formatted String
     */
    public String escapeLineSeperators(String line) {
        String escapedString = "";
        try {
            for (int i = 0; i < (line.length() - 1); i++) {

                if ((int) line.charAt(i) == 27 && (int) line.charAt(i + 1) == 91 && line.contains("H")) {
                    String escape = line.substring(i, line.length());
                    int locationOfEscape = escape.indexOf("H");
                    escape = line.substring(i, locationOfEscape + i + 1);
                    line = line.replace(escape, System.lineSeparator());
                    if (i >= (line.length() - 1)) {
                        break;
                    }
                }
            }
            String[] lines = line.split(System.lineSeparator());
            lines[lines.length - 1] = "";
            for (String elem : lines) {
                escapedString += (elem + System.lineSeparator());
            }
        } catch (IndexOutOfBoundsException e) {
            return escapedString;
        }
        return escapedString;

    }

    /**
     * Method converts a String in the correct formatted way, and generates the prompt
     *
     * @param line server answer
     * @return correct formatted String
     */
    public String escapeLineSeperatorsAdvanced(String line) {
        String escapedString = "";
        String[] test = null;
        try {
            for (int i = 0; i < (line.length() - 1); i++) {

                if ((int) line.charAt(i) == 27 && (int) line.charAt(i + 1) == 91 && line.contains("H")) {
                    String escape =
                            line.substring(i, line.length());
                    int locationOfEscape = escape.indexOf("H");
                    escape = line.substring(i,
                            locationOfEscape + i + 1);
                    line = line.replace(escape, System.lineSeparator());
                    if (i >= (line.length() - 1)) {
                        break;
                    }
                }
            }
            test = line.split(System.lineSeparator());
            for (String elem : test) {
                escapedString += (elem + System.lineSeparator());
            }
        } catch (IndexOutOfBoundsException e) {
            return test[test.length - 1];
        }
        return test[test.length - 1];

    }

    /**
     * Writes a command while speaking to the server
     *
     * @param command the method is sending to the server
     */
    public void write(String command) {
        try {
            out.println(command);
            out.flush();
        } catch (Exception e) {
            throw new TesterraSystemException("Error while sending command to telnet" + e.getStackTrace());
        }
    }

    /**
     * Replaces unnecessary line separators
     *
     * @param answer that should be formatted
     * @return a correct formatted String
     */
    public String deleteSeparators(String answer) {
        if (answer.contains("\r\n\r\n")) {
            answer = answer.replace("\r\n\r\n", "");
        }
        return answer;
    }

    /**
     * Sends an command and receives a answer from the server
     *
     * @param command to be executed
     * @return answer of the server
     */
    public String exec(String command) {
        String answer = "";
        try {
            write(command);
            String response = readUntil(prompt);
            answer = getFormattedString(response, command);
            answer = escapeLineSeperators(answer);
            answer = deleteSeparators(answer);
            answer = answer.replace("Test", "");
            answer = answer.trim();
        } catch (Exception e) {
            throw new TesterraSystemException("Error while connecting to telnet", e);
        }
        return answer;
    }

    /**
     * Closes a connection to the server
     */
    public void disconnect() {
        try {
            telnetClient.disconnect();
        } catch (Exception e) {
            throw new TesterraSystemException("Error while closing the connection", e);
        }
    }

    public void setUsernamePrompt(String usernamePrompt) {
        this.usernamePrompt = usernamePrompt;
    }

    public void setPasswordPrompt(String passwordPrompt) {
        this.passwordPrompt = passwordPrompt;
    }

    /**
     *
     *
     * @param args .
     */
    public static void main(String[] args) {
        TelnetConnector telnet = new TelnetConnector("localhost", "test", "test");
        String exec = telnet.exec("ls -al");
        System.out.println(exec);
        telnet.disconnect();
    }
}
