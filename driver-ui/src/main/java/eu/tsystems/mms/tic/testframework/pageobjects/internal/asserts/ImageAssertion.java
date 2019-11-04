package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class ImageAssertion extends AbstractPropertyAssertion<File> implements IImageAssertion {

    public ImageAssertion(PropertyAssertion parentAssertion, AssertionProvider<File> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public IQuantifiedPropertyAssertion<Double> pixelDistance(final String referenceImageName) {
        final AtomicReference<LayoutCheck.MatchStep> atomicMatchStep = new AtomicReference<>();
        return propertyAssertionFactory.quantified(this, new AssertionProvider<Double>() {
            @Override
            public Double getActual() {
                LayoutCheck.MatchStep matchStep = LayoutCheck.matchPixels(provider.getActual(), referenceImageName);
                atomicMatchStep.set(matchStep);
                return matchStep.distance;
            }

            @Override
            public String getSubject() {
                return String.format("pixelDistance(referenceImageName: %s)", referenceImageName);
            }

            @Override
            public void failedFinally(PropertyAssertion assertion) {
                LayoutCheck.MatchStep matchStep = atomicMatchStep.get();
                atomicMatchStep.get();
                if (matchStep!=null && !matchStep.takeReferenceOnly) {
                    LayoutCheck.toReport(matchStep);
                }
            }
        });
    }

    @Override
    public IFileAssertion file() {
        return propertyAssertionFactory.file(this, new AssertionProvider<File>() {
            @Override
            public File getActual() {
                return provider.getActual();
            }

            @Override
            public String getSubject() {
                return String.format("\"%s\"", provider.getActual().getAbsolutePath());
            }
        });
    }
}
