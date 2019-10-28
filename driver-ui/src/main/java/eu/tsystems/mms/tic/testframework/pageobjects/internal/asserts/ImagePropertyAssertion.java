package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class ImagePropertyAssertion extends AbstractPropertyAssertion<File> implements IImagePropertyAssertion {
    public ImagePropertyAssertion(AssertionProvider<File> provider) {
        super(provider);
    }

    @Override
    public IQuantifiedPropertyAssertion<Double> pixelDistance(final String referenceImageName) {
        final AtomicReference<LayoutCheck.MatchStep> atomicMatchStep = new AtomicReference<>();
        return propertyAssertionFactory.quantified(new AssertionProvider<Double>(this) {
            @Override
            public Double actual() {
                LayoutCheck.MatchStep matchStep = LayoutCheck.matchPixels(provider.actual(), referenceImageName);
                atomicMatchStep.set(matchStep);
                return matchStep.distance;
            }

            @Override
            public String subject() {
                return String.format("pixelDistance(referenceImageName: %s)", referenceImageName);
            }

            @Override
            public void failed() {
                LayoutCheck.MatchStep matchStep = atomicMatchStep.get();
                atomicMatchStep.get();
                if (matchStep!=null && !matchStep.takeReferenceOnly) {
                    LayoutCheck.toReport(matchStep);
                }
            }
        });
    }

    @Override
    public IFilePropertyAssertion file() {
        return propertyAssertionFactory.file(new AssertionProvider<File>(this) {
            @Override
            public File actual() {
                return provider.actual();
            }

            @Override
            public Object subject() {
                return String.format("\"%s\"", provider.actual().getAbsolutePath());
            }
        });
    }
}
