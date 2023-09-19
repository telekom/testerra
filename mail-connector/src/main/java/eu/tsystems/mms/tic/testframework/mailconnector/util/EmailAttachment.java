package eu.tsystems.mms.tic.testframework.mailconnector.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class EmailAttachment {

    private final String fileName;
    private final InputStream is;
    private final String encoding;

    public EmailAttachment(String fileName, InputStream is, String encoding) {
        this.fileName = fileName;
        this.is = is;
        this.encoding = encoding;
    }

    /**
     * Saves the attachment as a file under the given destination
     *
     * @param pathToFile the destination where the file should be saved
     * @return the File object
     */
    public File saveAsFile(String pathToFile) throws IOException {
        File file = new File(pathToFile);
        // Reset the InputStream to ensure the file is created properly
        is.reset();
        FileUtils.copyInputStreamToFile(is, file);
        return file;
    }

    /**
     * Saves the attachment as a file
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
        is.reset();
        return IOUtils.toString(is, encoding);
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
        return is;
    }

    /**
     * @return the encoding used for the attachment
     */
    public String getEncoding() {
        return encoding;
    }
}
