package eu.tsystems.mms.tic.testframework.report;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.IAnnotation;
import org.testng.internal.ConstructorOrMethod;
import org.testng.internal.annotations.ConfigurationAnnotation;
import org.testng.internal.annotations.IBeforeMethod;
import org.testng.internal.annotations.JDK15AnnotationFinder;

/**
 * Created on 2024-01-12
 *
 * @author mgn
 */
public class DataProvAnnotationFinder extends JDK15AnnotationFinder {
    public DataProvAnnotationFinder(IAnnotationTransformer transformer) {
        super(transformer);
    }

    /**
     * To simulate a dataprovider method as a ConfigurationMethod, this method is needed to fake the annotation
     * to a 'BeforeMethod'.
     */
    @Override
    public <A extends IAnnotation> A findAnnotation(ConstructorOrMethod com, Class<A> annotationClass) {

        if (annotationClass == IBeforeMethod.class) {
            ConfigurationAnnotation dpAnnotation = new ConfigurationAnnotation();
            dpAnnotation.setBeforeTestMethod(true);
            return (A) dpAnnotation;
        } else {
            return super.findAnnotation(com, annotationClass);
        }
    }
}
