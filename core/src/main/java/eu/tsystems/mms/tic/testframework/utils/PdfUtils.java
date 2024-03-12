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

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for Reading Pdfs
 *
 * @author wakr
 */
public final class PdfUtils {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(PdfUtils.class);

    /**
     * Hide constructor.
     */
    private PdfUtils() {
    }

    /**
     * Get content of pdf as string.
     *
     * @param pdfFileLocation Absolute path of file
     * @return PDF content as String
     */
    public static String getStringFromPdf(String pdfFileLocation) {
        File pdfFile = new File(pdfFileLocation);
        PdfUtils.checkFile(pdfFile);
        try {
            return getStringFromPdf(new FileInputStream(pdfFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading pdf file", e);
        }
    }

    /**
     * Get content of pdf as string.
     *
     * @param stream File stream
     * @return PDF content as String
     */
    public static String getStringFromPdf(InputStream stream) {
        PDDocument pdDoc = getPdDocument(stream);
        PDFTextStripper stripper;
        String parsedText;
        try {
            stripper = new PDFTextStripper();
            parsedText = stripper.getText(pdDoc);
            return parsedText;
        } catch (IOException e) {
            throw new RuntimeException("Error reading pdf file", e);
        } finally {
            PdfUtils.closeDocument(pdDoc);
        }
    }

    /**
     * Get content of one page of the pdf as string.
     *
     * @param pdfFileLocation Absolute path of file
     * @param pageNumber The number of the page
     * @return PDF page content as String
     */
    public static String getStringFromPdf(String pdfFileLocation, int pageNumber) {
        File pdfFile = new File(pdfFileLocation);
        PdfUtils.checkFile(pdfFile);
        try {
            return getStringFromPdf(new FileInputStream(pdfFile), pageNumber);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading pdf file", e);
        }
    }

    /**
     * Get content of one page of the pdf as string.
     *
     * @param stream File stream
     * @param pageNumber The number of the page
     * @return PDF page content as String
     */
    public static String getStringFromPdf(InputStream stream, int pageNumber) {
        PDDocument pdDoc = getPdDocument(stream);

        PdfUtils.checkPageIndex(pdDoc, pageNumber);

        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(pageNumber);
        stripper.setEndPage(pageNumber);
        try {
            return stripper.getText(pdDoc);
        } catch (IOException e) {
            throw new RuntimeException("Error reading pdf file", e);
        } finally {
            PdfUtils.closeDocument(pdDoc);
        }
    }

    /**
     * Create an image for each page from pdf file in the given path
     *
     * @param pdfFileLocation Absolute path of file
     * @return A list of rendered image files
     */
    public static List<File> pdfToImage(String pdfFileLocation, int dpi) {
        File pdfFile = new File(pdfFileLocation);
        PdfUtils.checkFile(pdfFile);

        try {
            return PdfUtils.pdfToImage(new FileInputStream(pdfFile), dpi);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading pdf file", e);
        }
    }

    /**
     * Create an image for each page from pdf file in the given url
     *
     * @param stream File stream
     * @return A list of rendered image files
     */
    public static List<File> pdfToImage(InputStream stream, int dpi) {
        List<File> files = new ArrayList<>();

        PDDocument pdDoc = getPdDocument(stream);
        int numberOfPages = pdDoc.getNumberOfPages();
        PDFRenderer renderer = new PDFRenderer(pdDoc);

        String fileName = PdfUtils.getParsedDocumentName(pdDoc);

        for (int i = 1; i <= numberOfPages; i++) {
            files.add(PdfUtils.renderImage(renderer, dpi, i, fileName));
        }

        return files;
    }

    /**
     * Create an image for a specific page from pdf file in the given path
     *
     * @param pdfFileLocation Absolute path of file
     * @param pageNumber The number of the page
     * @return A list of rendered image files
     */
    public static File pdfToImage(String pdfFileLocation, int dpi, int pageNumber) {
        File pdfFile = new File(pdfFileLocation);
        PdfUtils.checkFile(pdfFile);

        try {
            return pdfToImage(new FileInputStream(pdfFile), dpi, pageNumber);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading pdf file", e);
        }
    }

    /**
     * Create an image for a specific page from the given input stream
     *
     * @param stream File stream
     * @param pageNumber The number of the page
     * @return A list of rendered image files
     */
    public static File pdfToImage(InputStream stream, int dpi, int pageNumber) {
        PDDocument pdDoc = getPdDocument(stream);
        PdfUtils.checkPageIndex(pdDoc, pageNumber);

        PDFRenderer renderer = new PDFRenderer(pdDoc);
        String fileName = PdfUtils.getParsedDocumentName(pdDoc);

        return PdfUtils.renderImage(renderer, dpi, pageNumber, fileName);
    }

    /**
     * Get the number of pages from pdf.
     *
     * @param pdfFileLocation Absolute path of file
     * @return Amount of pages the pdf file has
     */
    public static int getNumberOfPages(String pdfFileLocation) {
        File pdfFile = new File(pdfFileLocation);
        PdfUtils.checkFile(pdfFile);

        try {
            return PdfUtils.getNumberOfPages(new FileInputStream(pdfFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading pdf file", e);
        }
    }

    /**
     * Get the number of pages from pdf.
     *
     * @param stream File stream
     * @return Amount of pages the pdf file has
     */
    public static int getNumberOfPages(InputStream stream) {
        PDDocument pdDoc = getPdDocument(stream);
        int numberOfPages = pdDoc.getNumberOfPages();
        PdfUtils.closeDocument(pdDoc);

        return numberOfPages;
    }

    private static void checkPageIndex(PDDocument pdDoc, int pageNumber) {
        int numberOfPages = pdDoc.getNumberOfPages();
        if (pageNumber > numberOfPages || pageNumber < 1) {
            String errorMessage = "Page " + pageNumber + " does not exist in document.";
            if (numberOfPages == 1) {
                errorMessage = errorMessage + " Pdf file only contains one page.";
            } else {
                errorMessage = errorMessage + " Choose a page in range [1," + numberOfPages + "].";
            }
            throw new RuntimeException(errorMessage);
        }
    }

    private static PDDocument getPdDocument(InputStream stream) {
        PDDocument pdDoc;
        try {
            pdDoc = Loader.loadPDF(IOUtils.toByteArray(stream));
            return pdDoc;
        } catch (IOException e) {
            throw new RuntimeException("File cannot be loaded: Not a valid PDF document.", e);
        }
    }

    private static File renderImage(PDFRenderer renderer, int dpi, int pageNumber, String fileName) {
        BufferedImage actualImage;
        try {
            actualImage = renderer.renderImageWithDPI(pageNumber - 1, dpi);
        } catch (IOException e) {
            throw new RuntimeException("Error rendering image", e);
        }
        String imagePath = System.getProperty("java.io.tmpdir") + fileName + "_page_" + pageNumber + ".png";
        File imageFile = new File(imagePath);
        try {
            ImageIO.write(actualImage, "png", imageFile);
        } catch (IOException e) {
            throw new RuntimeException("Error writing png file", e);
        }

        return imageFile;
    }

    private static String getParsedDocumentName(PDDocument pdDoc) {
        return pdDoc.getDocumentInformation().getTitle().toLowerCase().replace(" ", "_");
    }

    private static void closeDocument(PDDocument pdDoc) {
        try {
            pdDoc.close();
        } catch (IOException e) {
            LOG.debug("Error closing pdf stream", e);
        }
    }

    private static void checkFile(File pdfFile) {
        if (!pdfFile.isFile()) {
            throw new RuntimeException("Given path is no file");
        }
    }
}
