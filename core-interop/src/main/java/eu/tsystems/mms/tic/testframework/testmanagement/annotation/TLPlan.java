
package eu.tsystems.mms.tic.testframework.testmanagement.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;


/**
 * Annotation that indicates to which QualityCenter TestSet test results should be synchronized.
 *
 * @author mrgi
 */
@Target({METHOD, TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TLPlan {

    /**
     * The TestLink Testplan Name.
     */
    String value();
}

