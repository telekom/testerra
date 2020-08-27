import {autoinject} from 'aurelia-framework';
import {HttpClient} from 'aurelia-fetch-client';


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

    getProject(): Promise<object> {
        return this.get("data.json")
            .then(response => {
              return response.text().then(text=>JSON.parse(text))
              //    return this.responseToProtobufJSMessage(response, Project)
            })
    }

}
