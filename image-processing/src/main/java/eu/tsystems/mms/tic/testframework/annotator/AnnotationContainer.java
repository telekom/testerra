/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.annotator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Class to serialize annotations into a file.
 *
 * Used by Annotator and LayoutComparator.
 */
public class AnnotationContainer implements Serializable {
    private static final long serialVersionUID = 2095081099028549391L;

    private Color annotationColor;

    private String baseImagePath;
    private String saveObjectPath;

    private List<Rectangle> annotations;

    /**
     * Default constructor.
     *
     * @param annotationColor Color for annotations.
     * @param annotations List of annotations.
     */
    public AnnotationContainer(Color annotationColor, List<Rectangle> annotations) {
        setAnnotationColor(annotationColor);
        setAnnotations(annotations);
    }

    public Color getAnnotationColor() {
        return annotationColor;
    }

    public String getBaseImagePath() {
        return baseImagePath;
    }

    public String getSaveObjectPath() {
        return saveObjectPath;
    }

    public List<Rectangle> getAnnotations() {
        return annotations;
    }

    public void setAnnotationColor(Color annotationColor) {
        this.annotationColor = annotationColor;
    }

    public void setAnnotations(List<Rectangle> annotations) {
        this.annotations = annotations;
    }

    /**
     * Write this Object to a json File.
     *
     * @param jsonFile File to write detials into.
     * @throws IOException Exception handling file.
     */
    public void toJson(File jsonFile) throws IOException {
        Gson gson = new Gson();
        JsonObject container = new JsonObject();
        container.add("color", gson.toJsonTree(annotationColor));
        container.add("annotations", gson.toJsonTree(annotations));
        String jsonString = gson.toJson(container);
        FileOutputStream fileOutputStream = new FileOutputStream(jsonFile);
        fileOutputStream.write(jsonString.getBytes());
        fileOutputStream.close();
    }

    /**
     * Deserialize {@link AnnotationContainer} from json file.
     *
     * @param jsonFile File to read.
     * @return {@link AnnotationContainer}
     * @throws IOException Exception handling File.
     */
    public static AnnotationContainer readFromJson(File jsonFile) throws IOException {
        FileInputStream fis = new FileInputStream(jsonFile);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        String json = sb.toString();
        bufferedReader.close();
        Gson gson = new Gson();
        JsonElement jsonElement = new JsonParser().parse(json);
        Color color = gson.fromJson(jsonElement.getAsJsonObject().get("color"), Color.class);
        Type listType = new TypeToken<List<Rectangle>>() {
        }.getType();
        List<Rectangle> annotations = gson.fromJson(jsonElement.getAsJsonObject().get("annotations"), listType);
        return new AnnotationContainer(color, annotations);
    }
}
