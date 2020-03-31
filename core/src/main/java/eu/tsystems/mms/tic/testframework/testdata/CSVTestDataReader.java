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

import com.opencsv.CSVReader;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This class ...
 *
 * @author fakr
 */
public class CSVTestDataReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVTestDataReader.class);

    private final static char DEFAULT_SEPARATOR = ';';
    private final static char DEFAULT_QUOTE_CHAR = '\"';

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


    public List<Map<String, String>> readCsvTestDataFromResource(String fileInResources) {

        // init values
        final List<Map<String, String>> dataSetsList = new LinkedList<>();

        // init reader
        final List<String[]> content = this.readAllLinesFromCsvFile(fileInResources);
        final String[] headerRow = content.get(this.headerRow);
        final int nrOfHeaders = headerRow.length;

        for (int j = this.headerRow + 1; j < content.size(); j++) {

            final String[] cells = content.get(j);
            /*
            Exit with clear error print out
             */
            if (cells.length != nrOfHeaders) {

                final StringBuilder message = new StringBuilder("Could not read " + fileInResources + "\nline: ");
                for (String cell : cells) {
                    message.append(cell).append(separator);
                }
                LOGGER.error(message.toString());
            } else {
                final Map<String, String> dataSet = new LinkedHashMap<>();
                for (int i = 0; i < nrOfHeaders; i++) {
                    dataSet.put(headerRow[i], cells[i]);
                }
                dataSetsList.add(dataSet);
            }
        }

        return dataSetsList;
    }

    /**
     * reads specified csv file from resources and creates beans of type clazz from it
     * note: csv file needs to have the same name as the corresponding class,
     * fieldnames of the class and columnames in the csv should be identical (order of the columns does not matter)
     * the class should have getter and setter for each field
     *
     * @param fileInResources name of the csv file in resources
     * @param clazz           class to create beans from
     *
     * @return list with created beans
     */
    public <T> List<T> readCsvIntoBeans(final String fileInResources, final Class<T> clazz) {

        final List<String[]> content = this.readAllLinesFromCsvFile(fileInResources);
        final List<String> header = Arrays.asList(content.get(this.headerRow));

        if (this.headerRow >= 0) {
            content.subList(0, this.headerRow + 1).clear();
        }

        return content.stream()
                .map(arr -> createBeanFromLine(clazz, Arrays.asList(arr), header))
                .collect(Collectors.toList());
    }

    private String findFileInResources(String filename) {

        Reflections reflections = new Reflections("", new ResourcesScanner());
        Set<String> resourceList = reflections.getResources(x -> true);
        return resourceList.stream()
                .filter(path -> path.contains(filename))
                .findFirst()
                .orElse(null);
    }

    private FileReader openFileFromResources(String fileInResources) {

        final File resourceFile = FileUtils.getResourceFile(fileInResources);
        FileReader resourceFileReader = null;
        try {
            resourceFileReader = new FileReader(resourceFile);
        } catch (FileNotFoundException e) {
            Assert.fail("Could not read file: " + fileInResources);
        }
        return resourceFileReader;
    }


    /**
     * creates a bean object with given identifier from specified csv file of type clazz
     * note: csv file needs to have the same name as the corresponding class,
     * fieldnames of the class and columnames in the csv should be identical (order of the columns does not matter)
     * the class should have getter and setter for each field
     *
     * @param fileInResources name of the csv file in resources
     * @param clazz           class to create beans from
     * @param identifier      identifier to find the correct line in the file
     *
     * @return an object of type class with given identifier
     */
    private <T> T readLineIntoBean(String fileInResources, Class<T> clazz, String identifier) {

        LOGGER.info(String.format("Reading line with %s from %s", identifier, fileInResources));

        final List<String[]> reader = readAllLinesFromCsvFile(fileInResources);
        final Iterator<String[]> ite = reader.iterator();
        final List<String> headers = Arrays.asList(ite.next());
        final Iterable<String[]> iterable = () -> ite;

        final List<String> correctLine = StreamSupport.stream(iterable.spliterator(), true)
                .map(Arrays::asList)
                .filter(line -> line.contains(identifier))
                .findFirst()
                .orElseThrow(() -> new TesterraRuntimeException(String.format("No line with identifier '%s' found in %s", identifier, fileInResources)));

        return createBeanFromLine(clazz, correctLine, headers);
    }

    private Object handleField(Class type, String value) {
        Object obj;
        if (type.isEnum()) {
            obj = Enum.valueOf(type, value);
        } else {
            try {
                /* try to use Constructor with String as parameter */
                Constructor constructFromString = type.getConstructor(String.class);
                obj = constructFromString.newInstance(value);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                /* searching for another csv in resources to create an object from */
                String path = findFileInResources(type.getSimpleName() + ".csv");
                if (path != null) {
                    /* create object from csv */
                    obj = readLineIntoBean(path, type, value);
                } else {
                    /* create empty object with default constructor */
                    try {
                        obj = type.newInstance();
                    } catch (InstantiationException | IllegalAccessException e1) {
                        throw new TesterraRuntimeException(e1);
                    }
                }
            }
        }
        return obj;
    }

    private <T> T createBeanFromLine(Class<T> clazz, List<String> line, List<String> header) {

        LOGGER.info(String.format("Creating Bean of %s with following data: %s", clazz.getSimpleName(), String.join(",", line)));

        T bean;
        try {
            bean = clazz.newInstance();
            for (int col = 0; col < header.size(); col++) {

                final String headerName = header.get(col);

                if (col >= line.size()) {
                    break;
                }

                BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
                PropertyDescriptor propDesc = Arrays.stream(beanInfo.getPropertyDescriptors())
                        .filter(descriptor -> descriptor.getDisplayName().equals(headerName))
                        .findFirst()
                        .orElseThrow(() -> new TesterraRuntimeException(String.format("No property with name %s found", headerName)));
                Method setter = propDesc.getWriteMethod();
                Class type = propDesc.getPropertyType();
                Object obj = handleField(type, line.get(col));
                setter.invoke(bean, obj);
            }
        } catch (InstantiationException | IllegalAccessException | IntrospectionException | InvocationTargetException e) {
            throw new TesterraRuntimeException(e);
        }

        return bean;
    }

    private List<String[]> readAllLinesFromCsvFile(final String fileName) {

        LOGGER.info(String.format("Reading from %s", fileName));

        final FileReader fileReader = this.openFileFromResources(fileName);
        final CSVReader csvReader = new CSVReader(fileReader, getSeparator(), getQuoteChar(), getSkippedLines());

        try {
            return csvReader.readAll();
        } catch (IOException e) {
            throw new TesterraRuntimeException(String.format("Could not read csv file in resources %s.", fileName), e);
        }
    }
}
