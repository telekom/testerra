/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems MMS GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.logging;

import eu.tsystems.mms.tic.testframework.report.model.context.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.slf4j.Logger;
import org.slf4j.Marker;

public class PromptLogger implements Logger, Loggable {
    private static final Map<String, Logger> loggers = new ConcurrentHashMap<>();
    private static MessageFactory messageFactory2;
    private final String name;

    PromptLogger(String name) {
        this.name = name;
    }

    static {
        try {
            Constructor<? extends MessageFactory> constructor = AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS.getConstructor();
            messageFactory2 = constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Logger getLogger(Class clazz) {
        String name = clazz.getCanonicalName();
        Logger logger;
        if (!loggers.containsKey(name)) {
            logger = new PromptLogger(name);
            loggers.put(name, logger);
        } else {
            logger = loggers.get(name);
        }
        return logger;
    }

    private void log(Message message, Level logLevel) {
        LogMessage logMessage = new LogMessage(this.name, message, logLevel);
        Optional<MethodContext> methodContextForThread = ExecutionContextController.getMethodContextForThread();
        if (methodContextForThread.isPresent()) {
            methodContextForThread.get().addLogMessage(logMessage);
        } else {
            ExecutionContextController.getCurrentExecutionContext().addLogMessage(logMessage);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void trace(String s) {
        this.log(messageFactory2.newMessage(s), Level.TRACE);
    }

    @Override
    public void trace(String s, Object o) {
        this.log(messageFactory2.newMessage(s, o), Level.TRACE);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        this.log(messageFactory2.newMessage(s, o, o1), Level.TRACE);
    }

    @Override
    public void trace(String s, Object... objects) {
        this.log(messageFactory2.newMessage(s, objects), Level.TRACE);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        this.log(messageFactory2.newMessage(s, throwable), Level.TRACE);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    public void trace(Marker marker, String s) {
        this.trace(s);
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        this.trace(s, o);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        this.trace(s,o,o1);
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        this.trace(s, objects);
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        this.trace(s, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(String s) {
        this.log(messageFactory2.newMessage(s), Level.DEBUG);
    }

    @Override
    public void debug(String s, Object o) {
        this.log(messageFactory2.newMessage(s, o), Level.DEBUG);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        this.log(messageFactory2.newMessage(s, o, o1), Level.DEBUG);
    }

    @Override
    public void debug(String s, Object... objects) {
        this.log(messageFactory2.newMessage(s, objects), Level.DEBUG);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        this.log(messageFactory2.newMessage(s, throwable), Level.DEBUG);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return false;
    }

    @Override
    public void debug(Marker marker, String s) {
        this.debug(s);
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        this.debug(s, o);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        this.debug(s, o, o1);
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        this.debug(s, objects);
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        this.debug(s, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String s) {

    }

    @Override
    public void info(String s, Object o) {

    }

    @Override
    public void info(String s, Object o, Object o1) {

    }

    @Override
    public void info(String s, Object... objects) {

    }

    @Override
    public void info(String s, Throwable throwable) {

    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return false;
    }

    @Override
    public void info(Marker marker, String s) {

    }

    @Override
    public void info(Marker marker, String s, Object o) {

    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void info(Marker marker, String s, Object... objects) {

    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(String s) {

    }

    @Override
    public void warn(String s, Object o) {

    }

    @Override
    public void warn(String s, Object... objects) {

    }

    @Override
    public void warn(String s, Object o, Object o1) {

    }

    @Override
    public void warn(String s, Throwable throwable) {

    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return false;
    }

    @Override
    public void warn(Marker marker, String s) {

    }

    @Override
    public void warn(Marker marker, String s, Object o) {

    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {

    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(String s) {

    }

    @Override
    public void error(String s, Object o) {

    }

    @Override
    public void error(String s, Object o, Object o1) {

    }

    @Override
    public void error(String s, Object... objects) {

    }

    @Override
    public void error(String s, Throwable throwable) {

    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return false;
    }

    @Override
    public void error(Marker marker, String s) {

    }

    @Override
    public void error(Marker marker, String s, Object o) {

    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void error(Marker marker, String s, Object... objects) {

    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {

    }
}
