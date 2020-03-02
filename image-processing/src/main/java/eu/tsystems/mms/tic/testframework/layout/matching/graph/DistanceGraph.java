
package eu.tsystems.mms.tic.testframework.layout.matching.graph;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.layout.DefaultParameter;
import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import eu.tsystems.mms.tic.testframework.layout.matching.detection.GroupMovementDetector;
import eu.tsystems.mms.tic.testframework.layout.matching.error.ElementMovedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * User: rnhb Date: 23.05.14
 */
public class DistanceGraph {

    /**
     * Logger
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * The Nodes of the graph representing templates.
     */
    private HashSet<TemplateNode> templateNodes;

    /**
     * The Nodes of the graph representing matches.
     */
    private HashSet<MatchNode> matchNodes;

    /**
     * The distance between two matches of one template, that indicates a bad parameter setting.
     */
    private double distanceBetweenMultipleMatchesToProduceWarning;

    /**
     * If at least this fraction of elements has moved as a group, we can infer that the annotated image is displaced (sub image).
     */
    private double minimumSimilarMovementErrorsForDisplacementCorrection;

    public DistanceGraph() {
        templateNodes = new HashSet<TemplateNode>();
        matchNodes = new HashSet<MatchNode>();
    }

    public HashSet<TemplateNode> getTemplateNodes() {
        return templateNodes;
    }

    public HashSet<MatchNode> getMatchNodes() {
        return matchNodes;
    }

    public void createMatchNode(LayoutElement layoutElement, Point2D matchedLocation) {
        MatchNode node = new MatchNode(layoutElement, matchedLocation);
        getTemplateNodeOfElement(layoutElement).connectTo(node);
        matchNodes.add(node);
    }

    public TemplateNode getTemplateNodeOfElement(LayoutElement layoutElement) {
        for (TemplateNode node : templateNodes) {
            if (node.hasLayoutElement(layoutElement)) {
                return node;
            }
        }
        logger.trace("Could not find TemplateNode of Element " + layoutElement + " when trying to add a match.");
        return null;
    }

    public void createTemplateNode(LayoutElement layoutElement) {
        TemplateNode node = new TemplateNode(layoutElement);
        for (TemplateNode existingNode : templateNodes) {
            node.connectTo(existingNode);
        }
        templateNodes.add(node);
    }

    /**
     * Combines MatchNodes at the same location into one MatchNode, changing also the edges.
     */
    public void combineMatchNodes() {
        LinkedList<MatchNodeBin> matchNodeBins = binMatchNodes();
        for (MatchNodeBin bin : matchNodeBins) {
            if (bin.size() == 1) {
                break;
            }
            LinkedList<MatchNode> nodesToDelete = bin.combineNodes();
            for (MatchNode matchNode : nodesToDelete) {
                matchNodes.remove(matchNode);
            }
        }
    }

    /**
     * Summarize all matches at the same location into bins.
     *
     * @return List of MatchNode bins.
     */
    private LinkedList<MatchNodeBin> binMatchNodes() {
        LinkedList<MatchNodeBin> bins = new LinkedList<MatchNodeBin>();
        for (MatchNode matchNode : matchNodes) {
            boolean isInBin = false;
            for (MatchNodeBin bin : bins) {
                if (bin.isInBin(matchNode)) {
                    bin.addNode(matchNode);
                    isInBin = true;
                    break;
                }
            }
            if (!isInBin) {
                MatchNodeBin bin = new MatchNodeBin(matchNode.getPosition(), matchNode.getSize());
                bins.add(bin);
                bin.addNode(matchNode);
            }
        }
        return bins;
    }

    public void printFullReport() {
        for (TemplateNode templateNode : templateNodes) {
            logger.trace("\nThe Template Node " + templateNode);
            for (Edge edge : templateNode.getEdgesToMatchNodes()) {
                logger.trace("      is connected to Match " + edge.getEndNode());
            }
            for (Edge edge : templateNode.getEdgesToTemplateNode()) {
                Node node = edge.getStartNode().equals(templateNode) ? edge.getEndNode() : edge.getStartNode();
                logger.trace("      has a distance of " + edge.getLength() + " to " + node);
            }
        }
        logger.trace("\n");
        for (MatchNode matchNode : matchNodes) {
            logger.trace("Match Node " + matchNode);

            for (Edge edge : matchNode.getEdgesToTemplateNode()) {
                Node node = edge.getStartNode().equals(matchNode) ? edge.getEndNode() : edge.getStartNode();
                logger.trace("      is connected to Template " + edge.getLength() + " to " + node);
            }
            for (Edge edge : matchNode.getEdgesToMatchNodes()) {
                logger.trace("     has a distance of " + edge.getLength() + " to "
                        + edge.getOtherNode(matchNode));
            }
        }
    }

