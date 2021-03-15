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

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import org.testng.ITestResult;

/**
 * Template for Maven Test Listeners used by Testerra. Methods that need to be implemented by subclasses are abstract, other
 * inherited Methods of JUnit and TestNg are empty, so the implementing classes don't need to implement them, cause we
 * don't use them.
 *
 * @author mrgi, sepr, erku
 */
public abstract class AbstractCommonSynchronizer implements Loggable, MethodEndEvent.Listener {

    /**
     * The type mapping to Quality Center.
     */
    protected static SyncType syncType;

    /**
     * Is Synchronization turned on or off?
     */
    protected static boolean isSyncActive;

    @SuppressWarnings("static-access")
    public void setSyncType(final SyncType synctype) {
        this.syncType = synctype;
    }

    @Subscribe
    @Override
    public void onMethodEnd(MethodEndEvent event) {
        final ITestResult currentTestResult = event.getTestResult();

        switch (currentTestResult.getStatus()) {
            case ITestResult.SUCCESS:

                pOnTestSuccess(event);
                break;
            case ITestResult.FAILURE:
                pOnTestFailure(event);
                break;
            case ITestResult.SKIP:
                pOnTestSkip(event);
                break;
            default:
                log().warn("unsupported test method result");
        }
    }

    protected abstract void pOnTestSuccess(MethodEndEvent event);

    protected abstract void pOnTestFailure(MethodEndEvent event);

    protected void pOnTestSkip(MethodEndEvent event) {

        /* do nothing at default */
    }
}
