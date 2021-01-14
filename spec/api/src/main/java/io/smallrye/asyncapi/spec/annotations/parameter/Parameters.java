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
package io.smallrye.asyncapi.spec.annotations.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes a map of parameters included in a channel name.
 *
 * @see "https://www.asyncapi.com/docs/specifications/2.0.0/#a-name-parametersobject-a-parameters-object"
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Parameters {

    /**
     * The key represents the name of the parameter. It MUST match the parameter name used in the parent channel name.
     *
     * @return the parameters of the channel
     */
    Parameter[] value() default {};
}
