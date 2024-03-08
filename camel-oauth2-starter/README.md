# Camel OAuth2 Starter

This is a the Spring Boot starter for the Camel OAuth2 component that can be used to retrieve and cache OAuth2 tokens using the client credentials flow.

## Usage
You can configure the following parameters at component level using the `camel.component.oauth2` prefix.

| Name                       | Default                                               | Description                                                 |
|----------------------------|-------------------------------------------------------|-------------------------------------------------------------|
| `clientId`                 |     | The client id for the OAuth2 server.                        |
| `clientSecret`             |     | The client secret for the OAuth2 server.                    |
| `accessTokenUrl`           |     | The URL to the token endpoint.                              |
| `tokenExpirationThreshold` | 300 | The amount of seconds to substract from the token lifetime. |
| `scope`                    |     | The scope to use when retrieving the token.                 |
| `redirectURI`              |     | The redirect URI.                                           |

## License
This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE.md) file for details.


