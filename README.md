# WebServish
A simple Webb/REST project

## Two simple flows
### request / response
HttpClient --> Controller[/]

```mermaid
sequenceDiagram
    HttpClient->>+RestController: request for root /
    RestController->>+HttpClient: respond with request headers and base64 encoded respond with request headers
```

### request / response with a simple proxy
HttpClient --> Controller[/anything] --> DataClient --> [http://httpbin.org/anything]
```mermaid
sequenceDiagram
    HttpClient->>+RestController: request for '/anything'
    RestController->>+DataClient: Set up Client request
    DataClient->>+Httpbin: request for '/anything'
    Httpbin-->>-DataClient: Httpbin responds
    DataClient-->>-RestController: movin response RestController
    RestController-->>-HttpClient: respons to client
```