    public void connectMatchNodes() {
        for (MatchNode node1 : matchNodes) {
            for (MatchNode node2 : matchNodes) {
                if (node1 != node2 && !node1.hasEdgeToMatchNode(node2)) {
                    node1.connectTo(node2);
                }
            }
        }
    }

    /**
     * This method will check for a situation, where it is likely that several different matches in the same region were found,
     * due to the parameter settings.
     * As a reminder, for a single template there are often several pixels near to each other that have a high matching score.
     * This is tackled by the algorithm by detecting such cases and summarizing this into a single match.
     * However, for certain types of images like simple, long bars, the matching pixels can be farther away. In this case
     * the report can only give an ambiguous movement error, where a concrete movement error would be better. This can
     * potentially be improved by changing some parameters.
     */
    public void checkForParameterWarnings() {
         distanceBetweenMultipleMatchesToProduceWarning = PropertyManager.getDoubleProperty(
                 TesterraProperties.LAYOUTCHECK_INTERNAL_PARAMETER_4,
                 DefaultParameter.LAYOUTCHECK_INTERNAL_PARAMETER_4);

        HashSet<Edge> warnedEdges = new HashSet<Edge>();
        for (TemplateNode templateNode : templateNodes) {
            if (templateNode.edgesToMatchNodes.size() > 1) {
                for (Edge edge1 : templateNode.edgesToMatchNodes) {
                    for (Edge edge2 : templateNode.edgesToMatchNodes) {
                        if (edge1 != edge2 && !warnedEdges.contains(edge2.getEndNode().getEdgeTo(edge1.getEndNode()))) {
                            Point2D position1 = edge1.getEndNode().getPosition();
                            Point2D position2 = edge2.getEndNode().getPosition();
                            double distanceBetweenMatches = position1.getEuclideanDistance(position2);
                            if (distanceBetweenMatches < distanceBetweenMultipleMatchesToProduceWarning) {
                                warnedEdges.add(edge1.getEndNode().getEdgeTo(edge2.getEndNode()));
                                logger.warn("Found two matches of element at " + templateNode.getPosition() +
                                        " that are quite near to each other (" + distanceBetweenMatches + "px) at " + position1 +
                                        " and " + position2 + ". This could be ok, but it also could be a bad parameter setting. " +
                                        "You have 3 options to tackle this. 1: Increase matchThreshold, to accept only higher" +
                                        " quality matches. 2: Increase minimalDistanceBetweenMatches to summarize matches in a " +
                                        "larger radius. 3: Increase distanceBetweenMultipleMatchesToProduceWarning to suppress" +
                                        " this warning.");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * It is possible to annotate (and reference) only a part of a whole screenshot, while still using a full actual screenshot.
     * In such a case, the elements would be placed in different coordinate systems, which would lead to false positive movement
     * errors.
     * This method tries to detect such a case and adjust both coordinate systems.
     */
    public void incorporateSubImageDisplacement() {
        minimumSimilarMovementErrorsForDisplacementCorrection = PropertyManager.getDoubleProperty(
                TesterraProperties.LAYOUTCHECK_INTERNAL_PARAMETER_3,
                DefaultParameter.LAYOUTCHECK_INTERNAL_PARAMETER_3);

        GroupMovementDetector groupMovementDetector = new GroupMovementDetector();
        List<List<ElementMovedError>> movementErrorGroups = groupMovementDetector.getMovementErrorGroups(this);
        int minimalGroupSize = (int) Math.ceil(minimumSimilarMovementErrorsForDisplacementCorrection * templateNodes.size());
        boolean displacementDetected = false;
        List<ElementMovedError> largestElementMovedErrorGroup = new ArrayList<ElementMovedError>();
        for (List<ElementMovedError> movementErrorGroup : movementErrorGroups) {
            if(movementErrorGroup.size()>largestElementMovedErrorGroup.size()){
                largestElementMovedErrorGroup=movementErrorGroup;
            }
        }
        if (minimalGroupSize <= largestElementMovedErrorGroup.size()) {
            Point2D averageDisplacement = new Point2D(0, 0);
            for (ElementMovedError elementMovedError : largestElementMovedErrorGroup) {
                averageDisplacement = averageDisplacement.add(elementMovedError.getMovement());
            }
            averageDisplacement.multiplyWith(1d / (double) largestElementMovedErrorGroup.size());
            logger.info("Detected that the reference image is displaced by " + averageDisplacement
                    + " in the actual Screenshot");
            displacementDetected = true;

            for (Node node : templateNodes) {
                node.changePositionBy(averageDisplacement);
            }
        }
        if (!displacementDetected) {
            logger.error("Could not detect the position of the reference image in the actual screenshot. This could be due to " +
                    "too many errors, or because the parameter minimumSimilarMovementErrorsForDisplacementCorrection is set" +
                    " too high. Or simply because the subImage is starting at the same point.");
        }
    }
}
