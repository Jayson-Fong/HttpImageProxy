# HttpImageProxy
[![Java CI with Maven](https://github.com/Jayson-Fong/HttpImageProxy/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/Jayson-Fong/HttpImageProxy/actions/workflows/maven.yml)

A simple HTTP proxy to serve files (and particularly images) securely to an end-user.

Suggested for:
* Serving insecure (non-SSL) images.
* Anonymizing IP addresses and browser information for websites with public post image hot-linking.

A HMAC with a configurable secret and algorithm (`src/main/resources/config.properties`) is used to secure the proxy endpoint.

Example Request: `https://example.com/HttpProxy/proxy/<token>?url=<url>`

Deploy it with a Java Application Server like Apache TomEE.
