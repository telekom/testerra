/*
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

package eu.tsystems.mms.tic.testframework.core.test.testdata;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.testdata.XLSTestDataReader;
import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Test for reading data out of excel sheets.
 *
 * @author pele/expanded by pimi
 */
public class XLSReaderTest extends AbstractWebDriverTest {

    /**
     * Data provider for reading multiple documents from resources.
     *
     * @return document(s) of choose
     */
    @DataProvider(name = "FileType")
    public Object[][] createData1() {
        return new Object[][] {
                {"testfiles/TestXLSReader.xls"},
                {"testfiles/TestXLSReader.xlsx"},
        };
    }

    /**
     * Reads a complete sheet of a .xls(x) file
     *
     * @param filename gets xls(x) document(s) given by DataProvider
     */
    @Test(dataProvider = "FileType")
    public void testT01_XlsReader_FileBulk(String filename) {
        XLSTestDataReader xlsTestDataReader = new XLSTestDataReader();
        xlsTestDataReader.setHeaderRow(3);
        List<Map<String, String>> list = xlsTestDataReader.readXLSTestDataFromResource(filename, "Sheet1");
        Assert.assertEquals(list.size(), 3);
        Assert.assertEquals(list.get(0).get("ID"), "testdata");
        Assert.assertEquals(list.get(1).get("ID"), "testdata2");
        Assert.assertEquals(list.get(2).get("ID"), "testdata3");
    }

    /**
     * Reads a number of rows of a .xls(x) limited by "fromId" (row from which to start) and "toId" (row where to end)
     *
     * @param filename gets xls(x) document(s) given by DataProvider
     */
    @Test(dataProvider = "FileType")
    public void testT02_XlsReader_FileFromTo(String filename) {
        XLSTestDataReader xlsTestDataReader = new XLSTestDataReader();
        xlsTestDataReader.setHeaderRow(3);
        List<Map<String, String>> dataSetList = xlsTestDataReader.readXLSTestDataFromResource(filename, "Sheet1",
                "testdata2", "testdata3");
        Assert.assertEquals(dataSetList.size(), 2);
        Assert.assertEquals(dataSetList.get(0).get("ID"), "testdata2");
        Assert.assertEquals(dataSetList.get(1).get("ID"), "testdata3");
    }

    /**
     * Verification test for Numbers.
     *
     * @param filename gets xls(x) document(s) given by DataProvider
     */
    @Test(dataProvider = "FileType")
    public void testT03_XlsReader_Number(String filename) {
        XLSTestDataReader xlsTestDataReader = new XLSTestDataReader();
        xlsTestDataReader.setHeaderRow(3);
        Map<String, String> dataSet = xlsTestDataReader.readXLSTestDataFromResource(filename, "Sheet1",
                "testdata");
        Assert.assertEquals(dataSet.get("Service Number"), "8001888972600", "Content verification of number");
    }

    /**
     * Verification test for Texts.
     *
     * @param filename gets xls(x) document(s) given by DataProvider
     */
    @Test(dataProvider = "FileType")
    public void testT04_XlsReader_Text(String filename) {
        XLSTestDataReader xlsTestDataReader = new XLSTestDataReader();
        xlsTestDataReader.setHeaderRow(3);
        Map<String, String> dataSet = xlsTestDataReader.readXLSTestDataFromResource(filename, "Sheet1",
                "testdata");
        Assert.assertEquals(dataSet.get("Product Name"), "iPhone 16GB", "Content verification of text");
    }

    /**
     * Verification test for Dates.
     *
     * @param filename gets xls(x) document(s) given by DataProvider
     */
    @Test(dataProvider = "FileType")
    public void testT05_XlsReader_Date(String filename) {
        XLSTestDataReader xlsTestDataReader = new XLSTestDataReader();
        xlsTestDataReader.setHeaderRow(3);
        Map<String, String> dataSet = xlsTestDataReader.readXLSTestDataFromResource(filename, "Sheet1",
                "testdata");
        Assert.assertEquals(dataSet.get("Date"), "01.08.2014", "Content verification of date");
    }

