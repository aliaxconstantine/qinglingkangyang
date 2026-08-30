export class RetRequestResult<T> {
    requestParams: T;
    url: string;
    description: string;
    constructor(requestParams: T, url: string, description: string) {
      this.requestParams = requestParams;
      this.url = url;
      this.description = description;
    }
}  