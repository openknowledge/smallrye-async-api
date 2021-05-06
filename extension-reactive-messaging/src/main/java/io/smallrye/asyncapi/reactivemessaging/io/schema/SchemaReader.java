/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package io.smallrye.asyncapi.reactivemessaging.io.schema;

import java.util.List;

import io.smallrye.asyncapi.reactivemessaging.util.CloudEventUtil;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.Type;

import io.smallrye.asyncapi.core.api.models.schema.SchemaImpl;
import io.smallrye.asyncapi.core.runtime.scanner.spi.AnnotationScannerContext;
import io.smallrye.asyncapi.reactivemessaging.util.TypeUtil;
import io.smallrye.asyncapi.spec.annotations.schema.SchemaType;
import io.smallrye.asyncapi.spec.models.schema.Schema;

public class SchemaReader {

    /**
     * Read schema from method return type
     *
     * @param context the scanning context
     * @param instance the current method
     * @return Schema definition of the method
     */
    public static Schema readReturnType(final AnnotationScannerContext context, final AnnotationInstance instance) {
        if (instance == null) {
            return null;
        }

        Type type = TypeUtil.getReturnType(instance);
        SchemaType schemaType = TypeUtil.typeToSchemaType(type);

        if (schemaType.equals(SchemaType.OBJECT)){
            return new SchemaImpl().ref(CloudEventUtil.createRef(type));
        }

        Schema schema = new SchemaImpl();
        schema.setType(schemaType);

        return schema;
    }

    /**
     * Read schema from method parameter
     *
     * @param context the scanning context
     * @param instance the current method
     * @return Schema definition of the method
     */
    public static Schema readParameterType(final AnnotationScannerContext context, final AnnotationInstance instance) {
        if (instance == null) {
            return null;
        }

        List<Type> parameters = TypeUtil.getParameters(instance);

        if (parameters.size() != 1) {
            return new SchemaImpl();
        }

        Type type = parameters.get(0);

        if (CloudEventUtil.isCloudEvent(type)){
            Schema schema = CloudEventUtil.readCloudEventParameter(instance, context);
            return schema;
        }

        Schema schema = new SchemaImpl();
        schema.setType(TypeUtil.typeToSchemaType(parameters.get(0)));

        String name = instance.target()
            .asMethod()
            .parameterName(0);

        schema.setName(name); //needed for components key
        schema.setTitle(name);

        return schema;
    }
}
