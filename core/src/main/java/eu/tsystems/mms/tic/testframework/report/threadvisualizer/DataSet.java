/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann
 *     pele
*/
package eu.tsystems.mms.tic.testframework.report.threadvisualizer;

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

    /** Start of thread */
    private Long startTime;
    /** End of Threa. */
    private Long stopTime;
    /** HTML content to print out. */
    private String content;
    /** Name of thread. */
    private String threadName;

    /**
     * Constructor creating a Data Set for the ThreadVisualizer without endTime.
     *
     * @param threadName Name of thread.
     * @param content HTML content representing the data.
     * @param startTime Start of thread.
     */
    public DataSet(final String threadName, final String content, final Long startTime) {
        this.threadName = threadName;
        this.content = content;
        this.startTime = startTime;
    }

    /**
     * Constructor creating a Data Set for the ThreadVisualizer.
     *
     * @param threadName Name of thread.
     * @param content HTML content representing the data.
     * @param startTime Start of thread.
     * @param stopTime End of Thread.
     */
    public DataSet(final String threadName, final String content, final Long startTime, final Long stopTime) {
        this.threadName = threadName;
        this.content = content;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    /**
     * Format String for html.
     *
     * @param s String to format.
     * @return formatted String.
     */
    private String format(final String s) {
        return s.replaceAll("'", "\\\\'");
    }

    public String getContent() {
        return format(content);
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getStopTime() {
        return stopTime;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public void setStartTime(final Long startTime) {
        this.startTime = startTime;
    }

    public void setStopTime(final Long stopTime) {
        this.stopTime = stopTime;
    }

    public void setThreadName(final String threadName) {
        this.threadName = threadName;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
