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
package eu.tsystems.mms.tic.testframework.pageobjects.clickpath;

import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by piet on 20.10.15.
 */
public final class ClickPath {

    private static ThreadLocal<List<ClickPathNode>> nodes = new ThreadLocal<List<ClickPathNode>>();
    private static ThreadLocal<ClickPathNode> lastClickNode = new ThreadLocal<ClickPathNode>();

    private static void checkList() {
        List<ClickPathNode> nodes = ClickPath.nodes.get();
        if (nodes == null) {
            /*
            Create new node list
             */
            nodes = new ArrayList<ClickPathNode>();
            ClickPath.nodes.set(nodes);
        }
    }

    private static void addNode(ClickPathNode node) {
        checkList();

        // add it
        nodes.get().add(node);
    }

    private static ClickPathNode getLastNode() {
        checkList();
        List<ClickPathNode> nodes = ClickPath.nodes.get();
        if (nodes.size() == 0) {
            return null;
        }
        return nodes.get(nodes.size() - 1);
    }

    private static ClickPathNode getPreviousNode() {
        checkList();
        List<ClickPathNode> nodes = ClickPath.nodes.get();
        if (nodes.size() <= 1) {
            return null;
        }
        return nodes.get(nodes.size() - 2);
    }

    private static void joinLast2Nodes() {
        ClickPathNode lastNode = getLastNode();
        ClickPathNode previousNode = getPreviousNode();
        if (lastNode != null && previousNode != null) {
            List<ClickPathElement> elements = lastNode.getElements();
            previousNode.addElements(elements);

            // remove last node
            lastNode.clear();
            List<ClickPathNode> nodes = ClickPath.nodes.get();
            nodes.remove(nodes.size() - 1);
        }
    }

    public static void stack(ClickPathElement clickPathElement, WebDriver driver) {
        stack(clickPathElement, null, driver);
    }

    public static void stack(ClickPathElement clickPathElement, WebElement webElement, WebDriver driver) {
        if (driver != null) {
            final String sessionId = WebDriverManagerUtils.getSessionId(driver);
            if (!StringUtils.isStringEmpty(sessionId) && !sessionId.endsWith(WebDriverManager.DEFAULT_SESSION_KEY)) {
                clickPathElement.enhanceInfo(" (Session: " + sessionId + ")");
            }
        }

        if (clickPathElement == null) {
            return;
        }

        if (clickPathElement.getType() != ClickPathElement.CPEType.CLICK) {
            ClickPath.lastClickNode.remove();
        }

        switch (clickPathElement.getType()) {
            case CLICK: {
                /*
                Check lastClickNode to provide better information
                 */
                final ClickPathNode lastClickNode = ClickPath.lastClickNode.get();
                if (lastClickNode != null && webElement != null && lastClickNode.getWebElement() != null) {
                    if (lastClickNode.getWebElement().equals(webElement)) {
                        // then do not replace it
                        break;
                    }
                }

                ClickPathNode lastNode = getLastNode();
                if (lastNode != null && lastNode.getType() == ClickPathNode.Type.CLICK) {
                    lastNode.setToSomething();

                    checkIfLast2NodesAreJoinable();
                }

                ClickPathNode node = new ClickPathNode(clickPathElement);
                if (webElement != null) {
                    node.setWebElement(webElement);
                }
                addNode(node);
                ClickPath.lastClickNode.set(node);
            }
            break;
            case GET: {
                ClickPathNode node = new ClickPathNode(clickPathElement);
                addNode(node);
            }
            break;
            case NON_CLICK_ACTION: {
                ClickPathNode lastNode = getLastNode();
                if (lastNode!= null && webElement != null && lastNode.getWebElement() != null) {
                    if (lastNode.getWebElement().equals(webElement)) {
                        // then do not replace it
                        break;
                    }
                }

                if (lastNode == null || lastNode.getType() == ClickPathNode.Type.PAGE || lastNode.getType() == ClickPathNode.Type.GET) {
                    ClickPathNode node = new ClickPathNode(clickPathElement);
                    if (webElement != null) {
                        node.setWebElement(webElement);
                    }
                    addNode(node);
                }
                else {
                    lastNode.setToSomething();
                    lastNode.addElement(clickPathElement);

                    checkIfLast2NodesAreJoinable();
                }
            }
            break;
            case PAGE: {
                ClickPathNode node = new ClickPathNode(clickPathElement);
                addNode(node);
            }
            break;
            default: {
                ClickPathNode node = new ClickPathNode(clickPathElement);
                addNode(node);
            }
        }
    }

    private static void checkIfLast2NodesAreJoinable() {
        // check if joinable
        ClickPathNode previousNode = getPreviousNode();
        if (previousNode != null && previousNode.getType() == ClickPathNode.Type.OTHER_ACTIONS) {
            joinLast2Nodes();
        }
    }

    public static List<ClickPathNode> getNodeList() {
        return nodes.get();
    }

    public static void clear() {
        nodes.remove();
        lastClickNode.remove();
    }

