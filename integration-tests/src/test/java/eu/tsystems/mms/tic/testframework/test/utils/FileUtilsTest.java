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

package eu.tsystems.mms.tic.testframework.test.utils;

import eu.tsystems.mms.tic.testframework.exceptions.FileNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class FileUtilsTest extends TesterraTest {

    private static final String testfile = "testfiles/Test.txt";
    private static final String testWithWhiteSpace = "test with whitespace/test.txt";
    private static final Path localFilePath = Paths.get(System.getProperty("user.dir"), "FileUtilsTest.txt");

    @AfterMethod
    public void removeLocalFileIfPresent(Method method) throws IOException {

        final File file = localFilePath.toFile();

        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
    }

    /**
     * checks if method gets the absolute file path
     *
     * @throws IOException           .
     * @throws FileNotFoundException .
     */
    @Test
    public void testT01_AbsoluteFilePath() throws IOException, FileNotFoundException {
        String absoluteFilePath = FileUtils.getAbsoluteFilePath(testfile);
        File file = new File(absoluteFilePath);
        Assert.assertTrue(file.exists(), "File exists");
    }

    /**
     * reads content from resource file and checks it
     *
     * @throws IOException           .
     * @throws FileNotFoundException .
     */
    @Test
    public void testT02_readResourceFile() throws IOException, FileNotFoundException {
        String content = FileUtils.readFromResourceFile(testfile);
        AssertUtils.assertContains(content, "Huhu");
    }

    /**
     * reads content from file and checks it
     *
     * @throws IOException           .
     * @throws FileNotFoundException .
     */
    @Test
    public void testT03_readFromFile() throws IOException, FileNotFoundException {

        String absoluteFilePath = FileUtils.getAbsoluteFilePath(testfile);
        String content = FileUtils.readFromFile(absoluteFilePath);
        AssertUtils.assertContains(content, "Huhu");
    }

    @Test
    public void testT04_readFileNotExistingFailed() {

        SystemException foundException = null;

        try {
            FileUtils.getLocalResourceInputStream("/does/not/exist.txt");
        } catch (final SystemException e) {
            foundException = e;
        }

        Assert.assertNotNull(foundException, "No Exception occurred, but we expected one.");
    }

    @Test
    public void testT05_readResourceInsideDependencyJarFailed() {

        SystemException exception = null;

        try {
            FileUtils.getLocalResourceInputStream("testng.css");
        } catch (SystemException e) {
            exception = e;
        }

        Assert.assertNotNull(exception, "Error reading file.");
    }

    @Test
    public void testT06_readLocalFile() {

        final InputStream resourceInputStream = FileUtils.getLocalFileOrResourceInputStream("build.gradle");
        Assert.assertNotNull(resourceInputStream, "Resource File found.");
    }

    @Test
    public void testT07_readFilesWithWhitespaces() throws java.io.FileNotFoundException {

        FileUtils fileUtils = new FileUtils();
        File localOrResourceFile = fileUtils.getLocalOrResourceFile(testWithWhiteSpace);
        Assert.assertTrue(localOrResourceFile.exists(), "File exists.");

    }
}
