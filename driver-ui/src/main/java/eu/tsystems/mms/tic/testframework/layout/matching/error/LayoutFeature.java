/*
 * Testerra
 *
 * (C) 2020, René Habermann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.layout.matching.error;

import eu.tsystems.mms.tic.testframework.layout.matching.graph.Node;
import eu.tsystems.mms.tic.testframework.layout.reporting.GraphicalReporter;
import eu.tsystems.mms.tic.testframework.report.model.Serial;
import java.io.Serializable;

/**
 * User: rnhb
 * Date: 21.05.14
 */
public abstract class LayoutFeature implements Serializable {

    private static final long serialVersionUID = Serial.SERIAL;

    protected Node nodeOfElement;
    protected String name;

    protected LayoutFeature(Node nodeOfElement) {
        this.nodeOfElement = nodeOfElement;
        name = this.getClass().getSimpleName();
    }

    public abstract void callbackDraw(GraphicalReporter graphicalReporter);

    public String getName() {
        return name;
    }
}
