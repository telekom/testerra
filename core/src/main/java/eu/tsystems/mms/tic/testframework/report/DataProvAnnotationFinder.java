/*
 * Testerra
 *
 * (C) 2024, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
