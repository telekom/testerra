/*
 * Testerra
 *
 * (C) 2020, Mike Beuthan, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.OutputStreamManager;
import org.apache.logging.log4j.core.layout.ByteBufferDestination;

import java.io.OutputStream;

/**
 * Manages an OutputStream so that it can be shared by multiple Appenders and will
 * allow appenders to reconfigure without requiring a new stream.
 */
public class DefaultLogAppenderOutputStreamManager extends OutputStreamManager implements ByteBufferDestination {

    protected DefaultLogAppenderOutputStreamManager(OutputStream os, String streamName, Layout<?> layout, boolean writeHeader) {
        super(os, streamName, layout, writeHeader);
    }

    public static <T> DefaultLogAppenderOutputStreamManager getManager(final String name, final T data,
                                                                       final ManagerFactory<? extends OutputStreamManager, T> factory) {
        return (DefaultLogAppenderOutputStreamManager) AbstractManager.getManager(name, factory, data);
    }
}