    public static String prettyPrintList() {
        checkList();
        String msg = "";
        List<ClickPathNode> nodes = ClickPath.nodes.get();
        int step = 0;
        for (ClickPathNode node : nodes) {
            ClickPathNode.Type type = node.getType();
            msg += "<font style='color: " + type.getColorCode() + ";'>";

            step++;

            String stepMsg;
            switch (type) {
                case GET:
                    stepMsg = step + ") " + type + " " + node.getFirstElementInfo();
                    break;
                case CLICK:
                    stepMsg = step + ") " + type + " " + node.getFirstElementInfo();
                    break;
                case PAGE:
                    stepMsg = step + ") " + node.getFirstElementInfo();
                    break;
                case OTHER_ACTIONS:
                    stepMsg = step + ") " + type;
                    List<ClickPathElement> elements = node.getElements();
                    for (ClickPathElement element : elements) {
                        final ClickPathElement.CPEType type1 = element.getType();
                        if (type1 == ClickPathElement.CPEType.NON_CLICK_ACTION) {
                            stepMsg += "\n     - " + element.getInfo();
                        }
                        else {
                            stepMsg += "\n     - " + type1 + " " + element.getInfo();
                        }
                    }
                    break;
                case ERROR:
                    stepMsg = step + ") " + type + " " + node.getFirstElementInfo();
                    break;
                default:
                    stepMsg = step + ") not yet implemented: " + type;
            }

            stepMsg = StringUtils.prepareStringForHTML(stepMsg);
            msg += stepMsg + "</font><br/>";
        }

        return msg;
    }

    private static final boolean WITH_LOOPS = true;

    public static String createNodeList() {
        List<String> pageNames = new ArrayList<String>();
        final List<ClickPathNode> clickPathNodes = nodes.get();


        int nodeCount = 0;
        String out = "[";
        if (clickPathNodes != null) {

            for (ClickPathNode node : clickPathNodes) {
                nodeCount++;
                /*
                some text modifications
                 */
                String firstElementInfo = node.getFirstElementInfo();
                // escape '
                firstElementInfo = firstElementInfo.replaceAll("\'", "\\\\\'");
                // shorten the click text
                if (node.getType() == ClickPathNode.Type.CLICK) {
                    int maxLength = 20;
                    if (firstElementInfo.length() > maxLength)
                    firstElementInfo = firstElementInfo.substring(0, maxLength) + "...";
                }

                // skip when already visited
                if (WITH_LOOPS) {
                    if (node.getType() == ClickPathNode.Type.PAGE) {
                        final String pageName = firstElementInfo;
                        if (pageNames.contains(pageName)) {
                            // already visited
                            continue;
                        }
                        else {
                            pageNames.add(pageName);
                        }
                    }
                }

                out += "\n{ data: { ";
                final ClickPathNode.Type nodeType = node.getType();
                String nodeName = nodeType.name();

                // display page name
                switch (nodeType) {
                    case GET:
                        nodeName = "OPEN\\n" + firstElementInfo;
                        break;
                    case CLICK:
                        nodeName += "\\n" + firstElementInfo;
                        break;
                    case PAGE:
                        nodeName = firstElementInfo;
                        break;
                    case OTHER_ACTIONS:
                    default:
                        // modify nothing
                }

                final int nodeId = node.getNodeCount();

                // add node count
                if (nodeType != ClickPathNode.Type.PAGE) {
                    nodeName = nodeCount + ")" + nodeName;
                }

                out += "id: 'n" + nodeId + "', name: '" + nodeName + "', ";

                // layout
                switch (nodeType) {
                    case GET:
                        out += "fontSize: '14px', weight: 65, faveColor: '" + nodeType.getColorCode() + "', faveShape: '" + nodeType.getShape() + "'";
                        break;
                    case CLICK:
                        out += "fontSize: '14px', weight: 65, faveColor: '" + nodeType.getColorCode() + "', faveShape: '" + nodeType.getShape() + "'";
                        break;
                    case PAGE:
                        out += "fontSize: '20px', weight: 100, faveColor: '" + nodeType.getColorCode() + "', faveShape: '" + nodeType.getShape() + "'";
                        break;
                    case OTHER_ACTIONS:
                        out += "fontSize: '14px', weight: 65, faveColor: '" + nodeType.getColorCode() + "', faveShape: '" + nodeType.getShape() + "'";
                        break;
                    case ERROR:
                        out += "fontSize: '20px', weight: 100, faveColor: '" + nodeType.getColorCode() + "', faveShape: '" + nodeType.getShape() + "'";
                        break;
                    default:
                        out += "fontSize: '14px', weight: 65, faveColor: '#86B342', faveShape: 'rectangle'";
                }

                out += " } },";
            }

            // remove last comma
            out = out.substring(0, out.length() - 1);

            // close array
            out += "\n]";

//            System.out.println(out);

            return out;
        }

        return null;
        /*"[\n" +
                "            { data: { id: 'j', name: 'Jerry', weight: 65, faveColor: '#6FB1FC', faveShape: 'triangle' } },\n" +
                "            { data: { id: 'e', name: 'Elaine', weight: 45, faveColor: '#EDA1ED', faveShape: 'ellipse' } },\n" +
                "            { data: { id: 'k', name: 'Kramer', weight: 75, faveColor: '#86B342', faveShape: 'octagon' } },\n" +
                "            { data: { id: 'g', name: 'George', weight: 70, faveColor: '#F5A45D', faveShape: 'rectangle' } }\n" +
                "            ]";
                */
    }

