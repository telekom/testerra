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
 * Copyright(c) 2013 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.report.threadvisualizer;

import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;

/**
 * Created with IntelliJ IDEA. User: pele Date: 04.01.13 Time: 10:14 To change this template use File | Settings | File
 * Templates.
 */
public final class Util {

    /**
     * Html break code.
     */
    private static final String LINEBREAK = "<br/>";

    /**
     * Hide default constructor.
     */
    private Util() {

    }

    /**
     * Gets html content formatted for the thread visualizer.
     *
     * @param methodContext  {@link MethodContext}
     * @return Formatted HTML content.
     */
    public static String getFormattedContent(final MethodContext methodContext) {
        String html = "";


        String style = "background: linear-gradient(to top, " + methodContext.status.color + " 0%, white 100%); ";

        if (methodContext.isConfigMethod()) {
            style += "border: 2px solid #ffa500; ";
        }
        else {
            style += "border: 1px solid " + methodContext.status.color + "; ";
        }

        style += "border-radius: 6px 6px 6px 6px; " +
                "box-shadow: 0 1px 0 0 #FFFFFF inset; " +
                "color: #777777; " +
                "font-family: arial; " +
                "font-size: 15px; " +
                "font-weight: bold; " +
                "padding-left: 2px; " +
                "padding-top: 2px; " +
                "padding-bottom: 2px; " +
                "text-decoration: none; " +
                "line-height: 15px;";

        /*
         * build the formatted html content of one event bubble
         */
        html += "<div class='tooltip' style='" + style + "'>";

        html += "<a onclick='showLoadingIn(null);' href='methods/" + methodContext.methodRunIndex + ".html'>";
        html += "<font size='3' color='black'>" + methodContext.name + "</font>";
        html += LINEBREAK;
        html += "<font size='1' color='grey'>" + methodContext.status.title + "</font>";
        html += LINEBREAK;
        html += "<font size='1' color='white'>" + methodContext.getEffectiveClassContext().name + "</font>";
        html += LINEBREAK;
        html += "<font size='1' color='white'>(" + methodContext.methodRunIndex + ")</font>";

        html += "</a>";
        html += "</div>";

        return html;
    }

}
