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

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import eu.tsystems.mms.tic.testframework.exceptions.fennecRuntimeException;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Destination;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Protocol;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Source;

/**
 * fennec RouteBuilder extending the camel default one. 
 * Source and destination are stored by the builder, so that configurations can
 * be done according to their properties.
 * 
 * @author pele
 */
public class FSRouteBuilder extends RouteBuilder {

    /** Source of camel route. */
    private final Source source;
    /** Destination of camel route. */
    private final Destination destination;

    /**
     * Constructor setting source and destination of the builder.
     * 
     * @param source
     *            Source for camel routes.
     * @param destination
     *            Destination for camel routes.
     */
    public FSRouteBuilder(final Source source, final Destination destination) {
        super();
        this.source = source;
        this.destination = destination;
    }

    /**
     * Overriden configure method, that builds the exchange statements using the camel DSL.
     */
    @Override
    public void configure() {
        this.pConfigure();
    }
    
    /**
     * Configure method, that builds the exchange statements using the camel DSL.
     */
    private void pConfigure() {
        if (destination.getProtocol() == Protocol.HTTP) {
            throw new fennecRuntimeException("HTTP not allowed as destination");
        }
        if (source.getProtocol() == Protocol.HTTP) {
            from("timer://foo?fixedRate=true&delay=200&period=5000&repeatCount=1")
            .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                    .process(new CompletionProcessor()).to(source.getCamelUrl())
                    .setHeader(Exchange.FILE_NAME, constant(destination.getFilename())).to("mock:results")
                    .process(new CompletionProcessor()).to(destination.getCamelUrl());
        } else {
            from(source.getCamelUrl()).filter(header(Exchange.FILE_NAME).isEqualTo(source.getFilename()))
                    .process(new CompletionProcessor()).to(destination.getCamelUrl());
        }
    }
}
