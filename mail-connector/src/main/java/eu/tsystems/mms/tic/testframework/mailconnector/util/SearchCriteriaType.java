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
package eu.tsystems.mms.tic.testframework.mailconnector.util;

import javax.mail.search.FromTerm;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.RecipientTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.SubjectTerm;

/**
 * Enum for criteria to search mails for.
 *
 * @author pele
 * @deprecated Use implementations of {@link SearchTerm} instead
 */
@Deprecated
public enum SearchCriteriaType {
    /**
     * Criteria to search mails.
     * @deprecated Use {@link FromTerm} instead
     */
    SENDER,
    /**
     * @deprecated Use {@link RecipientTerm} instead
     */
    RECIPIENT,
    /**
     * @deprecated Use {@link SubjectTerm} instead
     */
    SUBJECT,
    /**
     * @deprecated Use {@link MessageIDTerm} instead
     */
    MESSAGEID,
    /**
     * Expect sentDate to be after criteria value
     * @deprecated Use {@link SentDateTerm} instead
     */
    AFTER_DATE
}
