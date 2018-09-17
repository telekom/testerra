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
 * Created on 13.08.2012
 *
 * Copyright(c) 2012 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.exceptions;

/**
 * RuntimeException for fennec.
 * 
 * @author pele
 */
public class fennecRuntimeException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default exception, no message.
     */
    public fennecRuntimeException() {
        super();
    }

    /**
     * Exception with message.
     * 
     * @param message The message that should displayed.
     */
    public fennecRuntimeException(final String message) {
        super(message);
    }

    /**
     * Exception with message and cause.
     * 
     * @param message The message that should displayed.
     * @param cause The Throwable of the exception.
     */
    public fennecRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception with cause.
     * 
     * @param cause The Throwable of the exception.
     */
    public fennecRuntimeException(final Throwable cause) {
        super(cause);
    }
}
