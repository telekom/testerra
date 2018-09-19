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
package eu.tsystems.mms.tic.testframework.model;

/**
 * Created by pele on 01.06.2016.
 */
public class NodeInfo extends HostInfo {

    private Integer vncPort = null;
    private Integer mtsPort = null;

    public NodeInfo(String hostIP, int nodePort, int vncPort, int mtsPort) {
        super(hostIP, nodePort);
        this.mtsPort = mtsPort;
        this.vncPort = vncPort;
    }

    public NodeInfo(String host, int port) {
        super(host, port);
    }

    public boolean hasVNC() {
        return (vncPort != null && vncPort > 0);
    }

    public int getVNCPort() {
        return vncPort;
    }

    public boolean hasMTSServer() {
        return (mtsPort != null && mtsPort > 0);
    }

    public int getMTSPort() {
        return mtsPort;
    }

    @Override
    public String toString() {
        return super.toString()
                + ((hasVNC()) ? (" with VNC on port " + vncPort) : "")
                + ((hasMTSServer()) ? (" with MTSServer on port " + mtsPort) : "");
    }

    /**
     * Set maintenance service port.
     * 
     * @param value value to set
     */
    public void setMTSPort(int value) {
        mtsPort = value;
    }
}
