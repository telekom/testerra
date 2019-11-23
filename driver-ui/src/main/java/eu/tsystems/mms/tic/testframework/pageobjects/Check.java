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
 *     Peter Lehmann
 *     pele
 */
/*
 * Created on 04.01.2013
 *
 * Copyright(c) 2012 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.IGuiElement;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created with IntelliJ IDEA.
 * User: pele
 * Date: 14.08.12
 * Time: 09:12
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Check {

    /**
     * simple boolean function
     */
    boolean nonFunctional() default false;

    /**
     * Defaults to {@link IGuiElement.Properties#ELEMENT_TIMEOUT_SECONDS}
     */
    int timeout() default -1;

    /**
     * Whether to check for isDisplayed.
     *
     * @return
     */
    CheckRule checkRule() default CheckRule.DEFAULT;
//    CheckRule checkRule() default CheckRule.IS_PRESENT;

    String prioritizedErrorMessage() default "";

}
