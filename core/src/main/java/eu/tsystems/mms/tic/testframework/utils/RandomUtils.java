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
 * Created on 02.11.2012
 *
 * Copyright(c) 2012 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.utils;

import java.util.Random;

/**
 * Created with IntelliJ IDEA. User: pele Date: 02.11.12 Time: 13:26 To change this template use File | Settings | File
 * Templates.
 */
public final class RandomUtils {

    /** Private constructor, to hide the public. */
    private RandomUtils() {
    }

    /**
     * Generates a random string with length of 8 characters [a..z]
     * 
     * @return random string
     */
    public static String generateRandomString() {
        final Random random = new Random();
        final StringBuilder out = new StringBuilder();
        // chars of 97-122
        // diff=25
        final int length = 8;
        for (int i = 1; i <= length; i++) {
            out.append((char) (random.nextInt(25) + 97));
        }
        return out.toString();
    }

    /**
     * Generiert eine Random Number als String der Lange 4.
     * 
     * @return random number
     */
    public static String generateRandomNumber() {
        return generateRandomNumber(4);
    }

    /**
     * Generiert eine Random Number als String der Lange ?
     *
     * @param length länge der nummer
     * @return random number
     */
    public static String generateRandomNumber(int length) {
        final Random random = new Random();
        final StringBuilder out = new StringBuilder();

        for (int i = 1; i <= length; i++) {
            out.append(random.nextInt(10));
        }
        return out.toString();
    }

    /**
     * Generiert eine Random Number als int mit Max ?.
     *
     * @param max maximum der nummer
     * @return random number
     */
    public static int generateRandomInt(int max) {
        final Random random = new Random();
        return random.nextInt(max);
    }

    public static int generateRandomInt(int min, int max) {
        Random rnd = new Random();
        int rndInt = rnd.nextInt(max - min);
        return min + rndInt;
    }

    public static boolean magischeMiesmuschel(String frage) {
        return new Random().nextBoolean();
    }

}

