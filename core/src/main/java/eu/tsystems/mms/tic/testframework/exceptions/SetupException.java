/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
 package eu.tsystems.mms.tic.testframework.exceptions;

public class SetupException extends RuntimeException {

    /**
     * Exception with message.
     *
     * @param message The message that should displayed.
     */
    public SetupException(final String message) {
        super(message);
    }

    /**
     * Exception with message and cause.
     *
     * @param message The message that should displayed.
     * @param cause The Throwable of the exception.
     */
    public SetupException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception with cause.
     *
     * @param cause The Throwable of the exception.
     */
    public SetupException(final Throwable cause) {
        super(cause);
    }

}
