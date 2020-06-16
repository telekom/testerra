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
 package eu.tsystems.mms.tic.testframework.core.test.testdata;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;
import eu.tsystems.mms.tic.testframework.testdata.XLSTestDataReader;
import eu.tsystems.mms.tic.testframework.testdata.XLSWriter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * Tests for reading data out of excel sheets.
 *
 * @author pele
 */
public class XLSWriterTest extends AbstractWebDriverTest {

    public static final String XLS = "testfiles/TestXLSWriter.xls";
    public static final String XLSX = "testfiles/TestXLSWriter.xlsx";

    private static final Object LOCK = new Object();

    private static int filecounter = 0;

    /**
     * Executes content verification tests
     *
     * @param filename name of the file in resources
     * @param col column number to write in
     * @param dataName dataname
     * @param data expected data written by the test
     * @throws IOException exception
     */
    private void executeXlsWriterTest(String filename, int col, String dataName, String data) throws IOException {
        synchronized (LOCK) {
            XLSWriter xlsWriter = new XLSWriter();
            String sheetname = "Tabelle2";
            xlsWriter.openFileFromResource(filename, sheetname);
            String id = "writtentestdata";

            xlsWriter.writeCell(id, col, data);

            filecounter++;
            Report report = new Report();
            final File fullFile = report.getReportDirectory("xls/out-" + filecounter + "-" + filename);

            // check dirs
            File dir = fullFile.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // save
            xlsWriter.saveAs(fullFile.getAbsolutePath());

            XLSTestDataReader xlsTestDataReader = new XLSTestDataReader();
            xlsTestDataReader.setHeaderRow(1);
            Map<String, String> dataSet = xlsTestDataReader.readXLSTestDataFromFile(fullFile.getAbsolutePath(),
                    sheetname, id);
            Assert.assertEquals(dataSet.get(dataName), data, "Content verification");
        }
    }

    /**
     * writes new testdata into the .xls document
     * @throws IOException Error saving files
     */
    @Test
    public void testT01_XLS_Writer_XLS_String() throws IOException {
        String filename = XLS;
        executeXlsWriterTest(filename, 1, "Product Name", "Galaxy S");
    }

    /**
     * writes new testdata into .xlsx document
     * @throws IOException Error saving files
     */
    @Test
    public void testT02_XLS_Writer_XLSX_String() throws IOException {
        String filename = XLSX;
        executeXlsWriterTest(filename, 1, "Product Name", "Galaxy S");
    }

    /**
     * Writes new testdata into the .xls document
     * @throws IOException Error saving files
     */
    @Test
    public void testT03_XLS_Writer_XLS_Date() throws IOException {
        String filename = XLS;
        executeXlsWriterTest(filename, 2, "Date", "02.02.2002");
    }

    /**
     * Writes new testdata into the .xlsx document
     * @throws IOException Error saving files
     */
    @Test
    public void testT04_XLS_Writer_XLSX_Date() throws IOException {
        String filename = XLSX;
        executeXlsWriterTest(filename, 2, "Date", "02.02.2002");
    }

    /**
     * Writes new testdata into the .xls document
     * @throws IOException Error saving files
     */
    @Test
    public void testT05_XLS_Writer_XLS_Euro() throws IOException {
        String filename = XLS;
        executeXlsWriterTest(filename, 3, "Price", "20,00€");
    }

    /**
     * Writes new testdata into the .xlsx document
     * @throws IOException Error saving files
     */
    @Test
    public void testT06_XLS_Writer_XLSX_Euro() throws IOException {
        String filename = XLSX;
        executeXlsWriterTest(filename, 3, "Price", "20,00€");
    }

    /**
     * Writes new testdata into the documents(.xls or .xlsx) given by the Data Provider
     *
     * @throws IOException Error saving file.
     */
    @Test
    public void testT07F_XLS_Writer_FileNotExisting_FromResources() throws IOException {
        XLSWriter xlsWriter = new XLSWriter();
        try {
            xlsWriter.openFileFromResource("notExisting.xls", "sheet1");
        } catch (TesterraSystemException e) {
            Assert.assertNotNull(e.getMessage());
            final String msg = "Error reading resource file";
            Assert.assertTrue(e.getMessage().contains(msg), "Error message contains '" + msg + "'");
            return;
        }
        Assert.fail("No exception for a not existing document");
    }


    /**
     * Writes new testdata into the documents(.xls or .xlsx) given by the Data Provider
     *
     * @throws IOException Error saving file.
     */
    @Test
    public void testT08F_XLS_Writer_FileNotExisting_Absolute() throws IOException {
        XLSWriter xlsWriter = new XLSWriter();
        File file = new File(".");
        String rootDir = file.getAbsolutePath();
        String filepath = rootDir + "\\target\\" + file;
        try {
            xlsWriter.openFileAbsolute(filepath, "sheet1");
        } catch (TesterraSystemException e) {
            Assert.assertEquals(e.getMessage(),
                    "Cannot read xls(x) file: " + file.getAbsolutePath() + "\\target\\.");
            return;
        }
        Assert.fail("No exception for a not existing document");
    }

    /**
     * Writes new testdata into the documents(.xls or .xlsx) given by the Data Provider
     *
     * @throws IOException Error saving file.
     */
    @Test
    public void testT09F_XLS_Writer_SaveAs_PathNotFound() throws IOException {
        XLSWriter xlsWriter = new XLSWriter();
        String filename = XLS;
        xlsWriter.openFileFromResource(filename, "Tabelle2");
        String price = "20,00";

        xlsWriter.writeCell("writtentestdata", 2, price);

        String outName = "out" + filename;

        File file = new File(".");
        String rootDir = file.getAbsolutePath();
        String fullFileName = rootDir + "\\notexisting\\" + outName;

        try {
            xlsWriter.saveAs(fullFileName);
        } catch (FileNotFoundException e) {
            // ok
            return;
        }
        Assert.fail("No exception for not existing path");
    }
}
