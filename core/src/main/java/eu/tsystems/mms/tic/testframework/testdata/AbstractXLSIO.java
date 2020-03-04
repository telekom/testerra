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
package eu.tsystems.mms.tic.testframework.testdata;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public abstract class AbstractXLSIO {

    /** Default logger */
    protected static final Logger LOGGER = LoggerFactory.getLogger(XLSTestDataReader.class);
    /** Workbook object for xls(x) file */
    protected Workbook workbook;
    /** Excel formular evaluator. */
    protected FormulaEvaluator formulaEvaluator;
    /** Object representing sheet to read */
    protected Sheet sheet;

    /** sdf for reading dates */
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    protected static final Object LOCK = new Object();

    /**
     * Header row, 1-based.
     */
    protected int headerRow = 1;
    /**
     * Index columns, 1-based.
     */
    protected int indexColumn = 1;

    /**
     * Get the string cell value of any cell type.
     *
     * @param cell .
     * @return String.
     */
    protected String getStringCellValue(final Cell cell) {
        if (cell == null) {
            return "";
        }
        int cellType = cell.getCellType();
        switch (cellType) {
        case Cell.CELL_TYPE_BLANK:
            return "";
        case Cell.CELL_TYPE_BOOLEAN:
            return Boolean.toString(cell.getBooleanCellValue());
        case Cell.CELL_TYPE_ERROR:
            return "Error " + cell.getErrorCellValue();
        case Cell.CELL_TYPE_FORMULA:
            return getCellValue(cell);
        case Cell.CELL_TYPE_NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                // if this is a date cell
                Date date = cell.getDateCellValue();
                return simpleDateFormat.format(date);
            }

            return getCellValue(cell);
        case Cell.CELL_TYPE_STRING:
            return cell.getStringCellValue();
        default:
            return "";
        }
    }

    /**
     * Get the cell calue.
     *
     * @param cell .
     * @return Value.
     */
    protected String getCellValue(final Cell cell) {
        String logPrefix = "Cell (" + (cell.getRowIndex() + 1) + ", " + (cell.getColumnIndex() + 1) + ") - ";
        String dataFormatString = cell.getCellStyle().getDataFormatString();
        String cellValue;

        if (dataFormatString != null) {
            DataFormatter dataFormatter = new DataFormatter();
            if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                LOGGER.debug(logPrefix + "Found formula: " + cell.getCellFormula());
                cellValue = dataFormatter.formatCellValue(cell, formulaEvaluator);
            }
            else {
                cellValue = dataFormatter.formatCellValue(cell);
            }
        }
        else {
            cellValue = cell.getNumericCellValue() + "";
        }

        return reformatCellValue(cellValue);
    }

    /**
     * Simple string replacements for weird formattings.
     *
     * @param cellValue .
     * @return Re-formatted value.
     */
    protected String reformatCellValue(final String cellValue) {
        return cellValue.replace("[$EUR]", "EUR");
    }

    public int getHeaderRow() {
        return headerRow;
    }

    /**
     * Set Header row, 1-based.
     *
     * @param headerRow .
     */
    public void setHeaderRow(int headerRow) {
        this.headerRow = headerRow;
    }

    public int getIndexColumn() {
        return indexColumn;
    }

    /**
     * Set index column, 1-based.
     *
     * @param indexColumn .
     */
    public void setIndexColumn(int indexColumn) {
        this.indexColumn = indexColumn;
    }

    /**
     * TODO always returns null?
     *
     * Open xls file from resources.
     *
     * @param fileInResources filename
     * @param sheetName Sheet of xls(x) to read from
     * @return XLSWriter
     */
    protected XLSWriter openFileFromResource(final String fileInResources, final String sheetName) {
        LOGGER.info("Reading from " + fileInResources);
        synchronized (LOCK) {
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(fileInResources);

            if (resourceAsStream == null) {
                throw new TesterraSystemException("Error reading resource file " + fileInResources);
            }

            try {
                workbook = WorkbookFactory.create(resourceAsStream);
            } catch (IOException e) {
                throw new TesterraSystemException("Cannot read xls(x) file: " + fileInResources, e);
            } catch (InvalidFormatException e) {
                throw new TesterraSystemException("Cannot read xls(x) file: " + fileInResources, e);
            }
            // Get data sheet by name
            sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new TesterraSystemException("No sheet with name " + sheetName + " found.");
            }
            return null;
        }
    }

    /**
     * TODO always returns null?
     *
     * Open xls file from absolute path.
     *
     * @param filename filename
     * @param sheetName Sheet of xls(x) to read from
     * @return XLSWriter
     */
    protected XLSWriter openFileAbsolute(final String filename, final String sheetName) {
        LOGGER.info("Reading from " + filename);
        synchronized (LOCK) {
            try {
                FileInputStream fileInputStream = new FileInputStream(filename);
                workbook = WorkbookFactory.create(fileInputStream);
            } catch (IOException e) {
                throw new TesterraSystemException("Cannot read xls(x) file: " + filename, e);
            } catch (InvalidFormatException e) {
                throw new TesterraSystemException("Cannot read xls(x) file: " + filename, e);
            }
            // Get data sheet by name
            sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new TesterraSystemException("No sheet with name " + sheetName + " found.");
            }

            return null;
        }
    }

    /**
     * Iterates through all rows until one with the given id was found (in index column)
     *
     * @param id Id to search for.
     * @return Row object representing xls(x) row.
     */
    protected Row findRowWithIndex(final String id) {
        // get row iterator
        Iterator<Row> rowIterator = sheet.rowIterator();
        Row row;
        String currentIndex;
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            Cell cell = row.getCell(indexColumn - 1);

            if (cell == null) {
                continue;
            }

            currentIndex = getStringCellValue(cell);

            if (currentIndex.trim().equals(id.trim())) {
                return row;
            }
        }
        throw new TesterraSystemException("Could not find a dataset for >" + id + "< in column " + indexColumn
                + " in current worksheet.");
    }

    public SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    public void setSimpleDateFormat(SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public Workbook getWorkbook() {
        return workbook;
    }
}
