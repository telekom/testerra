import {autoinject} from 'aurelia-framework';
import {HttpClient} from 'aurelia-fetch-client';
import {data} from "services/report-model";


/**
 * Data backend service for retrieving and caching data backend responses
 * @author Mike Reiche <mike.reiche@t-systems.com>
 */
@autoinject()
export class DataBackendService {

  constructor(
    private _httpClient: HttpClient,
  ) {
    //  this.httpClient.configure(config => {
    //  config.withBaseUrl(TapConfig.DataBackendBaseUrl);
    //  });
    this._httpClient.configure(config => {
      config
        .useStandardConfiguration()
        .rejectErrorResponses()
        .withDefaults({
          "headers": {
            // "content-type": "application/octet-stream",
            "accept": "*/*"
          }
        });
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

  // @ts-ignore
  getExecution(): Promise<data.ExecutionContext> {
    return this.get("model/execution")
      .then(response => {
        return this.responseToProtobufJSMessage(response, data.ExecutionContext)
      })
  }

}
