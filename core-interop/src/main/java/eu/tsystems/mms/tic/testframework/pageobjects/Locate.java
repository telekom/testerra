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
 package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.filter.WebElementFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;

/**
 * Advanced selector for elements as replacement for By
 *
 * @author Mike Reiche
 */
public class Locate {

    private List<WebElementFilter> filters;
    private boolean unique = false;
    private By by;
    private String preparedFormat;

    Locate(By by) {
        this.by = by;
    }

    Locate(String preparedFormat) {
        this.preparedFormat = preparedFormat;
    }

    public By getBy() {
        return this.by;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public Locate unique() {
        this.unique = true;
        return this;
    }

    public Locate displayed() {
        return filter(WebElementFilter.DISPLAYED.is(true));
    }
    public Locate notDisplayed() {
        return filter(WebElementFilter.DISPLAYED.is(false));
    }

    public Locate filter(WebElementFilter... filters) {
        if (filters != null) {
            Collections.addAll(getFilters(), filters);
        }
        return this;
    }

    public List<WebElementFilter> getFilters() {
        if (this.filters == null) {
            this.filters = new ArrayList<>();
        }
        return this.filters;
    }

    @Override
    public String toString() {
        String toString = (unique ? "unique " : "") + by.toString();
        if (filters != null && !filters.isEmpty()) {
            toString += String.format("with filters %s", filters.stream().map(WebElementFilter::toString).collect(Collectors.joining(", ")));
        }
        return toString;
    }

    /**
     * Formats an xpath selector
     * @param args
     * @return
     */
    public Locate with(Object... args) {
        Locate locate = new Locate(By.xpath(String.format(preparedFormat, args)));
        locate.unique = unique;
        locate.filters = filters;
        return locate;
    }
}
