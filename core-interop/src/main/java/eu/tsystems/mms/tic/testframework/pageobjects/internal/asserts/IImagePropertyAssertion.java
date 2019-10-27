package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.io.File;

public interface IImagePropertyAssertion extends IActualProperty<File> {
    IQuantifiedPropertyAssertion<Double> pixelDistance(final String referenceImageName);
    IFilePropertyAssertion file();
}
