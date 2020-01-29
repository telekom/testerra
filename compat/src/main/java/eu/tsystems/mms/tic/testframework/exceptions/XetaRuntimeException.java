/*
 * Created on 13.08.2012
 *
 * Copyright(c) 2012 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.exceptions;

/**
 * @deprecated Use {@link TesterraRuntimeException} instead
 */
@Deprecated
public class XetaRuntimeException extends TesterraRuntimeException {
    public XetaRuntimeException(final String message) {
        super(message);
    }
    public XetaRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public XetaRuntimeException(final Throwable cause) {
        super(cause);
    }
}
