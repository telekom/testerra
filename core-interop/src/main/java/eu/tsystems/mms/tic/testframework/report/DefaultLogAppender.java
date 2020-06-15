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
package eu.tsystems.mms.tic.testframework.report;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.CloseShieldOutputStream;
import org.apache.logging.log4j.core.util.NullOutputStream;

import java.io.OutputStream;
import java.io.Serializable;

/**
 * BaseLoggingActor allows to use log4j logs for HTML Reports.
 */
@Plugin(name = "DefaultLog", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class DefaultLogAppender extends AbstractOutputStreamAppender<DefaultLogAppenderOutputStreamManager> implements TesterraLogger {

    private LogFormatter formatter = new DefaultLogFormatter();

    protected DefaultLogAppender(String name, Layout<? extends Serializable> layout, Filter filter, boolean ignoreExceptions, boolean immediateFlush, Property[] properties, eu.tsystems.mms.tic.testframework.report.DefaultLogAppenderOutputStreamManager manager) {
        super(name, layout, filter, ignoreExceptions, immediateFlush, properties, manager);
    }

    @Override
    public DefaultLogAppender setFormatter(LogFormatter formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public LogFormatter getFormatter() {
        return this.formatter;
    }

    /**
     * Appends a LoggingEvent to the HTML Report using Reporter.log().
     *
     * @param event The event to be logged.
     */
    @Override
    public void append(final LogEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.formatter.format(event));

        if (event.getThrown() != null) {
            for (StackTraceElement ste : event.getThrown().getStackTrace()) {
                sb.append("\n");
                sb.append(ste);
            }
        }

        // append for console
        if (event.getLevel().isMoreSpecificThan(Level.ERROR)) {
            System.err.println(sb.toString());
        } else {
            System.out.println(sb.toString());
        }
    }

    public static class Builder<B extends DefaultLogAppender.Builder<B>> extends AbstractOutputStreamAppender.Builder<B>
            implements org.apache.logging.log4j.core.util.Builder<DefaultLogAppender> {

        private boolean follow = false;

        private final boolean ignoreExceptions = true;

        private OutputStream target;

        @Override
        public DefaultLogAppender build() {
            final Layout<? extends Serializable> layout = getLayout();
            final Layout<? extends Serializable> actualLayout = layout == null ? PatternLayout.createDefaultLayout()
                    : layout;
            return new DefaultLogAppender(getName(), actualLayout, getFilter(), ignoreExceptions, true, getPropertyArray(), getManager(target, follow, actualLayout));
        }

        public B setFollow(final boolean shouldFollow) {
            this.follow = shouldFollow;
            return asBuilder();
        }

        public B setTarget(final OutputStream aTarget) {
            this.target = aTarget;
            return asBuilder();
        }
    }

    private static class FactoryData {

        private final Layout<? extends Serializable> layout;
        private final String name;
        private final OutputStream os;

        public FactoryData(final OutputStream os, final String type, final Layout<? extends Serializable> layout) {
            this.os = os;
            this.name = type;
            this.layout = layout;
        }
    }

    private static class DefaultLogAppenderOutputStreamManagerFactory implements ManagerFactory<DefaultLogAppenderOutputStreamManager, DefaultLogAppender.FactoryData> {

        /**
         * Creates an OutputStreamManager.
         *
         * @param name The name of the entity to manage.
         * @param data The data required to create the entity.
         * @return The OutputStreamManager
         */
        @Override
        public DefaultLogAppenderOutputStreamManager createManager(final String name, final FactoryData data) {
            return new DefaultLogAppenderOutputStreamManager(data.os, data.name, data.layout, true);
        }
    }

    private static DefaultLogAppenderOutputStreamManagerFactory factory = new DefaultLogAppenderOutputStreamManagerFactory();

    @PluginFactory
    public static DefaultLogAppender createAppender(Layout<? extends Serializable> layout, final Filter filter,
                                                    final OutputStream target, final String name, final boolean follow, final boolean ignore) {
        if (name == null) {
            LOGGER.error("No name provided for OutputStreamAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new DefaultLogAppender(name, layout, filter, ignore, true, null, getManager(target, follow, layout));
    }

    private static DefaultLogAppenderOutputStreamManager getManager(final OutputStream target, final boolean follow,
                                                                    final Layout<? extends Serializable> layout) {
        final OutputStream os = target == null ? NullOutputStream.getInstance() : new CloseShieldOutputStream(target);
        final OutputStream targetRef = target == null ? os : target;
        final String managerName = targetRef.getClass().getName() + "@" + Integer.toHexString(targetRef.hashCode())
                + '.' + follow;
        return DefaultLogAppenderOutputStreamManager.getManager(managerName, new DefaultLogAppender.FactoryData(os, managerName, layout), factory);
    }
}
