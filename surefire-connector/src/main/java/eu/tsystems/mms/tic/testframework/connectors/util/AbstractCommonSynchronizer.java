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
package eu.tsystems.mms.tic.testframework.connectors.util;

import eu.tsystems.mms.tic.testframework.events.ITesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.ITesterraEventType;
import eu.tsystems.mms.tic.testframework.events.TesterraEvent;
import eu.tsystems.mms.tic.testframework.events.TesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEventListener;
import eu.tsystems.mms.tic.testframework.events.TesterraEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import java.util.Map;

/**
 * Template for Maven Test Listeners used by Testerra. Methods that need to be implemented by subclasses are abstract, other
 * inherited Methods of JUnit and TestNg are empty, so the implementing classes don't need to implement them, cause we
 * don't use them.
 *
 * @author mrgi, sepr, erku
 */
public abstract class AbstractCommonSynchronizer implements TesterraEventListener {

    /**
     * The type mapping to Quality Center.
     */
    protected static SyncType syncType;

    /**
     * Is Synchronization turned on or off?
     */
    protected static boolean isSyncActive;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("static-access")
    public void setSyncType(final SyncType synctype) {
        this.syncType = synctype;
    }

    @Override
    public void fireEvent(TesterraEvent testerraEvent) {

        final ITesterraEventType ttEventType = testerraEvent.getTesterraEventType();
        if (ttEventType == TesterraEventType.SYNC_METHOD_RESULT) {

            final Map<ITesterraEventDataType, Object> data = testerraEvent.getData();
            final ITestResult currentTestResult = (ITestResult) data.get(TesterraEventDataType.ITestResult);

            switch (currentTestResult.getStatus()) {
                case ITestResult.SUCCESS:
                    pOnTestSuccess(currentTestResult);
                    break;
                case ITestResult.FAILURE:
                    pOnTestFailure(currentTestResult);
                    break;
                case ITestResult.SKIP:
                    pOnTestSkip(currentTestResult);
                    break;
                default:
                    logger.warn("unsupported test method result");
            }
        }
    }

    protected abstract void pOnTestSuccess(ITestResult testResult);

    protected abstract void pOnTestFailure(ITestResult testResult);

    protected void pOnTestSkip(ITestResult testResult) {

        /* do nothing at default */
    }
}
