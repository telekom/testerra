
package eu.tsystems.mms.tic.testframework.layout.matching;

import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.matching.error.LayoutFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * User: rnhb
 * Date: 21.05.14
 */
public class LayoutMatch {
    private List<ElementMatch> matches = new ArrayList<ElementMatch>();
    private List<LayoutFeature> criticalMatches = new ArrayList<LayoutFeature>();
    private List<LayoutFeature> ignoredCriticalMatches = new ArrayList<LayoutFeature>();
    private List<LayoutFeature> correctMatches = new ArrayList<LayoutFeature>();

    /**
     * Object to store matches we found.
     */
    public LayoutMatch() {
    }

    public void addMatch(LayoutElement templateElement, LayoutElement matchedElement, double rating) {
        ElementMatch match = new ElementMatch(templateElement, matchedElement, rating);
        addMatch(match);
    }

    public List<ElementMatch> getMatches() {
        return matches;
    }

    public void addMatch(ElementMatch match) {
        matches.add(match);
    }

    public List<LayoutFeature> getCriticalMatches() {
        return criticalMatches;
    }

    public void addCriticalMatch(LayoutFeature layoutFeature) {
        criticalMatches.add(layoutFeature);
    }

    public List<LayoutFeature> getIgnoredCriticalMatches() {
        return ignoredCriticalMatches;
    }

    public List<LayoutFeature> getCorrectMatches() {
        return correctMatches;
    }
}
