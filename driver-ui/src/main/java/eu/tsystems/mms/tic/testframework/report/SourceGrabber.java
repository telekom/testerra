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
 package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.interop.SourceCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.AbstractPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource;
import eu.tsystems.mms.tic.testframework.utils.SourceUtils;

public class SourceGrabber implements SourceCollector {

    @Override
    public ScriptSource getSourceFor(Throwable throwable) {
        // look for guielement error
        ScriptSource source = SourceUtils.findSourceForThrowable(throwable, GuiElement.class, Page.class);

        // look for abstract page error
        if (source == null) {
            source = SourceUtils.findSourceForThrowable(throwable, AbstractPage.class, Page.class);
        }

        return source;
    }

}
