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
package io.smallrye.asyncapi.core.runtime.io.tag;

import org.jboss.jandex.DotName;

import io.smallrye.asyncapi.spec.annotations.tag.Tag;
import io.smallrye.asyncapi.spec.annotations.tag.Tags;

/**
 * Constants related to Server
 */
public class TagConstant {
    static final DotName DOTNAME_TAG = DotName.createSimple(Tag.class.getName());

    public static final DotName DOTNAME_TAGS = DotName.createSimple(Tags.class.getName());

    public static final String PROP_NAME = "name";

    public static final String PROP_DESCRIPTION = "description";

    private TagConstant() {
    }
}