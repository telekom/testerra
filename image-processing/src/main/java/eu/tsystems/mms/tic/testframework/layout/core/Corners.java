/*
 * Created on 03.13.2014
 *
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.layout.core;

/**
 * User: rnhb
 * Date: 16.05.14
 */
public enum Corners {
    UPPER_LEFT(0),
    UPPER_RIGHT(1),
    LOWER_RIGHT(2),
    LOWER_LEFT(3);

    public final int i;

    private Corners(int index) {
        this.i = index;
    }

    public static String getName(int index) {
        for (Corners p : Corners.values()) {
            if (p.i == index) {
                return p.name().toLowerCase();
            }
        }
        return "";
    }
}
