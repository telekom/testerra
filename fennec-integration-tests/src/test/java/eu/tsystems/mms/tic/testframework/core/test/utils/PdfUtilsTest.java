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
/* 
 * Created on 24.07.2014
 * 
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.exceptions.FileNotFoundException;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.utils.PdfUtils;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author wakr
 * 
 */
public class PdfUtilsTest extends AbstractTest {

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




