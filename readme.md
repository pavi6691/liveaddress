### Important Note

In order to use search API, First add client with rate limit by calling this rate limit API. then use the same client id in header of Search API

## REST API endpoints

#### Update rateLimit for client

  ```http
  POST /api/v1/ratelimit/{clientId}/{rateLimit} 
  ```

#### Search AutoComplete

  ```http
  GET /api/v1/search?input=BATHGATE

  Header
  X-Client-ID: client-id
  ```

