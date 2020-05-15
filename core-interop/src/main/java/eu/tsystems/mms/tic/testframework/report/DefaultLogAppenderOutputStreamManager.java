package eu.tsystems.mms.tic.testframework.report;

import java.io.OutputStream;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.*;
import org.apache.logging.log4j.core.layout.ByteBufferDestination;

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
