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
 * Date: 23.05.14
 */
public class ValuedPoint2D extends Point2D {
    public double value;

    public ValuedPoint2D(int x, int y, double value) {
        super(x, y);
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
