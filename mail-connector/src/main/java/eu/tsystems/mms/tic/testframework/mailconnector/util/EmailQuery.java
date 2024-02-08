/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.mailconnector.util;

import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;

import java.util.List;

import jakarta.mail.search.AndTerm;
import jakarta.mail.search.SearchTerm;

/**
 * EMail query object
 */
public class EmailQuery implements PropertyManagerProvider {
    /**
     * Key for pollingfrequency Property.
     */
    private static final String POLLING_TIMER_SECONDS_PROPERTY = "POLLING_TIMER_SECONDS";
    /**
     * Key for max_read_tries Property.
     */
    private static final String MAX_READ_TRIES_PROPERTY = "MAX_READ_TRIES";

    private int retryCount = Long.valueOf(PROPERTY_MANAGER.getLongProperty(MAX_READ_TRIES_PROPERTY, 20L)).intValue();
    private long pauseMs = PROPERTY_MANAGER.getLongProperty(POLLING_TIMER_SECONDS_PROPERTY, 10L) * 1000;

    private SearchTerm searchTerm;
    private String folderName;

    public int getRetryCount() {
        return retryCount;
    }

    public EmailQuery setRetryCount(int maxReadTries) {
        this.retryCount = maxReadTries;
        return this;
    }

    public long getPauseMs() {
        return pauseMs;
    }

    public EmailQuery setPauseMs(long millis) {
        this.pauseMs = millis;
        return this;
    }

    public SearchTerm getSearchTerm() {
        return searchTerm;
    }

    public EmailQuery setSearchTerm(SearchTerm searchTerm) {
        this.searchTerm = searchTerm;
        return this;
    }

    public EmailQuery withAllOfSearchTerms(List<SearchTerm> searchTerms) {
        return withAllOfSearchTerms(searchTerms.toArray(new SearchTerm[]{}));
    }

    public EmailQuery withAllOfSearchTerms(SearchTerm... searchTerms) {
        this.searchTerm = new AndTerm(searchTerms);
        return this;
    }

    public String getFolderName() {
        return folderName;
    }

    public EmailQuery setFolderName(String folderName) {
        this.folderName = folderName;
        return this;
    }
}
