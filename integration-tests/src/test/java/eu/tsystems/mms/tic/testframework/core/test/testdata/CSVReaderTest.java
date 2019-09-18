/*
 * Created on 18.09.2019
 */
package eu.tsystems.mms.tic.testframework.core.test.testdata;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.helper.TestCsvReaderBean;
import eu.tsystems.mms.tic.testframework.helper.TestCsvReaderBeanWithSubBean;
import eu.tsystems.mms.tic.testframework.helper.TestCsvReaderSubBean;
import eu.tsystems.mms.tic.testframework.testdata.CSVTestDataReader;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * CSVReaderTest
 * <p>
 * Date: 18.09.2019
 * Time: 08:48
 *
 * @author Eric.Kubenka <Eric.Kubenka@t-systems.com>
 */
public class CSVReaderTest {

    @Test
    public void testT01_CsvReader() {

        final CSVTestDataReader csvTestDataReader = new CSVTestDataReader();
        csvTestDataReader.setHeaderRow(1);

        final List<Map<String, String>> testDataMap = csvTestDataReader.readCsvTestDataFromResource("testfiles/TestCSVReader.csv");

        Assert.assertEquals(testDataMap.size(), 3);
        Assert.assertEquals(testDataMap.get(0).get("id"), "testdata");
        Assert.assertEquals(testDataMap.get(1).get("id"), "testdata2");
        Assert.assertEquals(testDataMap.get(2).get("id"), "testdata3");
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

        final List<TestCsvReaderBean> testDataList = csvTestDataReader.readCsvIntoBeans("testfiles/TestCSVReader.csv", TestCsvReaderBean.class);

        Assert.assertNotNull(testDataList);
        Assert.assertNotEquals(testDataList.size(), 0);

        final TestCsvReaderBean testCSVReader = testDataList.get(0);
        final String serviceNumber = testCSVReader.getServiceNumber();

        Assert.assertEquals(serviceNumber, "8001888972600");
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
