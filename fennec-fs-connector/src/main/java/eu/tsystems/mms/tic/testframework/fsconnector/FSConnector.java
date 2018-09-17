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
package eu.tsystems.mms.tic.testframework.fsconnector;

import eu.tsystems.mms.tic.testframework.exceptions.fennecRuntimeException;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Destination;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Protocol;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Source;
import eu.tsystems.mms.tic.testframework.fsconnector.internal.*;
import org.apache.camel.impl.SimpleRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeoutException;

/**
 * The FileSystem connector provides a simple interface to copy files between different locations.
 * 
 * @author pele.
 */
public final class FSConnector {

    private static int TIMEOUT_SECONDS = 20;

    /**
     * Hide default constructor of utility class.
     */
    private FSConnector() {
    }

    /**
     * Copy a file from one Location to another.
     * 
     * @param source Source location of file.
     * @param destination Destinated location of file.
     * @throws Exception .
     */
    public static void copy(final Source source, final Destination destination) throws Exception {
        pCopy(source, destination);
    }
    
    /**
     * Copy a file from one Location to another.
     * 
     * @param source Source location of file.
     * @param destination Destinated location of file.
     */
    private static void pCopy(final Source source, final Destination destination) throws Exception {
        // WebDav --> File
        if (source.getProtocol() == Protocol.WEBDAV) {
            if (destination.getProtocol() != Protocol.FILE) {
                throw new fennecRuntimeException(
                        "WebDav can only be used together with the File protocol at the moment. " +
                                "Set a file as Destination.");
            }
            WebDavConnection.download(source, destination);
            // File --> WebDav
        } else if (destination.getProtocol() == Protocol.WEBDAV) {
            if (source.getProtocol() != Protocol.FILE) {
                throw new fennecRuntimeException(
                        "WebDav can only be used together with the File protocol at the moment. Set a file as Source.");
            }
            WebDavConnection.upload(source, destination);
            // Others
        } else {
            final SimpleRegistry reg = new SimpleRegistry();
            reg.put("fennecFSSinglePoller", new EndpointSinglePollStrategy());

            final fennecFSCamelContext context = new fennecFSCamelContext(reg);
            try {
                context.addRoutes(new FSRouteBuilder(source, destination));
            } catch (final Exception e) {
                throw new ConnectionException(e, source, destination);
            }
            execute(context);
        }
    }

    /**
     * Set thread to sleep for a defined amount of time.
     * 
     * @param mseconds Time to sleep in ms.
     */
    private static void sleep(final int mseconds) {
        try {
            Thread.sleep(mseconds);
        } catch (final InterruptedException e) {
            LoggerFactory.getLogger(FSConnector.class).warn(e.getMessage());
        }
    }

    /**
     * Start a file transfer.
     * 
     * @param context Camel steps.
     */
    private static void start(final fennecFSCamelContext context) {
        try {
            context.start();
        } catch (final Exception e) {
            throw new ConnectionException("Error executing camel steps", e);
        }
    }

    /**
     * End a file transfer.
     * 
     * @param context Camel steps.
     */
    private static void stop(final fennecFSCamelContext context) {
        if (!context.isStopped()) {
            try {
                context.stop();
            } catch (final Exception e) {
                throw new ConnectionException(e);
            }
        }
    }

    /**
     * If an exception occurs at transfering the file, this method will handle it.
     * 
     * @param exception Exception to handle.
     */
    private static void exitWithError(final Exception exception) throws Exception {
        sleep(1000);
        final Logger logger = LoggerFactory.getLogger(FSConnector.class);
        logger.error(exception.getMessage());
        throw new ConnectionException("Error executing camel steps", exception);
    }

    /**
     * Method wrapping the execution of a file transfer encapsulated in a CamelContext.
     * 
     * @param context Camel steps.
     */
    private static void execute(final fennecFSCamelContext context) throws Exception {
        start(context);

        // Warten auf Completion
        final Logger logger = LoggerFactory.getLogger(FSConnector.class);
        logger.info("Wait for transfer...");
        int timerInSeconds = getTimeoutSeconds();
        while (!context.isComplete()) {
            final Exception exception = context.getException();
            if (exception != null) {
                stop(context);
                exitWithError(exception);
            }

            sleep(1000);
            timerInSeconds--;

            // TODO: Better with Shutdown strategy!! - pele 04.11.2015
            if (timerInSeconds == 0) {
                throw new TimeoutException("Timed out " + getTimeoutSeconds() + " s.\nContext status is: " + context.getStatus());
            }
        }
        logger.info("Transfer ok.");

        stop(context);
    }

    public static int getTimeoutSeconds() {
        return TIMEOUT_SECONDS;
    }

    public static void setTimeoutSeconds(int timeoutSeconds) {
        TIMEOUT_SECONDS = timeoutSeconds;
    }
}