    /**
     * Verification test for Currencies.
     *
     * @param filename gets xls(x) document(s) given by DataProvider
     */
    @Test(dataProvider = "FileType")
    public void testT06_XlsReader_Currency(String filename) {
        XLSTestDataReader xlsTestDataReader = new XLSTestDataReader();
        xlsTestDataReader.setHeaderRow(3);
        Map<String, String> dataSet = xlsTestDataReader.readXLSTestDataFromResource(filename, "Sheet1",
                "testdata");
        Assert.assertEquals(dataSet.get("Price").replace(".", ","), "10,00 EUR", "Content verification of currency");
    }

    /**
     * Verification test for Formulas.
     *
     * @param filename gets xls(x) document(s) given by DataProvider
     */
    @Test(dataProvider = "FileType")
    public void testT07_XlsReader_Formula(String filename) {
        XLSTestDataReader xlsTestDataReader = new XLSTestDataReader();
        xlsTestDataReader.setHeaderRow(3);
        Map<String, String> dataSet = xlsTestDataReader.readXLSTestDataFromResource(filename, "Sheet1",
                "testdata");
        Assert.assertEquals(dataSet.get("Calculate").replace(".", ","), "15,00 EUR", "Content verification of mathematical formula");
    }

    /**
     * Negativ test case for an emty header row.
     *
     * @param filename gets xls(x) document(s) given by DataProvider
     */
    @Test(dataProvider = "FileType")
    public void testT08F_XlsReader_EmptyHeaderRow(String filename) {
        XLSTestDataReader xlsTestDataReader = new XLSTestDataReader();
        xlsTestDataReader.setHeaderRow(2);
        try {
            @SuppressWarnings("unused")
            Map<String, String> dataSet = xlsTestDataReader.readXLSTestDataFromResource(filename, "Sheet1",
                    "testdata");
        } catch (TesterraSystemException e) {
            Assert.assertEquals(e.getMessage(), "Header row is empty. Row 2 (1-based)");
            return;
        }
        Assert.fail("No exception for an empty header row");

    }

    /**
     * Negativ test case for a not existing file in resources.
     *
     */
    @Test
    public void testT09F_XlsReader_FileNotFoundInResources() {
        XLSTestDataReader xlsTestDataReader = new XLSTestDataReader();
        xlsTestDataReader.setHeaderRow(2);
        String file = "testfiles/notexisting.xls";
        try {
            @SuppressWarnings("unused")
            Map<String, String> dataSet = xlsTestDataReader.readXLSTestDataFromFile(file, "Sheet1",
                    "testdata");
        } catch (TesterraSystemException e) {
            String msg = "Cannot read xls(x) file: " + file;
            String message = e.getMessage();
            AssertUtils.assertContains(message, msg);
            return;
        }
        Assert.fail("No exception for not existing files");
    }

    /**
     * Negativ test case for a not existing file in any location.
     *
     */
    @Test
    public void testT10F_XlsReader_FileNotFoundAbsolutePath() {
        XLSTestDataReader xlsTestDataReader = new XLSTestDataReader();
        xlsTestDataReader.setHeaderRow(2);
        File file = new File("./NonExisting.xls");
        String rootDir = file.getAbsolutePath();
        String filepath = rootDir + "\\target\\" + file;
        try {
            @SuppressWarnings("unused")
            Map<String, String> dataSet = xlsTestDataReader.readXLSTestDataFromFile(filepath, "Sheet1",
                    "testdata");
        } catch (TesterraSystemException e) {
            String msg = "Cannot read xls(x) file:";
            String message = e.getMessage();
            AssertUtils.assertContains(message, msg);
            return;
        }
        Assert.fail("No exception for not existing files");
    }

    /**
     * Negativ test case for an emty header row.
     *
     * @param filename gets xls(x) document(s) given by DataProvider
     */
    @Test(dataProvider = "FileType")
    public void testT11F_XlsReader_NotExistingSheet(String filename) {
        XLSTestDataReader xlsTestDataReader = new XLSTestDataReader();
        try {
            @SuppressWarnings("unused")
            Map<String, String> dataSet = xlsTestDataReader.readXLSTestDataFromResource(filename, "Seite1",
                    "testdata");
        } catch (TesterraSystemException e) {
            Assert.assertEquals(e.getMessage(), "No sheet with name Seite1 found.", "Sheet not found.");
            return;
        }
        Assert.fail("No exception for a not existing sheet");
    }
}
