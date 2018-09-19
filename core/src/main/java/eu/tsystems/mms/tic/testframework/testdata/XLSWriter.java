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
 * Created on 21.10.2013
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.testdata;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class XLSWriter extends AbstractXLSIO {

    @Override
    public XLSWriter openFileFromResource(String fileInResources, String sheetName) {
        super.openFileFromResource(fileInResources, sheetName);
        return this;
    }

    @Override
    public XLSWriter openFileAbsolute(String filename, String sheetName) {
        super.openFileAbsolute(filename, sheetName);
        return this;
    }

    /**
     * Get 0-based row.
     *
     * @return last row number
     */
    public int getLastRowNumber() {
        return sheet.getLastRowNum();
    }

    /**
     * Write new values to the sheet;
     *
     * @param id Id.
     * @param columnNumber 0-based column.
     * @param value Value.
     * @return this.
     */
    public XLSWriter writeCell(final String id, final int columnNumber, final String value) {
        Row row = findRowWithIndex(id);

        Cell cell = row.createCell(columnNumber);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(value);

        return this;
    }

    public XLSWriter writeCell(final int rowNumber, final int columnNumber, final String value) {
        Row row = sheet.getRow(rowNumber);
        if (row == null) {
            sheet.createRow(rowNumber);
        }
        Cell cell = row.getCell(columnNumber);
        if (cell == null) {
            row.createCell(columnNumber);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(value);

        return this;
    }

    /**
     * Save file.
     *
     * @param filename .
     * @throws IOException file saved or not
     */
    public void saveAs(final String filename) throws IOException {
        LOGGER.info("Writing to " + filename);
        synchronized (LOCK) {
            FileOutputStream fos = new FileOutputStream(filename);
            workbook.write(fos);
            fos.flush();
            fos.close();
        }
    }
}
