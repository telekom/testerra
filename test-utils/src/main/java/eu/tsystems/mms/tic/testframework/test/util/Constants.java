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
/* 
 * Created on 23.02.2012
 * 
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.test.util;

/**
 * Define some Constants used for tests.
 * 
 * @author sepr
 */
public final class Constants {

    /** IP Address of remote selenium server. */
    public static final String TESTSERVERIP = "192.168.60.239";

    /** IP Address of remote selenium server. */
    public static final String SELENIUMHUB = "192.168.61.183";

    /** Path to Firefox 10 Installation. */
    public static final String ALTFIREFOXPATH = "C:\\Program Files\\Mozilla Firefox10\\firefox.exe";

    /** Default BaseURL used by Tests. */
    public static final String BASEURL = "http://192.168.60.239/";

    /** Alternative BaseURL to test other sites. */
    public static final String ALTBASEURL = "http://192.168.60.239/sub/";

    /** URL to input.html, which is used for some tests. */
    public static final String INPUTPAGEURL = BASEURL + "WebSitesForTests/Input/input.html";

    /** URL to multiSelectBox.html, which is used for some tests. */
    public static final String SELECTPAGEURL = BASEURL + "WebSitesForTests/Select/multiSelectBox.html";

    /** URL to uploadfeld.html, which is used for some tests. */
    public static final String UPLOADEPAGEURL = BASEURL + "WebsitesForTests/Upload/uploadfeld.html";

    /** URL to table.html, which is used for some tests. */
    public static final String TABLEPAGEURL = BASEURL + "WebsitesForTests/Table/table.html";

    /** URL to the action waiter site, which is used for some tests. */
    public static final String ACTIONWAITERPAGEURL = BASEURL + "WebsitesForTests/ActionWaiter/Startseite.html";

    /** URL to the frameset site, which is used for some tests. */
    public static final String FRAMESETPAGEURL = BASEURL + "WebsitesForTests/Frameset/index.htm";

    /** URL to the list site, which is used for some tests. */
    public static final String LISTPAGEURL = BASEURL + "WebsitesForTests/List/List.html";

    /** A default timeout of 10 seconds. */
    public static final String TIMEOUT = "10000";

    /**
     * hide Constructor.
     */
    private Constants() {
    }

}
