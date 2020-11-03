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
package eu.tsystems.mms.tic.testframework.utils;

import de.idyl.winzipaes.AesZipFileEncrypter;
import de.idyl.winzipaes.impl.AESEncrypter;
import de.idyl.winzipaes.impl.AESEncrypterJCA;
import eu.tsystems.mms.tic.testframework.exceptions.FileNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FilenameUtils;
import static java.lang.Thread.currentThread;

public final class FileUtils extends org.apache.commons.io.FileUtils implements Loggable {

    private static String lineBreak = "\n";

    public FileUtils() {
    }

    /**
     * Simply get the resource as a stream - BUT DOES NOT return if resource file is inside a a included dependency jar.
     *
     * @param fileInResources {@link String}
     *
     * @return InputStream
     *
     * @implNote avoid logging here!
     */
    public static InputStream getLocalResourceInputStream(final String fileInResources) throws SystemException {

        final URL resource = getResourceURL(fileInResources);

        // exit, when no file present
        // exit, when file is not loaded from our resource path but from jar
        if (resource == null || !resource.toString().startsWith("file:")) {
            throw new SystemException("No local resource file found: " + fileInResources);
        }

        try {
            return Objects.requireNonNull(resource).openStream();
        } catch (IOException e) {
            throw new SystemException(fileInResources, e);
        }
    }

    /**
     * Gets a local file if present, otherwise search for resource file.
     *
     * @param filePathAndName {@link String} relative path of file or resource
     *
     * @return InputStream
     *
     * @implNote avoid logging here!
     * @deprecated Use {@link #getLocalOrResourceFile(String)} instead
     */
    @Deprecated
    public static InputStream getLocalFileOrResourceInputStream(final String filePathAndName) throws SystemException {
        try {
            return getLocalFileInputStream(filePathAndName);
            // throws FileNotFound, when not present! --> Try to get the resource file instead.
        } catch (FileNotFoundException e) {
            return getLocalResourceInputStream(filePathAndName); // throws a TesterraException
        }
    }

    /**
     * Gets a local or a resource file
     * @param filePath
     * @return
     * @throws java.io.FileNotFoundException Thrown when the file hasn't been found or is not a local file
     */
    public File getLocalOrResourceFile(final String filePath) throws java.io.FileNotFoundException {
        final File relativeFile = new File(filePath);

        if (relativeFile.exists()) {
            return relativeFile;
        } else {
            URL resourceUrl = currentThread().getContextClassLoader().getResource(filePath);
            if (resourceUrl == null || !resourceUrl.toString().startsWith("file:")) {
                throw new java.io.FileNotFoundException("No local resource file: "+ filePath);
            }
            return new File(resourceUrl.getFile());
        }
    }

    /**
     * Gets a local file if present, otherwise returns null
     *
     * @param filePathAndName {@link String} relative path of file to load
     *
     * @return InputStream
     *
     * @throws FileNotFoundException Exception, when file not existing
     */
    @Deprecated
    public static InputStream getLocalFileInputStream(final String filePathAndName) throws FileNotFoundException {

        final File relativeFile = new File(filePathAndName);

        if (relativeFile.exists()) {
            try {
                return new FileInputStream(relativeFile);
            } catch (java.io.FileNotFoundException e) {
                throw new FileNotFoundException(filePathAndName, e);
            }
        }

        throw new FileNotFoundException(filePathAndName); // throw this, because File really does not exist!
    }

    /**
     * Get an absolute file path from a resource file path.
     *
     * @param fileInResources .
     *
     * @return Absolute file path.
     *
     * @throws FileNotFoundException
     */
    public static String getAbsoluteFilePath(String fileInResources) throws FileNotFoundException {

        ClassLoader contextClassLoader = currentThread().getContextClassLoader();
        URL resource = contextClassLoader.getResource(fileInResources);
        if (resource == null) {
            throw new FileNotFoundException(fileInResources);
        }

        URI uri = null;
        try {
            uri = resource.toURI();
        } catch (URISyntaxException e) {
            throw new SystemException("Error getting file uri: " + resource);
        }
        File file = new File(uri);
        String absolutePath = file.getAbsolutePath();
        return absolutePath;
    }

    /**
     * Read a resource file into a string.
     *
     * @param fileInResources .
     *
     * @return string.
     *
     * @throws IOException
     */
    public static String readFromResourceFile(String fileInResources) throws IOException {

        String absoluteFilePath = null;
        try {
            absoluteFilePath = getAbsoluteFilePath(fileInResources);
        } catch (FileNotFoundException e) {
            throw new SystemException("Error loading file: " + fileInResources, e);
        }

        return readFromFile(absoluteFilePath);
    }

