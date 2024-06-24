# Camel OAuth2 Component

This is a Camel component that can be used to retrieve and cache OAuth2 tokens using the client credentials flow.

## Usage
The URI for the component is `oauth2:name`. The name is a user defined name for the retrieved token and should be unique for each client id and secret combination. 

Other parameters you can set on the component and/or endpoint are:

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


