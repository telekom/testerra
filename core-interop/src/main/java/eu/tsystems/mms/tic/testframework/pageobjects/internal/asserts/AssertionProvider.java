/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

/**
 * Provides properties for further assertion
 * @author Mike Reiche
 */
public abstract class AssertionProvider<T> implements ActualProperty<T> {
    @Override
    abstract public T getActual();

    abstract public String getSubject();

    /**
     * This method will be called recursive from bottom to top
     * if one of the assertions failed.
     * @param assertion The failed assertion
     */
    public void failed(AbstractPropertyAssertion<T> assertion) {
    }

    /**
     * This method will be called recursive from bottom to top
     * if one of the assertions finally failed.
     * @param assertion The failed assertion
     */
    public void failedFinally(AbstractPropertyAssertion<T> assertion) {
    }
}
