/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.test.testdata;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.test.testdata.implementation.TestCsvReaderBean;
import eu.tsystems.mms.tic.testframework.test.testdata.implementation.TestCsvReaderBeanWithSubBean;
import eu.tsystems.mms.tic.testframework.test.testdata.implementation.TestCsvReaderSubBean;
import eu.tsystems.mms.tic.testframework.testdata.CSVTestDataReader;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * CSVReaderTest
 * <p>
 * Date: 18.09.2019
 * Time: 08:48
 *
 * @author Eric.Kubenka
 */
public class CSVReaderTest {

    @Test
    public void testT01_CsvReader() {

        final CSVTestDataReader csvTestDataReader = new CSVTestDataReader();
        csvTestDataReader.setHeaderRow(1);

        final List<Map<String, String>> testDataMap = csvTestDataReader.readCsvTestDataFromResource("testfiles/TestCsvReader.csv");

        Assert.assertEquals(testDataMap.size(), 2);
        Assert.assertEquals(testDataMap.get(0).get("id"), "AH_1");
        Assert.assertEquals(testDataMap.get(1).get("id"), "AH_2");
    }

    @Test
    public void testT02_CsvReader_FileNotFound() {

        final CSVTestDataReader csvTestDataReader = new CSVTestDataReader();
        csvTestDataReader.setHeaderRow(1);

        TesterraSystemException testerraSystemException = null;

        try {
            csvTestDataReader.readCsvTestDataFromResource("testfiles/Not_existing.csv");
        } catch (TesterraSystemException e) {
            testerraSystemException = e;
        }

        Assert.assertNotNull(testerraSystemException, "Exception thrown.");
        Assert.assertTrue(testerraSystemException.getMessage().contains("Could not load resource file"));
    }

    @Test
    public void testT03_CsvBeanReader() {

        final CSVTestDataReader csvTestDataReader = new CSVTestDataReader();
        csvTestDataReader.setHeaderRow(1);

        final List<TestCsvReaderBean> testDataList = csvTestDataReader.readCsvIntoBeans("testfiles/TestCsvReader.csv", TestCsvReaderBean.class);

        Assert.assertNotNull(testDataList);
        Assert.assertNotEquals(testDataList.size(), 0);

        final TestCsvReaderBean testCSVReader = testDataList.get(0);

        final String id = testCSVReader.getId();
        Assert.assertEquals(id, "AH_1");
    }

    @Test
    public void testT04_CsvBeanReaderWithSubBean() {

        final CSVTestDataReader csvTestDataReader = new CSVTestDataReader();
        csvTestDataReader.setHeaderRow(0);

        final List<TestCsvReaderBeanWithSubBean> testDataList = csvTestDataReader.readCsvIntoBeans("testfiles/TestCsvReaderBeanWithSubBean.csv", TestCsvReaderBeanWithSubBean.class);
        Assert.assertNotNull(testDataList);
        Assert.assertNotEquals(testDataList.size(), 0);

        final TestCsvReaderBeanWithSubBean testCsvReaderBeanWithSubBean = testDataList.get(0);
        Assert.assertEquals(testCsvReaderBeanWithSubBean.getId(), "AH_1");

        Assert.assertNotNull(testCsvReaderBeanWithSubBean.getSubBean());
        final TestCsvReaderSubBean subBean = testCsvReaderBeanWithSubBean.getSubBean();

        Assert.assertEquals(subBean.getId(), "SUB_1");
        Assert.assertEquals(subBean.getCity(), "Dresden");
    }
}
