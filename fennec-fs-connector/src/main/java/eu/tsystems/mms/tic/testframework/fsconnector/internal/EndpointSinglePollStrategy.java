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

import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.spi.PollingConsumerPollStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;

/**
 * Class implementing the camel {@link PollingConsumerPollStrategy}.
 * 
 * @author pele.
 */
public class EndpointSinglePollStrategy implements PollingConsumerPollStrategy {

    /** Slf4j logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointSinglePollStrategy.class);
    /** Number of trys for the same exchange. */
    private int tryCount = 1;

    @Override
    public boolean begin(final Consumer consumer, final Endpoint endpoint) {
        return this.pBegin(consumer, endpoint);
    }
    
    private boolean pBegin(final Consumer consumer, final Endpoint endpoint) {
        final String maxTryValue = endpoint.getEndpointConfiguration().getParameter("maxTrys");
        int maxTrys;
        if (maxTryValue != null && isNumber(maxTryValue)) {
            maxTrys = Integer.valueOf(maxTryValue);
        } else {
            maxTrys = 2;
        }
        LOGGER.info("Begin poll");
        if (tryCount > maxTrys) {
            tryCount = 1;
            throw new FennecRuntimeException("Tried getting the file " + maxTrys
                    + " times without success. Cancelled transfer.");
        }
        tryCount++;
        return true;
    }

    /**
     * Test if a String contains a number and can be converted into an Integer.
     * 
     * @param value
     *            String to test being a number.
     * @return true if String is a number, false otherwise.
     */
    private boolean isNumber(final String value) {
        try {
            Integer.valueOf(value);
        } catch (final Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void commit(final Consumer consumer, final Endpoint endpoint, final int number) {
        // nothing
    }

    @Override
    public boolean rollback(final Consumer consumer, final Endpoint endpoint, final int retryCounter, 
            final Exception exception) throws FennecSystemException {
        return this.pRollback(consumer, endpoint, retryCounter, exception);
    }
    
    private boolean pRollback(final Consumer consumer, final Endpoint endpoint, final int retryCounter, 
            final Exception exception) throws FennecSystemException {
        try {
            LOGGER.info("Rollback");
            tryCount = 1;
            final FennecFSCamelContext context = (FennecFSCamelContext) endpoint.getCamelContext();
            if (exception != null) {
                context.setException(exception);
            }
            context.stop();
        } catch (final Exception e) {
            //if exception is thrown
            throw new FennecSystemException(e);
        }
        if (exception != null) {
            //if parameter 'exception' is not null
            throw new FennecSystemException(exception);
        } else {
            return true;
        }
    }
}
