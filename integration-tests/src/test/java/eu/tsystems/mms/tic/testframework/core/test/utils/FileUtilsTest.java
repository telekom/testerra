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
package eu.tsystems.mms.tic.testframework.core.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.exceptions.FileNotFoundException;
import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by toku on 07.01.2015.
 */
public class FileUtilsTest extends AbstractTest {

    private static final String testfile = "testfiles/Test.txt";

    /**
     * checks if method gets the absolute file path
     * @throws IOException .
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
     * @throws IOException .
     * @throws FileNotFoundException .
     */
    @Test
    public void testT02_readResourceFile() throws IOException, FileNotFoundException {
        String content = FileUtils.readFromResourceFile(testfile);
        AssertUtils.assertContains(content, "Huhu");
    }

    /**
     * reads content from file and checks it
     * @throws IOException .
     * @throws FileNotFoundException .
     */
    @Test
    public void testT03_readFromFile() throws IOException, FileNotFoundException {
        String absoluteFilePath = FileUtils.getAbsoluteFilePath(testfile);
        String content = FileUtils.readFromFile(absoluteFilePath);
        AssertUtils.assertContains(content, "Huhu");


    }


}
