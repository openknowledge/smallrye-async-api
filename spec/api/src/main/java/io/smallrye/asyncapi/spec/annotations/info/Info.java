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
package io.smallrye.asyncapi.spec.annotations.info;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation provides metadata about the API. The metadata can be used by the clients if needed.
 *
 * @see <a href="https://www.asyncapi.com/docs/specifications/2.0.0/#a-name-infoobject-a-info-object">Info Object</a>
 **/
@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Info {

    /**
     * <strong>Required</strong>. The title of the application.
     *
     * @return the application's title
     **/
    String title();

    /**
     * <strong>Required</strong>. The version of the API definition.
     *
     * @return the application's version
     **/
    String version();

    /**
     * A short description of the application. CommonMark syntax can be used for rich text representation.
     *
     * @return the application's description
     **/
    String description() default "";

    /**
     * A URL to the Terms of Service for the API. Must be in the format of a URL.
     *
     * @return the application's terms of service
     **/
    String termsOfService() default "";

    /**
     * The contact information for the exposed API.
     *
     * @return a contact for the application
     **/
    Contact contact() default @Contact();

    /**
     * The license information for the exposed API.
     *
     * @return the license of the application
     **/
    License license() default @License(name = "");
}
