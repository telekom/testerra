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
 * Created on 27.02.2017
 *
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */

package eu.tsystems.mms.tic.testframework.testdata;

import com.opencsv.CSVReader;
import eu.tsystems.mms.tic.testframework.exceptions.fennecRuntimeException;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class ...
 *
 * @author fakr
 */
public class CSVTestDataReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVTestDataReader.class);

    final static char DEFAULT_SEPARATOR = ';';
    final static char DEFAULT_QUOTE_CHAR = '\"';

    private char separator = DEFAULT_SEPARATOR;
    private char quoteChar = DEFAULT_QUOTE_CHAR;

    // TODO: implement
    private boolean usingQuotes = false;
    private int skippedLines = 0;
    private int headerRow = 0;

    public CSVTestDataReader() {
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public char getQuoteChar() {
        return quoteChar;
    }

    public void setQuoteChar(char quoteChar) {
        this.quoteChar = quoteChar;
    }

    public boolean isUsingQuotes() {
        return usingQuotes;
    }

    public void setUsingQuotes(boolean usingQuotes) {
        this.usingQuotes = usingQuotes;
    }

    public int getSkippedLines() {
        return skippedLines;
    }

    public void setSkippedLines(int skippedLines) {
        this.skippedLines = skippedLines;
    }

    public int getHeaderRow() {
        return headerRow;
    }

    public void setHeaderRow(int headerRow) {
        this.headerRow = headerRow;
    }

    private FileReader openFileFromResources(String fileInResources) {
        File resourceFile = FileUtils.getResourceFile(fileInResources);
        FileReader resourceFileReader = null;
        try {
            resourceFileReader = new FileReader(resourceFile);
        } catch (FileNotFoundException e) {
            Assert.fail("Could not read file: " + fileInResources);
        }
        return resourceFileReader;
    }

    public List<Map<String, String>> readCSVTestDataFromResource(String fileInResources) {
        List<Map<String, String>> dataSetsList = new LinkedList<>();
        FileReader fileReader = openFileFromResources(fileInResources);
        CSVReader reader = new com.opencsv.CSVReader(fileReader, getSeparator(), getQuoteChar(), getSkippedLines());

        List<String[]> allDataSets;

        try {
            allDataSets = reader.readAll();
        } catch (IOException e) {
            throw new fennecRuntimeException("Could not read data from: " + fileInResources);
        }

        int headerRowNr = getHeaderRow();
        String[] headerRow = allDataSets.get(headerRowNr);
        int nrOfHeaders = headerRow.length;

        for (int j = headerRowNr + 1; j < allDataSets.size(); j++) {
            String[] cells = allDataSets.get(j);
            /*
            Exit with clear error print out
             */
            if (cells.length != nrOfHeaders) {
                String message = "Could not read " + fileInResources + "\nline: ";
                for (String cell : cells) {
                    message += cell + separator;
                }
                LOGGER.error(message);
            }
            else {
                Map<String, String> dataSet = new LinkedHashMap<>();
                for (int i = 0; i < nrOfHeaders; i++) {
                    dataSet.put(headerRow[i], cells[i]);
                }
                dataSetsList.add(dataSet);
            }
        }

        return dataSetsList;
    }

}
