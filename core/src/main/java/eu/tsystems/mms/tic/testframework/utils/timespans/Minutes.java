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
package eu.tsystems.mms.tic.testframework.utils.timespans;

/**
 * Created by ta on 10.08.2015.
 */
public class Minutes extends TimeSpan{

    public Minutes(int minutes) {
        super(minutes,60000);
    }

    @Override
    public String toString() {
        return value + " Minutes";
    }
}
