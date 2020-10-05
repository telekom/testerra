/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.action;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.AbstractPage;
import java.lang.reflect.Field;

public abstract class AbstractFieldAction {

    protected final Field field;
    protected final AbstractPage declaringPage;

    public AbstractFieldAction(Field field, AbstractPage declaringPage) {
        this.field = field;
        this.declaringPage = declaringPage;
    }

    protected abstract boolean before();

    protected abstract void execute();

    protected abstract void after();

    public void run() {
        if (before()) {
            execute();
        }
        after();
    }

}
