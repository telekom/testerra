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
 package eu.tsystems.mms.tic.testframework.transfer;

import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @todo This is not {@link Throwable}, so rename it
 */
@Deprecated
public class ThrowablePackedResponse<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThrowablePackedResponse.class);
    private final T response;
    private final Throwable throwable;
    private final boolean successful;
    private TimeoutException timeoutExceptionOrNull;
    private boolean logError = true;

    public ThrowablePackedResponse(T response, Throwable throwable, boolean successful, TimeoutException timeoutExceptionOrNull) {
        this.response = response;
        this.throwable = throwable;
        this.successful = successful;
        this.timeoutExceptionOrNull = timeoutExceptionOrNull;
    }

    public T logThrowableAndReturnResponse() {
        if (hasThrowable()) {
            if (logError) {
                LOGGER.error("Error executing sequence", getTimeoutException());
            }
        }
        return getResponse();
    }

    public T finalizeTimer() {
        if (!isSuccessful() && hasTimeoutException()) {
            throw getTimeoutException();
        }
        return getResponse();
    }

    public T getResponse() {
        return response;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Deprecated
    public boolean hasThrowable() {
        return timeoutExceptionOrNull != null;
    }

    public boolean hasTimeoutException() {
        return timeoutExceptionOrNull != null;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public TimeoutException getTimeoutException() {
        return timeoutExceptionOrNull;
    }

    public void setLogError(boolean logError) {
        this.logError = logError;
    }

    @Override
    public String toString() {
        return "ThrowablePackedResponse{" +
                "response=" + response +
                ", throwable=" + throwable +
                '}';
    }
}
