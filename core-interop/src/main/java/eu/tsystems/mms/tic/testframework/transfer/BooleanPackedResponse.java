/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.transfer;

/**
 * Created by pele on 02.06.2015.
 */
public class BooleanPackedResponse<T> {

    private final T response;
    private final boolean aBoolean;

    public BooleanPackedResponse(T response, boolean b) {
        this.response = response;
        this.aBoolean = b;
    }

    public T getResponse() {
        return response;
    }

    public boolean getBoolean() {
        return aBoolean;
    }

    @Override
    public String toString() {
        return "BooleanPackedResponse{" +
                "response=" + response +
                ", aBoolean=" + aBoolean +
                '}';
    }
}
