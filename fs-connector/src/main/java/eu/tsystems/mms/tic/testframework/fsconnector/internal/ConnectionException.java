/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
/* 
 * Created on 01.08.2012
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.fsconnector.internal;

/**
 * Exception indicating an error while trying to connect to an external location.
 * 
 * @author pele
 */
public class ConnectionException extends RuntimeException {

    /** Serial Id */
    private static final long serialVersionUID = -2092271040035099324L;

    /**
     * Constructor instantiating the Exception with a message.
     * 
     * @param message
     *            Message of exception.
     */
    public ConnectionException(final String message) {
        super(message);
    }

    /**
     * Constructor instantiating the Exception with a message and a throwable.
     * 
     * @param message
     *            Message of exception.
     * @param cause
     *            Throwable.
     */
    public ConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor instantiating the Exception with a cause.
     * 
     * @param cause
     *            Message of Throwable.
     */
    public ConnectionException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor instantiating the exception by a number of FSLocations.
     * 
     * @param locations
     *            FSLocations that could not been connected to.
     * @param exception
     *            The exception that was thrown.
     */
    public ConnectionException(final Exception exception, final AbstractFileSystemLocation<?>... locations) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Fehler bei Aufbau der Verbindung:");
        for (final AbstractFileSystemLocation<?> location : locations) {
            builder.append("\n " + location.getCamelUrl());
        }
        builder.append("\n");
        new ConnectionException(builder.toString(), exception);
    }

}
