/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.report.threadvisualizer;

import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA. User: peter Date: 16.12.12 Time: 14:13 To change this template use File | Settings | File
 * Templates.
 */
public class DataSet implements Serializable {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = -8406209532487083720L;

    /**
     * Start of thread
     */
    private Long startTime;
    /**
     * End of thread
     */
    private Long stopTime;
    /**
     * MethodContext of test method
     */
    private MethodContext context;

    /**
     * Constructor creating a Data Set for the ThreadVisualizer.
     *
     * @param context   MethodContext of test.
     * @param startTime Start of thread.
     * @param stopTime  End of Thread.
     */
    public DataSet(final MethodContext context, final Long startTime, final Long stopTime) {
        this.context = context;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getStopTime() {
        return stopTime;
    }

    public void setStartTime(final Long startTime) {
        this.startTime = startTime;
    }

    public void setStopTime(final Long stopTime) {
        this.stopTime = stopTime;
    }

    public MethodContext getContext() {
        return context;
    }

    public void setContext(final MethodContext context) {
        this.context = context;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
