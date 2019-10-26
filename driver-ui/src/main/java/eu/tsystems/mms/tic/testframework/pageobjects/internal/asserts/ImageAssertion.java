package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;

import java.io.File;

public class ImageAssertion extends AbstractAssertion<File> implements IImageAssertion {
    public ImageAssertion(IAssertionProvider<File> provider) {
        super(provider);
    }

    @Override
    public IQuantifiedAssertion<Double, IImageAssertion> pixelDistance(final String referenceImageName) {
        return new QuantifiedAssertion<>(this, new AssertionProvider<Double>() {
            @Override
            public Double actual() {
                LayoutCheck.MatchStep matchStep = LayoutCheck.matchPixels(provider.actual(), referenceImageName);
                return matchStep.distance;
            }

            @Override
            public String subject() {
                return null;
            }
        });
    }

    @Override
    public IFileAssertion file() {
        return new FileAssertion(provider);
    }
}
