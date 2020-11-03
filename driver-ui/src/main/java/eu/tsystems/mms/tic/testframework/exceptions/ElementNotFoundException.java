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
 package eu.tsystems.mms.tic.testframework.exceptions;

/**
 * Runtime Exception, stating that a GUI Element is missing.
 */
public class ElementNotFoundException extends java.lang.RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     *
     * @param text Exception message.
     */
    public ElementNotFoundException(final String text) {
        super(text);
    }

    /**
     * Constructor with exception.
     *
     * @param text Exception message.
     * @param exception exception for message output
     */
    public ElementNotFoundException(final String text, final Exception exception) {
        super(text, exception);
    }


}
