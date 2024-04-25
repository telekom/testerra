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
package eu.tsystems.mms.tic.testframework.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import eu.tsystems.mms.tic.testframework.logging.Loggable;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Created on 2024-04-25
 *
 * @author mgn
 */
public class GsonCustomTypeSerializer<T> implements JsonSerializer<T>, Loggable {

    /**
     * Simple serializer which only
     */
    @Override
    public JsonElement serialize(T t, Type src, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        Arrays.stream(t.getClass().getDeclaredMethods())
                .filter(method -> method.getName().startsWith("get"))
                .forEach(method -> {
                    String fieldName = method.getName().substring(3);
                    try {
                        jsonObject.add(fieldName, context.serialize(method.invoke(t)));
                    } catch (Exception e) {
                        log().debug("Cannot serialize {} from {}", fieldName, t);
                    }
                });
        return jsonObject;
    }
}
