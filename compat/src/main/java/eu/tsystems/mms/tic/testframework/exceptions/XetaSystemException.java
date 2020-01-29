/*
 * Created on 06.08.2012
 *
 * Copyright(c) 2012 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.exceptions;

/**
 * @deprecated Use {@link TesterraSystemException} instead
 */
@Deprecated
public class XetaSystemException extends TesterraSystemException {

    public XetaSystemException(final String message) {
        super(message);
    }

    public XetaSystemException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public XetaSystemException(final Throwable cause) {
        super(cause);
    }
}