    public static String createEdgesList() {
        Map<String, String> pageNamesToNodeIds = new HashMap<String, String>();
        Map<String, Integer> pageNamesCounters = new HashMap<String, Integer>();

        final List<ClickPathNode> clickPathNodes = nodes.get();
        String out = "[";
        if (clickPathNodes != null) {
            final int size = clickPathNodes.size();
            for (int a = 0; a < size - 1; a++) {
                /*
                a<b
                 */
                int b = a + 1;
                final ClickPathNode nodeA = clickPathNodes.get(a);
                final ClickPathNode nodeB = clickPathNodes.get(b);

                out += "\n{ data: { ";

                int outGoingCount = 1;
                if (WITH_LOOPS) {
                    // default value for all other node types
                    String nodeIdA = nodeA.getNodeCount() + "";
                    String nodeIdB = nodeB.getNodeCount() + "";

                    // page specific
                    if (nodeA.getType() == ClickPathNode.Type.PAGE) {
                        final String pageName = nodeA.getFirstElementInfo();

                        if (pageNamesToNodeIds.containsKey(pageName)) {
                            nodeIdA = pageNamesToNodeIds.get(pageName);
                            outGoingCount = pageNamesCounters.get(pageName);
                        } else {
                            nodeIdA = "" + nodeA.getNodeCount();
                            pageNamesToNodeIds.put(pageName, nodeIdA);
                            pageNamesCounters.put(pageName, 1);
                        }
                    }

                    if (nodeB.getType() == ClickPathNode.Type.PAGE) {
                        final String pageName = nodeB.getFirstElementInfo();

                        if (pageNamesToNodeIds.containsKey(pageName)) {
                            nodeIdB = pageNamesToNodeIds.get(pageName);
                            if (pageNamesCounters.containsKey(pageName)) {
                                Integer visitCount = pageNamesCounters.get(pageName);
                                visitCount++;
                                pageNamesCounters.remove(pageName);
                                pageNamesCounters.put(pageName, visitCount);
                            }
                        }
                        else {
                            nodeIdB = "" + nodeB.getNodeCount();
                            pageNamesToNodeIds.put(pageName, nodeIdB);
                            pageNamesCounters.put(pageName, 1);
                        }
                    }

                    String label = "";
                    if (outGoingCount > 0) {
                        label = outGoingCount + "";
                    }
                    // data line
                    out += "edgeLabel: '" + label + "', " + "source: 'n" + nodeIdA + "', target: 'n" + nodeIdB + "', ";
                }
                else {
                    out += "source: 'n" + nodeA.getNodeCount() + "', target: 'n" + nodeB.getNodeCount() + "', ";
                }

                // layout
                out += "faveColor: '#EDA1ED', strength: 100";

                out += " } },";
            }

            // remove last comma
            out = out.substring(0, out.length() - 1);

            // close array
            out += "\n]";

//            System.out.println(out);
//
            return out;
        }

        return null;
        /*"[\n" +
                "            { data: { source: 'j', target: 'e', faveColor: '#6FB1FC', strength: 90 } },\n" +
                "            { data: { source: 'j', target: 'k', faveColor: '#6FB1FC', strength: 70 } },\n" +
                "            { data: { source: 'j', target: 'g', faveColor: '#6FB1FC', strength: 80 } },\n" +
                "\n" +
                "            { data: { source: 'e', target: 'j', faveColor: '#EDA1ED', strength: 95 } },\n" +
                "            { data: { source: 'e', target: 'k', faveColor: '#EDA1ED', strength: 60 }, classes: 'questionable' },\n" +
                "\n" +
                "            { data: { source: 'k', target: 'j', faveColor: '#86B342', strength: 100 } },\n" +
                "            { data: { source: 'k', target: 'e', faveColor: '#86B342', strength: 100 } },\n" +
                "            { data: { source: 'k', target: 'g', faveColor: '#86B342', strength: 100 } },\n" +
                "\n" +
                "            { data: { source: 'g', target: 'j', faveColor: '#F5A45D', strength: 90 } }\n" +
                "            ]";
                */
    }

    public static int getClickPathHeight() {
        final List<ClickPathNode> nodeList = getNodeList();
        if (nodeList == null) {
            return 0;
        }
        if (!WITH_LOOPS) {
            return nodeList.size();
        }

        int length = 0;
        Map<String, Integer> visitedNodes = new HashMap<String, Integer>();
        for (ClickPathNode node : nodeList) {
            if (node.getType() == ClickPathNode.Type.PAGE) {
                final String nodeName = node.getFirstElementInfo();
                if (!visitedNodes.containsKey(nodeName)) {
                    length++;
                    visitedNodes.put(nodeName, length);
                }
                else {
                    // reset length
                    length = visitedNodes.get(nodeName);
                }
            }
            else {
                length++;
            }
        }

//        System.out.println("Length: " + length);

        return length;
    }
}
