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
package eu.tsystems.mms.tic.testframework.execution.worker.finish;

import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.pageobjects.clickpath.ClickPath;
import eu.tsystems.mms.tic.testframework.pageobjects.clickpath.ClickPathNode;

import java.util.List;

/**
 * Created by pele on 19.01.2017.
 */
public class StoreClickPathWorker extends MethodWorker {
    @Override
    public void run() {
        if (isTest()) {
             /*
             * Store clickpath to method
             */
            final List<ClickPathNode> nodeList = ClickPath.getNodeList();
            if (nodeList != null && nodeList.size() > 1) {
// TODO: clickpath
//                // textual
//                final String prettyPrintList = ClickPath.prettyPrintList();
//                methodContext.setClickpathString(prettyPrintList);
//
//                // nodecount and height
//                final int size = ClickPath.getNodeList().size();
//                methodContext.setClickPathNodesCount(size);
//                methodContext.setClickPathHeight(ClickPath.getClickPathHeight());
//
//                // nodelist
//                final String nodes = ClickPath.createNodeList();
//                methodContext.setClickpathNodeList(nodes);
//
//                // edgeslist
//                final String edges = ClickPath.createEdgesList();
//                methodContext.setClickpathEdgesList(edges);
            }

        }
    }
}
