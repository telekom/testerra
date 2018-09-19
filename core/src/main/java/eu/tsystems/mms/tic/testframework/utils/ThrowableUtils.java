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
package eu.tsystems.mms.tic.testframework.utils;

import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pele on 09.11.2015.
 */
public class ThrowableUtils {

    public static <T extends Throwable> Throwable getThrowableContainedIn(final Throwable throwable, final Class<T> throwableToSerachFor) {
        List<Throwable> throwables = new ArrayList<Throwable>();
        throwables.add(throwable);
        Throwable cause = throwable.getCause();
        while (cause != null) {
            throwables.add(cause);
            cause = cause.getCause();
        }

        for (Throwable t : throwables) {
            if (t.getClass().equals(throwableToSerachFor)) {
                return t;
            }
        }
        return null;
    }

    public static <T extends Throwable> void expectThrowable(Class<T> expectedThrowable, Runnable runnable) {
        try {
            runnable.run();
        }
        catch (Throwable throwable) {
            if (throwable.getClass().isAssignableFrom(expectedThrowable)) {
                return;
            }
            Assert.assertEquals(throwable.getClass(), expectedThrowable, "Throwable");
        }
        Assert.assertTrue(false, "Expected throwable is of type " + expectedThrowable.getSimpleName());
    }

}
