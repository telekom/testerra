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
package eu.tsystems.mms.tic.testframework.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rnhb on 03.12.2015.
 * <p/>
 * A special log for the execution of GuiElement methods.
 */
public class ExecutionLog {

    private final List<String> executionDescription;

    public ExecutionLog() {
        this.executionDescription = new ArrayList<String>();
    }

    public void clearLog() {
        executionDescription.clear();
    }

    public void addMessage(String message) {
        executionDescription.add(message + "\n");
    }

    @Override
    public String toString() {
        return executionDescription.toString();
    }
}
