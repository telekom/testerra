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
 * Created on 10.02.14
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.bmp;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import org.xbill.DNS.*;

import java.net.UnknownHostException;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public final class DNSLookup {

    private DNSLookup() {
    }

    public static Record[] lookup(String name) throws TextParseException, UnknownHostException {
        return lookup(name, null);
    }

    public static Record[] lookup(String name, String dnsServer) throws TextParseException, UnknownHostException {
//        Lookup lookup = new Lookup(name, Type.TXT, DClass.CH);
        Lookup lookup = new Lookup(name, Type.ANY);

        if (dnsServer != null) {
            lookup.setResolver(new SimpleResolver(dnsServer));
        }

        lookup.run();
        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            Record[] records = lookup.getAnswers();
            return records;
        }
        else {
            String msg = "DNS Lookup was not successful. Lookup result code was: ";
            switch (lookup.getResult()) {
                case Lookup.HOST_NOT_FOUND:
                    msg += "HOST_NOT_FOUND";
                    break;
                case Lookup.TRY_AGAIN:
                    msg += "TRY_AGAIN";
                    break;
                case Lookup.TYPE_NOT_FOUND:
                    msg += "TYPE_NOT_FOUND";
                    break;
                case Lookup.UNRECOVERABLE:
                    msg += "UNRECOVERABLE";
                    break;
                default:
                    msg += lookup.getErrorString();
            }
            throw new TesterraSystemException(msg);
        }
        
    }
}
