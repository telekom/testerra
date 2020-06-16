/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Class for Reading Pdfs
 *
 * @author wakr
 */
public final class PdfUtils {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(PdfUtils.class);

    /**
     * Get content of pdf as string.
     *
     * @param pdffilelocation Absolute path of file
     * @return PDF content as String
     */
    public static String getStringFromPdf(String pdffilelocation) {
        File pdfFile = new File(pdffilelocation);
        if (!pdfFile.isFile()) {
            LOG.error("Given path is no file");
            return "";
        }
        try {
            return getStringFromPdf(new FileInputStream(pdfFile));
        } catch (FileNotFoundException e) {
            LOG.error("Error reading pdf file", e);
        }
        return "";
    }

    /**
     * Get content of pdf as string.
     *
     * @param stream File stream
     * @return PDF content as String
     */
    public static String getStringFromPdf(InputStream stream) {
        PDFParser parser = null;
        COSDocument cosDoc = null;
        PDDocument pdDoc = null;
        PDFTextStripper stripper = null;
        String parsedtext = null;
        try {
            parser = new PDFParser(stream);

            parser.parse();
            cosDoc = parser.getDocument();
            pdDoc = new PDDocument(cosDoc);
            stripper = new PDFTextStripper();
            parsedtext = stripper.getText(pdDoc);
            return parsedtext;
        } catch (IOException e) {
            LOG.error("Error reading pdf file", e);
            return "";
        } finally {
            try {
                if (pdDoc != null) {
                    pdDoc.close();
                }
            } catch (IOException e) {
                LOG.debug("error closing pdf stream", e);
            }
        }
    }

    /**
     * Hide constructor.
     */
    private PdfUtils() {
    }
}
