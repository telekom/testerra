package eu.tsystems.mms.tic.testframework.layout.matching.detection;

import eu.tsystems.mms.tic.testframework.layout.matching.LayoutMatch;
import eu.tsystems.mms.tic.testframework.layout.matching.error.CorrectMatch;
import eu.tsystems.mms.tic.testframework.layout.matching.error.LayoutFeature;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.DistanceGraph;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Edge;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Node;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.TemplateNode;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by rnhb on 09.12.2014.
 */
public class CorrectMatchDetector extends FeatureDetector {

    @Override
    protected List<LayoutFeature> findFeatures(DistanceGraph distanceGraph, LayoutMatch layoutMatch) {
        List<LayoutFeature> errors = new LinkedList<LayoutFeature>();
        List<CorrectMatch> movementErrors = getMovementErrors(distanceGraph);
        for (CorrectMatch movementError : movementErrors) {
            errors.add(movementError);
        }
        return errors;
    }

    protected List<CorrectMatch> getMovementErrors(DistanceGraph distanceGraph) {
        List<CorrectMatch> correctMatches = new LinkedList<CorrectMatch>();
        for (TemplateNode templateNode : distanceGraph.getTemplateNodes()) {
            List<Edge> edgesToMatchNodes = templateNode.getEdgesToMatchNodes();
            for (Edge edgeToMatchNode : edgesToMatchNodes) {
                if (isEdgeToSameLocation(edgeToMatchNode, templateNode)) {
                    Node otherNode = edgeToMatchNode.getOtherNode(templateNode);
                    correctMatches.add(new CorrectMatch(templateNode, otherNode));
                    break;
                }
            }
        }
        return correctMatches;
    }
}
