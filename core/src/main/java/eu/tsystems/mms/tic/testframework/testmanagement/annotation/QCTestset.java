/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.testmanagement.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Annotation that indicates to which QualityCenter TestSet test results should be synchronized.
 *
 * @author mrgi, pele
 * @deprecated Use annotation from Testerra hpqc-connector
 */
@Deprecated
@Target({METHOD, TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface QCTestset {

    /**
     * The path to QC testset. To make the path dynamic you can use systemproperties:
     * //Root//Release_{systemproperty1}//MyPath
     */
    String value() default "";
}
