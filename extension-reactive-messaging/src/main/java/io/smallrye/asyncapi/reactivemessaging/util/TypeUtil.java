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
package io.smallrye.asyncapi.reactivemessaging.util;

import io.smallrye.asyncapi.core.api.models.schema.SchemaImpl;
import io.smallrye.asyncapi.spec.annotations.schema.SchemaType;
import io.smallrye.asyncapi.spec.models.schema.Schema;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

import java.util.List;

public class TypeUtil {

    public static Type getReturnType(final AnnotationInstance instance) {
        if (instance == null) {
            return null;
        }
        return instance.target()
                .asMethod()
                .returnType();
    }

    public static SchemaType typeToSchemaType(final Type type) {
        if ("java.lang.String".equals(type.toString())) {
            return SchemaType.STRING;
        }

        switch (type.kind()) {
            case ARRAY:
                return SchemaType.ARRAY;
            case PRIMITIVE:
                return primitiveTypeToSchemaType(type);
            default:
                return SchemaType.OBJECT;
        }
    }

    public static List<Type> getParameters(final AnnotationInstance instance){
        return instance.target().asMethod().parameters();
    }

    private static SchemaType primitiveTypeToSchemaType(final Type type) {
        switch (type.asPrimitiveType().primitive()) {
            case BOOLEAN:
                return SchemaType.BOOLEAN;
            case INT:
                return SchemaType.INTEGER;
            case FLOAT:
            case LONG:
            case SHORT:
            case DOUBLE:
                return SchemaType.NUMBER;
            default:
                return SchemaType.OBJECT;
        }
    }

    public static boolean isParameterized(Type type) {
        return type.kind().equals(Type.Kind.PARAMETERIZED_TYPE);
    }

    public static Schema readPrimitiveClass(final Type type){

        if (DotName.createSimple("java.lang.Boolean").equals(type.name())) {
            return new SchemaImpl().type(SchemaType.BOOLEAN);
        } else if (DotName.createSimple("java.lang.String").equals(type.name())) {
            return new SchemaImpl().type(SchemaType.STRING);
        }else if (DotName.createSimple("java.lang.Integer").equals(type.name())) {
            return new SchemaImpl().type(SchemaType.INTEGER);
        }else if (DotName.createSimple("java.lang.Double").equals(type.name()) ||
            DotName.createSimple("java.lang.Float").equals(type.name()) ||
            DotName.createSimple("java.lang.Short").equals(type.name())) {
            return new SchemaImpl().type(SchemaType.NUMBER);
        }

        return new SchemaImpl();
    }
}
