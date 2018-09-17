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
package eu.tsystems.mms.tic.testframework.test.constants;

/**
 * User: rnhb
 * Date: 26.09.13
 * Time: 10:02
 */
public final class AssertionMessages {

    private AssertionMessages() {
    }

    /** To use with Assert.assertEquals, to show the difference in the messages.
     *@return unexpected exception message
     */
    public static String unexpectedExcpetionMessage() {
        return "The message of the thrown exception is wrong.";
    }

}
