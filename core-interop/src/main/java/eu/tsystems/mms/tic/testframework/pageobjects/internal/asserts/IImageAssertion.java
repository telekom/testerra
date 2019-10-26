package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.io.File;

public interface IImageAssertion extends IAssertion<File> {
    IQuantifiedAssertion<Double> pixelDistance(final String referenceImageName);
    IFileAssertion file();
}
