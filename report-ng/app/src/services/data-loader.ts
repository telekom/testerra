/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import {autoinject} from 'aurelia-framework';
import {HttpClient} from 'aurelia-fetch-client';
import {data} from "services/report-model";
import ExecutionAggregate = data.ExecutionAggregate;
import File = data.File;

/**
 * Data backend service for retrieving and caching data backend responses
 * @author Mike Reiche <mike.reiche@t-systems.com>
 */
@autoinject()
export class DataLoader {

    constructor(
        private _httpClient: HttpClient,
    ) {
        //  this.httpClient.configure(config => {
        //  config.withBaseUrl(TapConfig.DataBackendBaseUrl);
        //  });
        this._httpClient.configure(config => {
            config
                .useStandardConfiguration()
            // .rejectErrorResponses()
            // .withDefaults({
            //   "headers": {
            //     // "content-type": "application/octet-stream",
            //     "accept": "*/*"
            //   }
            // });
            ;
        });
    }

    protected get(path: string): Promise<Response> {
        return this._httpClient.fetch(path, {
            method: "GET",
        });
    }

    protected responseToProtobufJSMessage(response: Response, messageClass) {
        return response.arrayBuffer().then(buffer => {
            return messageClass.decode(new Uint8Array(buffer))
        });
    }

    getExecutionAggregate(): Promise<ExecutionAggregate> {
        return this.get("model/execution")
            .then(response => {
                return this.responseToProtobufJSMessage(response, ExecutionAggregate)
            })
    }

    getFile(id: string): Promise<File> {
        return this.get("model/files/" + id)
            .then(response => {
                return this.responseToProtobufJSMessage(response, File)
            })
    }
}
