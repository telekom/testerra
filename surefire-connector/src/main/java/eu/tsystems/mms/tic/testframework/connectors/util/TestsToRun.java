/*
 * Created on 11.04.2011
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.connectors.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper class for holding all the test methods that should be executed while testing.
 *
 * @author mibu, sepr
 */
public class TestsToRun {

    /**
     * A list containing all the methods to execute.
     */
    private final Map<String, List<Method>> locatedMethods;

    /**
     * Public constructor. Creates a new <code>TestsToRun</code> object.
     */
    public TestsToRun() {
        locatedMethods = new HashMap<String, List<Method>>();
    }

    /**
     * Returns the number of tests to run.
     *
     * @return The number of tests to run.
     */
    public int size() {
        return locatedMethods.size();
    }

    /**
     * .
     *
     * @return A map containing the located methods.
     */
    public Map<String, List<Method>> getMethodMap() {
        return locatedMethods;
    }

    /**
     * Gets all located methods.
     *
     * @return A list containing all located methods.
     */
    public List<Method> getAllMethods() {
        final List<Method> rList = new ArrayList<Method>();
        for (final List<Method> l : locatedMethods.values()) {
            rList.addAll(l);
        }
        return rList;
    }

    /**
     * Adds new entry to the map.
     *
     * @param key   The testset string.
     * @param value A list containing the methods.
     */
    public void addEntryToMap(final String key, final List<Method> value) {
        locatedMethods.put(key, value);
    }
}
