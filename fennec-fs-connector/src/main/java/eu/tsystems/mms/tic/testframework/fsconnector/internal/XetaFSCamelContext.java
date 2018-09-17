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

import javax.naming.Context;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.Registry;

/**
 * Extension of {@link DefaultCamelContext} allowing to stop a camel transfer on completion or exception.
 * 
 * @author pele
 */
public class fennecFSCamelContext extends DefaultCamelContext {

    /** Boolean indicating this steps has been completed. */
    private Boolean contextComplete = false;
    /** Exception thrown by this steps. */
    private Exception exception = null;

    /**
     * Inherited default constructor.
     * 
     * @param jndiContext
     *            jndiContext
     */
    public fennecFSCamelContext(final Context jndiContext) {
        super(jndiContext);
    }

    /**
     * Inherited default constructor.
     * 
     * @param registry
     *            Camel registry
     */
    public fennecFSCamelContext(final Registry registry) {
        super(registry);
    }

    /**
     * sets a boolean value if file transfer is complete
     *
     * @param complete 
     *          The boolean value if file transfer is complete.
     */
    public void setComplete(final Boolean complete) {
        contextComplete = complete;
    }

    /**
     * gets the exception
     *
     * @return the thrown Exception.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * sets the exception
     *
     * @param exception
     *          The exception to set.
     */
    public void setException(final Exception exception) {
        this.exception = exception;
    }

    /**
     * checks if the filetransfer is complete
     *
     * @return the Status, if filetransfer is complete.
     */
    public Boolean isComplete() {
        return contextComplete;
    }
}
