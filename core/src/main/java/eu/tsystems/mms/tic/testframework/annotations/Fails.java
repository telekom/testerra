/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package eu.tsystems.mms.tic.testframework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class Fails. Marks a test method as a failing test method with the ability to hold some information.
 *
 * @author pele
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Fails {

    /**
     * tickedID as integer
     * @deprecated Use {@link #ticketString()} instead
     */
    int ticketId() default 0;

    /**
     * ticket as string
     */
    String ticketString() default "";

    /**
     * string description
     */
    String description() default "";

    boolean intoReport() default false;

    /**
     * @deprecated Use {@link #validatorClass()} instead
     */
    String[] validFor() default {};

    Class<?> validatorClass() default Object.class;
    String validator() default "";
}
