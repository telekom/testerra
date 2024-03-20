/*
 * Testerra
 *
 * (C) 2024, Tobias Adler, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.exceptions.FileNotFoundException;
import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.utils.PdfUtils;
import org.apache.commons.io.FilenameUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

public class PdfLayoutTest extends TesterraTest {

    @Test
    public void testPdfPageLayoutCheck() throws FileNotFoundException {
        int dpi = 150;
        String absoluteFilePath = FileUtils.getAbsoluteFilePath("testfiles/TestDocument.pdf");

        File actualImage = PdfUtils.pdfToImage(absoluteFilePath, dpi, 2);
        String referenceName = "TestDocument.pdf_page2";

        LayoutCheck.assertImage(actualImage, referenceName, 5);
    }

    @Test
    public void testPdfLayoutCheck() throws FileNotFoundException {
        int dpi = 150;
        String absoluteFilePath = FileUtils.getAbsoluteFilePath("testfiles/TestDocument.pdf");

        List<File> actualImages = PdfUtils.pdfToImage(absoluteFilePath, dpi);

        for (File image : actualImages) {
            String referenceName = FilenameUtils.removeExtension(image.getName());
            LayoutCheck.assertImage(image, referenceName, 5);
        }
    }
}
