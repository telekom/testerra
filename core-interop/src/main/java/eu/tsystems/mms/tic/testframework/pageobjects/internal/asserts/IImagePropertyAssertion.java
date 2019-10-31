package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.io.File;

public interface IImagePropertyAssertion extends ActualProperty<File> {
    IQuantifiedPropertyAssertion<Double> pixelDistance(String referenceImageName);
    IFilePropertyAssertion file();
}
