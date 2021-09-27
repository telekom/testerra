/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class FailsAnnotationConverter implements AnnotationConverter<Fails> {

    @Override
    public Map<String, Object> toMap(Fails annotation) {
        Map<String, Object> map = new HashMap<>();
        if (!StringUtils.isBlank(annotation.description())) {
            map.put("description", annotation.description());
        }
        if (!StringUtils.isBlank(annotation.ticketString())) {
            map.put("ticketString", annotation.ticketString());
        } else if (annotation.ticketId() > 0) {
            map.put("ticketString", Integer.toString(annotation.ticketId()));
        }
        return map;
    }
}
