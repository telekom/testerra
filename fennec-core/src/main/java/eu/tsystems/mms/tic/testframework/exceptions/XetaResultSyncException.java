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
 * Created on 31.01.2014
 *
 * Copyright(c) 2011 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.exceptions;

import java.io.IOException;

/**
 * Own Exception to bundle different errors thrown during test result synchronization (to qc/testlink).
 * 
 * @author sepr
 * 
 */
public class fennecResultSyncException extends IOException {

    /**
     * generated serialVersioUID
     */
    private static final long serialVersionUID = 4662478921637177888L;

    /**
     * default Constructor.
     * 
     * @param cause Exception causing the sync error.
     */
    public fennecResultSyncException(Throwable cause) {
        super(cause);
    }

    /**
     * default error message
     *
     * @param string error message
     */
    public fennecResultSyncException(String string) {
        super(string);
    }
}
