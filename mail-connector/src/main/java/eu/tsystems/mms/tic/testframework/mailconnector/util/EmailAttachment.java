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

    /**
     * Name of the file
     */
    private final String fileName;

    /**
     * InputStream of the attachment
     */
    private final InputStream inputStream;

    /**
     * The encoding used for the file
     */
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
     * Saves the attachment as a file under the given destination
     *
     * @param destination the destination path where the file should be saved
     * @return the File object
     */
    public File saveFile(String destination) throws IOException {
        File file = new File(destination);
        // Reset the InputStream to ensure the file is created properly
        inputStream.reset();
        FileUtils.copyInputStreamToFile(inputStream, file);
        return file;
    }

    /**
     * Saves the attachment as a file
     *
     * @return the File object
     */
    public File saveFile() throws IOException {
        return saveFile(fileName);
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

    /**
     * @return the name of the attachment
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the InputStream of the attachment
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * @return the encoding used for the attachment
     */
    public String getEncoding() {
        return encoding;
    }
}
