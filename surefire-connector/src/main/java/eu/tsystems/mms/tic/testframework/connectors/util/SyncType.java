/*
 * Created on 24.07.2012
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.connectors.util;

/**
 * Enum contains names for syncType Numbers.
 *
 * @author mrgi
 */
public enum SyncType {

    /**
     * Names of the syncTypes.
     */
    ANNOTATION(3);

    /**
     * The number of syncType
     */
    private int number;

    /**
     * Constructor to initialize the number.
     *
     * @param number The number of syncType
     */
    SyncType(final int number) {
        this.number = number;
    }

    /**
     * gets the syncMethod of syncType
     *
     * @return annotation of sync type
     */
    public static SyncType getSyncMethod() {

        return SyncType.ANNOTATION;
    }

    /**
     * Get the number of the syncType.
     *
     * @return .
     */
    public int getNumber() {
        return number;
    }
}
