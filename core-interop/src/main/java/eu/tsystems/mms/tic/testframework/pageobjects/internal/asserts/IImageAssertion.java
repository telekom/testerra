package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.io.File;

public interface IImageAssertion extends ActualProperty<File> {
    IQuantifiedPropertyAssertion<Double> pixelDistance(String referenceImageName);
    IFileAssertion file();
}