    /**
     * Read an absolute file into a string.
     *
     * @param absoluteFilePath .
     *
     * @return String.
     *
     * @throws IOException
     */
    public static String readFromFile(String absoluteFilePath) throws IOException {
        FileReader fileReader = new FileReader(absoluteFilePath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line).append(lineBreak);

            // read a new line
            line = bufferedReader.readLine();
        }

        // reading is done
        bufferedReader.close();

        return stringBuilder.toString();
    }

    public static String getLineBreak() {
        return lineBreak;
    }

    public static void setLineBreak(String lineBreak) {
        FileUtils.lineBreak = lineBreak;
    }

    public static void zip(final File targetFile, final File... filesToAdd) throws ZipException {
        zip(targetFile, new ZipParameters(), filesToAdd);
    }

    public static void zip(final File targetFile, ZipParameters params, final File... filesToAdd) throws ZipException {
        if (params == null) {
            params = new ZipParameters();
        }
        if (filesToAdd == null) {
            throw new IllegalArgumentException("No files named to zip.");
        }
        final ZipFile zipFile = new ZipFile(targetFile);
        for (File file : filesToAdd) {
            zipFile.addFile(file, params);
        }
    }

    public static void zip(final File targetFile, final InputStream... inputStreams) throws ZipException {
        zip(targetFile, new ZipParameters(), inputStreams);
    }

    public static void zip(final File targetFile, ZipParameters params, final InputStream... inputStreams) throws ZipException {
        if (params == null) {
            params = new ZipParameters();
        }
        if (inputStreams == null) {
            throw new IllegalArgumentException("No files named to zip.");
        }
        final ZipFile zipFile = new ZipFile(targetFile);
        for (InputStream is : inputStreams) {
            zipFile.addStream(is, params);
        }
    }

    public static void zipWinZipAes256(final File targetFile, final String password, final File... filesToAdd) throws IOException {
        AESEncrypter aesEncrypter = new AESEncrypterJCA();
        aesEncrypter.init(password, 256);
        AesZipFileEncrypter zip = new AesZipFileEncrypter(targetFile.getAbsolutePath(), aesEncrypter);

        for (File file : filesToAdd) {
            final String name = file.getName();
            final String absolutePath = file.getAbsolutePath();
            zip.add(name, new FileInputStream(absolutePath), password);
        }
        zip.close();
    }

    /**
     * Unzip utility.
     *
     * @param infile               Name of zipped File
     * @param destinationDirectory Directoy to unzip
     */
    public static void unzip(final String infile, final String destinationDirectory) throws ZipException {
        unzip(infile, destinationDirectory, null);
    }

    /**
     * Unzip utility.
     *
     * @param infile               Name of zipped File
     * @param destinationDirectory Directoy to unzip
     * @param password             Password
     */
    public static void unzip(final String infile, final String destinationDirectory, final String password) throws ZipException {
        ZipFile zipFile = new ZipFile(infile);
        if (password != null) {
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
        }
        zipFile.extractAll(destinationDirectory);
    }

    public static URL getResourceURL(String resourceFile) {
        return currentThread().getContextClassLoader().getResource(resourceFile);
    }

    public static File getResourceFile(String resourceFile) {

        final URL resourceUrl = getResourceURL(resourceFile);

        if (resourceUrl == null) {
            throw new SystemException("Could not load resource file. File does not exist: " + resourceFile);
        }

        return new File(resourceUrl.getFile());
    }
//
//    public <X> X fileToOutputType(final File file, OutputType<X> outputType) {
//        if (outputType == OutputType.FILE) {
//            return (X) file;
//        } else {
//            final byte[] bytes;
//            try {
//                bytes = IOUtils.toByteArray(new FileInputStream(file));
//                if (outputType == OutputType.BASE64) {
//                    return (X) Base64.getEncoder().encode(bytes);
//                } else {
//                    return (X) bytes;
//                }
//            } catch (IOException e) {
//                log().error("Unable convert file", e);
//            }
//        }
//        return null;
//    }

    public File createTempFileName(String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        return new File(System.getProperty("java.io.tmpdir") + "/" + FilenameUtils.getBaseName(fileName) + "-" + UUID.randomUUID() + (extension.length() > 0 ? "." + extension : ""));
    }

    public File createTempDir(String dirName) {
        File dir = new File(System.getProperty("java.io.tmpdir") + "/" + dirName + "-" + UUID.randomUUID());
        dir.mkdirs();
        return dir;
    }
}
