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
        FileInputStream stream = PdfUtils.getFileInputStream(pdfFile);
        return getStringFromPdf(stream);
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
     * Get content of one specific page of pdf as string.
     *
     * @param pdfFileLocation Absolute path of file
     * @param pageNumber The number of the page
     * @return PDF page content as String
     */
    public static String getStringFromPdf(String pdfFileLocation, int pageNumber) {
        File pdfFile = new File(pdfFileLocation);
        FileInputStream stream = PdfUtils.getFileInputStream(pdfFile);
        return getStringFromPdf(stream, pageNumber);
    }

    /**
     * Get content of one specific page of pdf as string.
     *
     * @param stream File stream
     * @param pageNumber The number of the page
     * @return PDF page content as String
     */
    public static String getStringFromPdf(InputStream stream, int pageNumber) {
        PDDocument pdDoc = getPdDocument(stream);
        PdfUtils.checkPageNumber(pdDoc, pageNumber);

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
     * Create an image for each page of pdf.
     *
     * @param pdfFileLocation Absolute path of file
     * @param dpi The resolution of the rendered image
     * @return A list of rendered image files
     */
    public static List<File> getImageFromPdf(String pdfFileLocation, int dpi) {
        File pdfFile = new File(pdfFileLocation);
        FileInputStream stream = PdfUtils.getFileInputStream(pdfFile);
        String fileName = pdfFile.getName();
        return PdfUtils.getImageFromPdf(stream, dpi, fileName);
    }

    /**
     * Create an image for each page of pdf.
     *
     * @param stream File stream
     * @param dpi The resolution of the rendered image
     * @param fileName The designated filename for saving the rendered images
     * @return A list of rendered image files
     */
    public static List<File> getImageFromPdf(InputStream stream, int dpi, String fileName) {
        List<File> files = new ArrayList<>();

        PDDocument pdDoc = getPdDocument(stream);
        int maxPageNumber = pdDoc.getNumberOfPages();
        PDFRenderer renderer = new PDFRenderer(pdDoc);

        String filesPath = PdfUtils.getParsedDocumentPath(fileName);

        for (int pageNumber = 1; pageNumber <= maxPageNumber; pageNumber++) {
            files.add(PdfUtils.renderImage(renderer, dpi, pageNumber, filesPath));
        }

        return files;
    }

    /**
     * Create an image for one specific page of pdf.
     *
     * @param pdfFileLocation Absolute path of file
     * @param dpi The resolution of the rendered image
     * @param pageNumber The number of the page
     * @return A list of rendered image files
     */
    public static File getImageFromPdf(String pdfFileLocation, int dpi, int pageNumber) {
        File pdfFile = new File(pdfFileLocation);
        FileInputStream stream = PdfUtils.getFileInputStream(pdfFile);
        String fileName = pdfFile.getName();
        return getImageFromPdf(stream, dpi, fileName, pageNumber);
    }

    /**
     * Create an image for one specific page of pdf.
     *
     * @param stream File stream
     * @param dpi The resolution of the rendered image
     * @param fileName The designated filename for saving the rendered images
     * @param pageNumber The number of the page
     * @return A list of rendered image files
     */
    public static File getImageFromPdf(InputStream stream, int dpi, String fileName, int pageNumber) {
        PDDocument pdDoc = getPdDocument(stream);
        PdfUtils.checkPageNumber(pdDoc, pageNumber);

        PDFRenderer renderer = new PDFRenderer(pdDoc);
        String filePath = PdfUtils.getParsedDocumentPath(fileName);

        return PdfUtils.renderImage(renderer, dpi, pageNumber, filePath);
    }

    public static int getNumberOfPages(String pdfFileLocation) {
        File pdfFile = new File(pdfFileLocation);
        FileInputStream stream = PdfUtils.getFileInputStream(pdfFile);
        return PdfUtils.getNumberOfPages(stream);
    }

    public static int getNumberOfPages(InputStream stream) {
        PDDocument pdDoc = getPdDocument(stream);
        int numberOfPages = pdDoc.getNumberOfPages();
        PdfUtils.closeDocument(pdDoc);

        return numberOfPages;
    }

    /**
     * Check if the page exists inside the pdf document.
     *
     * @param pdDoc The pdf document to check
     * @param pageNumber The page that should exist in the pdf
     */
    private static void checkPageNumber(PDDocument pdDoc, int pageNumber) {
        int maxPageNumber = pdDoc.getNumberOfPages();
        if (pageNumber > maxPageNumber || pageNumber < 1) {
            String errorMessage = "Page " + pageNumber + " does not exist in document.";
            if (maxPageNumber == 1) {
                errorMessage = errorMessage + " Pdf file only contains one page.";
            } else {
                errorMessage = errorMessage + " Choose a page number in range [1," + maxPageNumber + "].";
            }
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Uses the loader class to create the PDDocument.
     *
     * @param stream The content of the pdf file as an InputStream
     * @return The loaded pdf document
     */
    private static PDDocument getPdDocument(InputStream stream) {
        PDDocument pdDoc;
        try {
            pdDoc = Loader.loadPDF(IOUtils.toByteArray(stream));
            return pdDoc;
        } catch (IOException e) {
            throw new RuntimeException("File cannot be loaded: Not a valid PDF document.", e);
        }
    }

    /**
     * Uses the renderer to generate an image and save it to a file.
     *
     * @param renderer The renderer that should be used
     * @param dpi The amount of dots per inch the image should be rendered with
     * @param pageNumber The number of the page that should be saved as an Image
     * @param documentPath The path and name under which the image is saved
     * @return The loaded pdf document
     */
    private static File renderImage(PDFRenderer renderer, int dpi, int pageNumber, String documentPath) {
        BufferedImage actualImage;
        try {
            // PDFRenderer doesn't use the number of a page but rather the index starting at 0
            actualImage = renderer.renderImageWithDPI(pageNumber - 1, dpi);
        } catch (IOException e) {
            throw new RuntimeException("Error rendering image", e);
        }
        String imagePath = documentPath + "_page" + pageNumber + ".png";
        File imageFile = new File(imagePath);
        try {
            ImageIO.write(actualImage, "png", imageFile);
        } catch (IOException e) {
            throw new RuntimeException("Error writing png file", e);
        }
        return imageFile;
    }

    private static String getParsedDocumentPath(String fileName) {
        if (fileName == null) {
            fileName = "pdf_document";
        }

        FileUtils utils = new FileUtils();
        File tempDir = utils.createTempDir(fileName);

        return tempDir.getAbsolutePath() + File.separator + fileName;
    }

    private static void closeDocument(PDDocument pdDoc) {
        try {
            pdDoc.close();
        } catch (IOException e) {
            LOG.debug("Error closing pdf stream", e);
        }
    }

    private static FileInputStream getFileInputStream(File pdfFile) {
        if (!pdfFile.isFile()) {
            throw new RuntimeException("Given path is no file");
        }
        try {
            return new FileInputStream(pdfFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading pdf file", e);
        }
    }
}
