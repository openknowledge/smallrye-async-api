/*
 * Copyright 2019 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.smallrye.asyncapi.spec.annotations.security;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a security scheme that can be used by the operations. Supported schemes are:
 *
 * <ul>
 * <li>User/Password.</li>
 * <li>API key (either as user or as password).</li>
 * <li>X.509 certificate.</li>
 * <li>End-to-end encryption (either symmetric or asymmetric).</li>
 * <li>HTTP authentication.</li>
 * <li>HTTP API key.</li>
 * <li>OAuth2’s common flows (Implicit, Resource Owner Protected Credentials, Client Credentials and Authorization Code) as
 * defined in RFC6749.</li>
 * <li>OpenID Connect Discovery.</li>
 * </ul>
 *
 * @see "https://www.asyncapi.com/docs/specifications/2.0.0/#securitySchemeObject"
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SecurityScheme {

    /**
     * <strong>REQUIRED</strong>. The type of the security scheme. Valid values are:
     * "userPassword", "apiKey", "X509", "symmetricEncryption", "asymmetricEncryption", "httpApiKey", "http", "oauth2", and
     * "openIdConnect".
     *
     * @return the type of this SecurityScheme instance
     **/
    SecuritySchemeType type();

    /**
     * A short description for security scheme. CommonMark syntax can be used for rich text representation.
     *
     * @return description of this SecurityScheme instance
     **/
    String description() default "";

    /**
     * <strong>REQUIRED</strong>. The name of the header, query or cookie parameter to be used.
     *
     * @return the name of this SecurityScheme instance
     **/
    String name();

    /**
     * <strong>REQUIRED</strong>. The location of the API key.
     * Valid values are "user" and "password" for apiKey and "query", "header" or "cookie" for httpApiKey.
     *
     * @return the location of the API key
     **/
    SecuritySchemeIn in();

    /**
     * <strong>REQUIRED</strong>. The name of the HTTP Authorization scheme to be used in the Authorization header as defined in
     * RFC7235.
     *
     * @return the name of the HTTP Authorization scheme
     **/
    String scheme() default "";

    /**
     * A hint to the client to identify how the bearer token is formatted.
     *
     * Bearer tokens are usually generated by an authorization server, so this information is primarily for documentation
     * purposes.
     *
     * @return the format of the bearer token
     **/
    String bearerFormat() default "";

    /**
     * <strong>REQUIRED</strong>. An object containing configuration information for the flow types supported.
     *
     * @return flow types supported by this SecurityScheme instance
     **/
    OAuthFlows flows() default @OAuthFlows;

    /**
     * <strong>REQUIRED</strong>. OpenId Connect URL to discover OAuth2 configuration values. This MUST be in the form of a URL.
     *
     * @return URL where OAuth2 configuration values are stored
     **/
    String openIdConnectUrl() default "";
}
