/*
 * Testerra
 *
 * (C) 2023, Tobias Adler, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
 */
package eu.tsystems.mms.tic.testframework.mailconnector.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * E-mail attachment object that is used to store information about the name of a file, its InputStream
 * and the encoding used for the file content.
 */
public class EmailAttachment {

    private final String fileName;

    private final InputStream inputStream;

    private final String encoding;

    /**
     * Constructor, creates an EmailAttachment object
     */
    public EmailAttachment(String fileName, InputStream is, String encoding) {
        this.fileName = fileName;
        this.inputStream = is;
        this.encoding = encoding;
    }

    /**
     * Saves the attachment as a file under the given destination and returns the File object
     *
     * @param destination the destination path where the file should be saved
     * @return the File object
     */
    public File saveAsFile(String destination) throws IOException {
        File file = new File(destination);
        // Reset the InputStream to ensure the file is created properly
        inputStream.reset();
        FileUtils.copyInputStreamToFile(inputStream, file);
        return file;
    }

    /**
     * Saves the attachment as a file and returns the File object
     *
     * @return the File object
     */
    public File saveAsFile() throws IOException {
        return saveAsFile(fileName);
    }

    /**
     * Uses encoding to determine the content of the attachment
     *
     * @return the content of the InputStream
     */
    public String getContent() throws IOException {
        // Reset the InputStream to ensure the content is read properly
        inputStream.reset();
        return IOUtils.toString(inputStream, encoding);
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getEncoding() {
        return encoding;
    }
}
