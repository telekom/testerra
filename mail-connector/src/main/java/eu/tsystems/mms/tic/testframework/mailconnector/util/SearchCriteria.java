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
package eu.tsystems.mms.tic.testframework.mailconnector.util;


import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;

/**
 * Created by pele on 15.10.2015.
 */
public class SearchCriteria {

    private final SearchCriteriaType searchCriteriaType;
    private final Object value;

    /**
     * Create search criteria
     *
     * @param searchCriteriaType {@link SearchCriteriaType}
     * @param value java.util.Date for {@link SearchCriteriaType#AFTER_DATE}, String for others.
     */
    public SearchCriteria(SearchCriteriaType searchCriteriaType, Object value) {
        this.searchCriteriaType = searchCriteriaType;
        this.value = value;
    }

    public SearchCriteriaType getSearchCriteriaType() {
        return searchCriteriaType;
    }

    public Object getValue() {
        return value;
    }

    /**
     * @return value as String or Runtime exception if it's not a string.
     */
    public String getStringValue() {
        if(!(value instanceof String)) {
            throw new TesterraRuntimeException("Expect String value for SearchCriteria of type " + searchCriteriaType.name());
        }
        return (String) value;
    }
}
