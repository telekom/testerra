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
 package eu.tsystems.mms.tic.testframework.testdata;

import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class XLSTestDataReader extends AbstractXLSIO {

    /**
     * Read data from DataSource.
     *
     * @param fileInResources .
     * @param sheetName .
     * @param id .
     * @return A Map with the read data.
     */
    public Map<String, String> readXLSTestDataFromResource(final String fileInResources, final String sheetName,
            final String id) {
        openFileFromResource(fileInResources, sheetName);
        Map<String, String> readDataSet = readDataSet(id);
        return readDataSet;
    }

    /**
     * Read data from DataSource.
     *
     * @param file .
     * @param sheetName .
     * @param id .
     * @return A Map with the read data.
     */
    public Map<String, String> readXLSTestDataFromFile(final String file, final String sheetName,
            final String id) {
        openFileAbsolute(file, sheetName);
        Map<String, String> readDataSet = readDataSet(id);
        return readDataSet;
    }

    /**
     * Read data set from id
     *
     * @param id to read
     * @return data set for id
     */
    private Map<String, String> readDataSet(final String id) {
        // create formula evaluator object
        formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

        // get header
        Row headerRow = sheet.getRow(this.headerRow - 1);

        if (headerRow == null) {
            throw new SystemException("Header row is empty. Row " + this.headerRow + " (1-based)");
        }

        // find matching row
        Row row = findRowWithIndex(id);

        // Create the dataset
        Map<String, String> dataSet = readDataSet(headerRow, row);

        return dataSet;

    }

    /**
     * Put data of a found row in a dataset structure
     *
     * @param headerRow Row with header information
     * @param row Row to read data from.
     * @return Map mapping header to data.
     */
    private Map<String, String> readDataSet(Row headerRow, Row row) {
        // Create the dataset
        Map<String, String> dataSet = new HashMap<String, String>();

        // this is the data set we are looking for, so grep the data
        String key;
        String value;
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            key = getStringCellValue(headerRow.getCell(i)).trim();
            value = getStringCellValue(row.getCell(i)).trim();
            dataSet.put(key, value);
        }

        return dataSet;
    }

    /**
     * Read a complete sheet of an xls resource file.
     *
     * @param fileInResources name of file in resources.
     * @param sheetName Name of xls sheet.
     * @return List of Maps mapping headers to row values.
     */
    public List<Map<String, String>> readXLSTestDataFromResource(final String fileInResources,
            final String sheetName) {
        return readXLSTestDataFromResource(fileInResources, sheetName, null, null);
    }

    /**
     * Reads a xls(x) resource file from (fromId) to (toId)
     *
     * @param fileInResources name of file in resources.
     * @param sheetName Name of xls sheet.
     * @param fromId Index (1-based) of row to start reading from
     * @param toId Index of row where to stop reading.
     * @return List of Maps mapping headers to row values.
     */
    public List<Map<String, String>> readXLSTestDataFromResource(final String fileInResources,
            final String sheetName, final String fromId, final String toId) {
        openFileFromResource(fileInResources, sheetName);
        return readXLSTestData(fromId, toId);
    }


    /**
     * Read the data from the given file with absolute path
     *
     * @param fileAbsolutePath 	absolute path of file
     * @param sheetName 		Name of xls sheet.
     * @return List of Maps mapping headers to row values.
     */
    public List<Map<String, String>> readXLSTestDataFromFile(final String fileAbsolutePath,
            final String sheetName) {
        return readXLSTestDataFromFile(fileAbsolutePath, sheetName, null, null);
    }

    /**
     * Reads a xls(x) file on the file system from (fromId) to (toId)
     *
     * @param fileAbsolutePath absolute path of file
     * @param sheetName Name of xls sheet.
     * @param fromId Index (1-based) of row to start reading from
     * @param toId Index of row where to stop reading.
     * @return List of Maps mapping headers to row values.
     */
    public List<Map<String, String>> readXLSTestDataFromFile(final String fileAbsolutePath, final String sheetName,
            final String fromId, final String toId) {
        openFileAbsolute(fileAbsolutePath, sheetName);
        return readXLSTestData(fromId, toId);
    }

    /**
     * gets the data from the loaded source by a specification of a row range
     *
     * @param fromId           the row id in the file to start reading
     * @param toId             the row id in the file until which is read
     * @return List<Map<String, String>> with data from the specified rows
     */
    private List<Map<String, String>> readXLSTestData(final String fromId, final String toId) {
        // create formula evaluator object
        formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

        // get header
        Row headerRow = sheet.getRow(this.headerRow - 1);

        // create list
        List<Map<String, String>> dataSetList = new LinkedList<Map<String, String>>();

        // find matching row
        Row row;
        if (fromId == null) {
            if (headerRow != null) {
                if (sheet.getLastRowNum() == headerRow.getRowNum()) {
                	return dataSetList;
                } else {
                    row = sheet.getRow(headerRow.getRowNum() + 1);
                }
            } else {
            	return dataSetList;
            }
        } else {
            row = findRowWithIndex(fromId);
        }

        Map<String, String> dataSet;

        // find next rows
        String currentIndex;
        for (int i = row.getRowNum(); i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);

            dataSet = readDataSet(headerRow, row);
            dataSetList.add(dataSet);

            // get current index
            Cell cell = row.getCell(indexColumn - 1);

            if (cell == null) {
                continue;
            }

            currentIndex = getStringCellValue(cell);

            // stop if last index reached
            if (toId != null && currentIndex.trim().equals(toId.trim())) {
                break;
            }
        }
        return dataSetList;
    }
}
