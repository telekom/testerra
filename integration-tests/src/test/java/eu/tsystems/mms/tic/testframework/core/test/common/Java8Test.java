/*
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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.core.test.common;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Java8Test extends AbstractWebDriverTest {

    @FunctionalInterface
    private interface LambaInterface {

        boolean isLambda(String in1, String in2);

        default boolean isOk() {
            return false;
        }

    }

    private void checkLambda(List<String> stringList, LambaInterface lambaInterface) {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Java8Test.class);

    @Test
    public void testLambdasAndStreams() throws Exception {
        List<String> strings = new ArrayList<>();
        strings.add("this");
        strings.add("that");

        strings.forEach(element -> LOGGER.info("Found in list: " + element));

        Collections.sort(strings, (s1, s2) -> s1.length() - s2.length());
        checkLambda(strings, (s1, s2) -> true);

        List<String> newList = strings.stream().filter(s -> s.length() > 3).collect(Collectors.toList());
    }

}
