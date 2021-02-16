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
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.utils.PdfUtils;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author wakr
 *
 */
public class PdfUtilsTest extends TesterraTest {

    private final String errorMessage = "Error while opening pdf and reading the target text";
    private final String testfile = "testfiles/Test.pdf";

    /**
     * Test of pdf-conversion
     */
    @Test
    public void testT01_PdfConverting_ContentFound() {
        String content = PdfUtils
                .getStringFromPdf(this.getClass().getClassLoader().getResourceAsStream(testfile));
        Assert.assertTrue(content.contains("Test"), errorMessage);
    }

    /**
     * gets pdf over inputStream, converts it into a string and checks the content
     */
    @Test
    public void testT02F_PdfConverting_ContentNotFound() {
        String content = PdfUtils
                .getStringFromPdf(this.getClass().getClassLoader().getResourceAsStream(testfile));
        Assert.assertFalse(content.contains("error"), errorMessage);
    }

    /**
     * gets pdf over file path, converts it into a string and checks the content
     */
    @Test
    public void testT03_PdfConverting_ContentFound() throws FileNotFoundException {
        String absoluteFilePath = FileUtils.getAbsoluteFilePath(testfile);
        String content = PdfUtils.getStringFromPdf(absoluteFilePath);
        Assert.assertTrue(content.contains("alafandus"), errorMessage);
    }

    /**
     * gets pdf over file path, converts it into a string and checks the content
     */
    @Test
    public void testT04F_PdfConverting_ContentNotFound() throws FileNotFoundException {
        String absoluteFilePath = FileUtils.getAbsoluteFilePath(testfile);
        String content = PdfUtils.getStringFromPdf(absoluteFilePath);
        Assert.assertFalse(content.contains("13"), errorMessage);
    }

}




