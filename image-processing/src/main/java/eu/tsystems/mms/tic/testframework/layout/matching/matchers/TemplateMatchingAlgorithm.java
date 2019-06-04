package eu.tsystems.mms.tic.testframework.layout.matching.matchers;

import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.core.ValuedPoint2D;
import org.opencv.core.Mat;

import java.util.List;

/**
 * Created by joku on 03.11.2016.
 */
public interface TemplateMatchingAlgorithm {
    List<ValuedPoint2D> findMatchingPoints(LayoutElement layoutElement, Mat imageToMatch);
}