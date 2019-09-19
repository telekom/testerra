/*
 * Created on 18.09.2019
 */
package eu.tsystems.mms.tic.testframework.helper;

/**
 * TestCSVReader1
 * <p>
 * Date: 18.09.2019
 * Time: 09:21
 *
 * @author Eric.Kubenka <Eric.Kubenka@t-systems.com>
 */
public class TestCsvReaderBeanWithSubBean {

    private String id;
    private String name;
    private String firstName;
    private TestCsvReaderSubBean subBean;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public TestCsvReaderSubBean getSubBean() {
        return subBean;
    }

    public void setSubBean(TestCsvReaderSubBean subBean) {
        this.subBean = subBean;
    }
}
