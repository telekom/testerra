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
package eu.tsystems.mms.tic.testframework.test.testdata.implementation;

/**
 * TestCSVReader1
 * <p>
 * Date: 18.09.2019
 * Time: 09:21
 *
 * @author Eric.Kubenka
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
