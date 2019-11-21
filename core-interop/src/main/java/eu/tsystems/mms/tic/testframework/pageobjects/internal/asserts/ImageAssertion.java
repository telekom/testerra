package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.io.File;

/**
 * Allows layout and file assertions
 * @author Mike Reiche
 */
public interface ImageAssertion extends ActualProperty<File> {
    QuantityAssertion<Double> pixelDistance(String referenceImageName);
    FileAssertion file();
}
