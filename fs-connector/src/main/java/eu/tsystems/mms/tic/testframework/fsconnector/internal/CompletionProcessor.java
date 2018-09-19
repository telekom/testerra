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
import org.apache.camel.Processor;
import org.apache.camel.spi.Synchronization;

/**
 * Camel processor which ends an exchange after one has been processed.
 * 
 * @author pele
 */
public class CompletionProcessor implements Processor {

    /**
     * Process implementation which sets the steps of this processor on complete if one exchange has been done.
     * 
     * @param exchange
     *            Exchange to process.
     */
    @Override
    public void process(final Exchange exchange) {
        this.pProcess(exchange);
    }
    
    /**
     * Process implementation which sets the steps of this processor on complete if one exchange has been done.
     * 
     * @param exchange
     *            Exchange to process.
     */
    private void pProcess(final Exchange exchange) {
        final Synchronization sync = new Synchronization() {
            @Override
            public void onComplete(final Exchange exchange) {
                ((FennecFSCamelContext) exchange.getContext()).setComplete(true);
            }

            @Override
            public void onFailure(final Exchange exchange) {
                ((FennecFSCamelContext) exchange.getContext()).setException(exchange.getException());
            }
        };
        exchange.addOnCompletion(sync);
    }
}
