/*
 * Testerra
 *
 * (C) 2022, Marc Dietrich, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package io.testerra.report.test.pages.utils;

import eu.tsystems.mms.tic.testframework.report.Status;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestData {

    private String method;
    private String methodClass;

    private Status status;

    private Status[] states;
    private String failureAspect;
    private String[] failureAspects;

    public TestData(String method, String methodClass, Status status, String failureAspect) {
        this.method = method;
        this.methodClass = methodClass;
        this.status = status;
        this.failureAspect = failureAspect;
    }

    public TestData(String methode, String clazz, String... failureAspects){
        this.method = methode;
        this.methodClass = clazz;
        this.failureAspects = failureAspects.clone();
    }

    public TestData(String method, String methodClass, Status status) {
        this.method = method;
        this.methodClass = methodClass;
        this.status = status;
    }

    public TestData(String failureAspect, Status[] states) {
        this.failureAspect = failureAspect;
        this.states = states;
    }

    public TestData(String method, String failureAspect) {
        this.method = method;
        this.failureAspect = failureAspect;
    }

    public TestData(String method, Status status) {
        this.method = method;
        this.status = status;
    }

    public String getMethod() {
        return method;
    }

    public String getMethodClass() {
        return methodClass;
    }

    public Status getStatus1() {
        return status;
    }

    public List<Status> getStates() {
        return Arrays.stream(states).collect(Collectors.toList());
    }

    public String getFailureAspect() {
        return failureAspect;
    }

    @Override
    public String toString() {
        String status1Title;
        if (status == null) {
            status1Title = "null";
        } else {
            status1Title = status.title;
        }
        String statesTitle;
        if (states == null) {
            statesTitle = "null";
        } else {
            statesTitle = Arrays.toString(states);
        }

        return String.format("%s, %s, %s, %s, %s", method, methodClass, status1Title, statesTitle, failureAspect);
    }

    public String[] getFailureAspects() {
        return this.failureAspects;
    }
}
