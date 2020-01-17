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
package eu.tsystems.mms.tic.testframework.core.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.exceptions.FileNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by toku on 07.01.2015.
 */
public class FileUtilsTest extends AbstractWebDriverTest {

    private static final String testfile = "testfiles/Test.txt";
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

        TesterraSystemException foundException = null;

        try {
            FileUtils.getLocalResourceInputStream("/does/not/exist.txt");
        } catch (final TesterraSystemException e) {
            foundException = e;
        }

        Assert.assertNotNull(foundException, "No Exception occurred, but we expected one.");
    }

    @Test
    public void testT05_readResourceInsideDependencyJarFailed() {

        TesterraSystemException exception = null;

        try {
            FileUtils.getLocalResourceInputStream("testng.css");
        } catch (TesterraSystemException e) {
            exception = e;
        }

        Assert.assertNotNull(exception, "Error reading file.");
    }

    @Test
    public void testT06_readLocalFile() {

        final InputStream resourceInputStream = FileUtils.getLocalFileOrResourceInputStream("build.gradle");
        Assert.assertNotNull(resourceInputStream, "Resource File found.");
    }
}
