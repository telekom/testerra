/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import org.apache.commons.lang3.SerializationUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.Serializable;

public abstract class AbstractWebDriverRequest extends AbstractWebDriverConfiguration implements Serializable, WebDriverRequest {
    private String sessionKey;
    private DesiredCapabilities desiredCapabilities;

    public String getSessionKey() {
        return sessionKey;
    }

    public AbstractWebDriverRequest setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
        return this;
    }

    public DesiredCapabilities getDesiredCapabilities() {
        if (this.desiredCapabilities == null) {
            this.desiredCapabilities = new DesiredCapabilities();
        }
        return desiredCapabilities;
    }

    /**
     * Cloning of DesiredCapabilites with SerializationUtils occurs org.apache.commons.lang3.SerializationException: IOException while reading or closing cloned object data
     * -> We have to backup the current caps and clone WebDriverRequest without caps. After cloning the original caps are added again.
     * -> Caps can cloned via merge() method.
     *
     * @return
     */
    public AbstractWebDriverRequest clone() {
        Class<? extends AbstractWebDriverRequest> aClass = this.getClass();
        DesiredCapabilities cloneCaps = new DesiredCapabilities();
        boolean isCaps = this.desiredCapabilities != null;
        // Backup original caps
        if (isCaps) {
            cloneCaps = new DesiredCapabilities();
            cloneCaps.merge(this.desiredCapabilities);
            this.desiredCapabilities = null;
        }
        AbstractWebDriverRequest clone = SerializationUtils.clone(this);
        // Write back original caps to this and to clone object.
        if (isCaps) {
            clone.getDesiredCapabilities().merge(cloneCaps);
            this.desiredCapabilities = cloneCaps;
        }
        return aClass.cast(clone);
    }
}
