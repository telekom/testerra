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
package eu.tsystems.mms.tic.testframework.internal;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister64;

import java.util.Date;

/**
 * Utility class for ID generation.
 *
 * <p>
 * Author:          Francis R.
 * Date Created:    23.11.2017
 */
public class IDUtils {

    private IDUtils(){

    }

    private static final MersenneTwister64 ID_GENERATOR;
    private static final Uniform U;

    static {
        // Constructs and returns a random number generator seeded with the given date.
        ID_GENERATOR  = new MersenneTwister64(new Date());
        U = new Uniform(ID_GENERATOR);
    }

    /**
     * Get a random Long value (min: 0).
     *
     * @return
     *      A random Long value which can't be lower then 0.
     */
    public static long getRandomLongID() { return U.nextLongFromTo(0, Long.MAX_VALUE); }

}
