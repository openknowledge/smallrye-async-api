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

package io.smallrye.asyncapi.core.runtime.scanner.dataobject;

import static io.smallrye.asyncapi.core.runtime.scanner.AsyncApiDataObjectScanner.ARRAY_TYPE_OBJECT;
import static io.smallrye.asyncapi.core.runtime.scanner.AsyncApiDataObjectScanner.COLLECTION_TYPE;
import static io.smallrye.asyncapi.core.runtime.scanner.AsyncApiDataObjectScanner.ENUM_TYPE;
import static io.smallrye.asyncapi.core.runtime.scanner.AsyncApiDataObjectScanner.ITERABLE_TYPE;
import static io.smallrye.asyncapi.core.runtime.scanner.AsyncApiDataObjectScanner.MAP_TYPE;
import static io.smallrye.asyncapi.core.runtime.scanner.AsyncApiDataObjectScanner.OBJECT_TYPE;
import static io.smallrye.asyncapi.core.runtime.scanner.AsyncApiDataObjectScanner.SET_TYPE;
import static io.smallrye.asyncapi.core.runtime.scanner.AsyncApiDataObjectScanner.STRING_TYPE;
import static io.smallrye.asyncapi.core.runtime.util.TypeUtil.isTerminalType;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ArrayType;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.Type;

import io.smallrye.asyncapi.core.api.models.schema.SchemaImpl;
import io.smallrye.asyncapi.core.api.util.MergeUtil;
import io.smallrye.asyncapi.core.runtime.io.schema.SchemaFactory;
import io.smallrye.asyncapi.core.runtime.scanner.SchemaRegistry;
import io.smallrye.asyncapi.core.runtime.scanner.spi.AnnotationScannerContext;
import io.smallrye.asyncapi.core.runtime.util.TypeUtil;
import io.smallrye.asyncapi.spec.annotations.schema.SchemaType;
import io.smallrye.asyncapi.spec.models.schema.Schema;

/**
 * Process {@link Type} instances.
 */
public class TypeProcessor {

    private final Schema schema;

    private final AnnotationScannerContext context;

    private final AugmentedIndexView index;

    private final AnnotationTarget annotationTarget;

    private final DataObjectDeque objectStack;

    private final TypeResolver typeResolver;

    private final DataObjectDeque.PathEntry parentPathEntry;

    // Type may be changed.
    private Type type;

    public TypeProcessor(final AnnotationScannerContext context, DataObjectDeque objectStack,
            DataObjectDeque.PathEntry parentPathEntry,
            TypeResolver typeResolver, Type type, Schema schema, AnnotationTarget annotationTarget) {
        this.objectStack = objectStack;
        this.typeResolver = typeResolver;
        this.parentPathEntry = parentPathEntry;
        this.type = type;
        this.schema = schema;
        this.context = context;
        this.index = context.getAugmentedIndex();
        this.annotationTarget = annotationTarget;
    }

    public Schema getSchema() {
        return schema;
    }

    public Type processType() {
        // If it's a terminal type.
        if (isTerminalType(type)) {
            SchemaRegistry.checkRegistration(type, typeResolver, schema);
            return type;
        }

        if (type.kind() == Type.Kind.WILDCARD_TYPE) {
            type = TypeUtil.resolveWildcard(type.asWildcardType());
        }

        if (type.kind() == Type.Kind.TYPE_VARIABLE || type.kind() == Type.Kind.UNRESOLVED_TYPE_VARIABLE) {
            // Resolve type variable to real variable.
            type = resolveTypeVariable(schema, type, false);
        }

        if (type.kind() == Type.Kind.ARRAY) {
            DataObjectLogging.logger.processingArray(type);
            ArrayType arrayType = type.asArrayType();

            // Array-type schema
            Schema arrSchema = new SchemaImpl();
            schema.type(SchemaType.ARRAY);

            // Only use component (excludes the special name formatting for arrays).
            TypeUtil.applyTypeAttributes(arrayType.component(), arrSchema);

            // If it's not a terminal type, then push for later inspection.
            if (!isTerminalType(arrayType.component()) && index.containsClass(type)) {
                pushToStack(type, arrSchema);
            }

            arrSchema = SchemaRegistry.registerReference(arrayType.component(), typeResolver, arrSchema);

            while (arrayType.dimensions() > 1) {
                Schema parentArrSchema = new SchemaImpl();
                parentArrSchema.setType(SchemaType.ARRAY);
                parentArrSchema.items(arrSchema);

                arrSchema = parentArrSchema;
                arrayType = ArrayType.create(arrayType.component(), arrayType.dimensions() - 1);
            }

            schema.items(arrSchema);

            return arrayType;
        }

        if (TypeUtil.isOptional(type)) {
            Type optType = TypeUtil.getOptionalType(type);
            if (!isTerminalType(optType) && index.containsClass(optType)) {
                pushToStack(optType);
            }
            return optType;
        }

        if (isA(type, ENUM_TYPE) && index.containsClass(type)) {
            MergeUtil.mergeObjects(schema, SchemaFactory.enumToSchema(context, type));
            pushToStack(type);
            return STRING_TYPE;
        }

        if (type.kind() == Type.Kind.PARAMETERIZED_TYPE) {
            // Parameterized type (e.g. Foo<A, B>)
            return readParameterizedType(type.asParameterizedType());
        }

        // Raw Collection
        if (isA(type, COLLECTION_TYPE)) {
            return ARRAY_TYPE_OBJECT;
        }

        // Raw Iterable
        if (isA(type, ITERABLE_TYPE)) {
            return ARRAY_TYPE_OBJECT;
        }

        // Raw Map
        if (isA(type, MAP_TYPE)) {
            return OBJECT_TYPE;
        }

        // Simple case: bare class or primitive type.
        if (index.containsClass(type)) {
            pushToStack(type);
        } else {
            // If the type is not in Jandex then we don't have easy access to it.
            // Future work could consider separate code to traverse classes reachable from this classloader.
            DataObjectLogging.logger.typeNotInJandexIndex(type);
        }

        return type;
    }

    private Type readParameterizedType(ParameterizedType pType) {
        DataObjectLogging.logger.processingParametrizedType(pType);
        Type typeRead = pType;

        // If it's a collection, we should treat it as an array.
        if (isA(pType, COLLECTION_TYPE) || isA(pType, ITERABLE_TYPE)) {
            DataObjectLogging.logger.processingTypeAs("Java Collection", "Array");
            Schema arraySchema = new SchemaImpl();
            schema.type(SchemaType.ARRAY);

            if (TypeUtil.isA(context, pType, SET_TYPE)) {
                schema.setUniqueItems(Boolean.TRUE);
            }

            // Should only have one arg for collection.
            Type arg = pType.arguments()
                    .get(0);

            if (isTerminalType(arg)) {
                TypeUtil.applyTypeAttributes(arg, arraySchema);
            } else {
                arraySchema = resolveParameterizedType(arg, arraySchema);
            }

            schema.items(arraySchema);

            typeRead = ARRAY_TYPE_OBJECT; // Representing collection as JSON array
        } else if (isA(pType, MAP_TYPE)) {
            DataObjectLogging.logger.processingTypeAs("Map", "object");
            schema.type(SchemaType.OBJECT);

            if (pType.arguments()
                    .size() == 2) {
                Type valueType = pType.arguments()
                        .get(1);
                Schema propsSchema = new SchemaImpl();
                if (isTerminalType(valueType)) {
                    TypeUtil.applyTypeAttributes(valueType, propsSchema);
                } else {
                    propsSchema = resolveParameterizedType(valueType, propsSchema);
                }
                // Add properties schema to field schema.
                schema.additionalPropertiesSchema(propsSchema);
            }
            typeRead = OBJECT_TYPE;
        } else if (index.containsClass(type)) {
            // This type will be resolved later, if necessary.
            pushToStack(pType);
        }

        return typeRead;
    }

    private Schema resolveParameterizedType(Type valueType, Schema propsSchema) {
        if (valueType.kind() == Type.Kind.TYPE_VARIABLE || valueType.kind() == Type.Kind.UNRESOLVED_TYPE_VARIABLE
                || valueType.kind() == Type.Kind.WILDCARD_TYPE) {
            Type resolved = resolveTypeVariable(propsSchema, valueType, true);
            if (index.containsClass(resolved)) {
                propsSchema.type(SchemaType.OBJECT);
                propsSchema = SchemaRegistry.registerReference(valueType, typeResolver, propsSchema);
            }
        } else if (index.containsClass(valueType)) {
            if (isA(valueType, ENUM_TYPE)) {
                DataObjectLogging.logger.processingEnum(type);
                propsSchema = SchemaFactory.enumToSchema(context, valueType);
                pushToStack(valueType);
            } else {
                propsSchema.type(SchemaType.OBJECT);
                pushToStack(valueType, propsSchema);
            }

            propsSchema = SchemaRegistry.registerReference(valueType, typeResolver, propsSchema);
        }

        return propsSchema;
    }

    private Type resolveTypeVariable(Schema schema, Type fieldType, boolean pushToStack) {
        // Type variable (e.g. A in Foo<A>)
        Type resolvedType = typeResolver.getResolvedType(fieldType);
        DataObjectLogging.logger.resolvedType(fieldType, resolvedType);

        if (isTerminalType(resolvedType) || !index.containsClass(resolvedType)) {
            DataObjectLogging.logger.terminalType(resolvedType);
            TypeUtil.applyTypeAttributes(resolvedType, schema);
        } else if (pushToStack) {
            // Add resolved type to stack.
            objectStack.push(annotationTarget, parentPathEntry, resolvedType, schema);
        }

        return resolvedType;
    }

    private void pushToStack(Type fieldType) {
        objectStack.push(annotationTarget, parentPathEntry, fieldType, schema);
    }

    private void pushToStack(Type resolvedType, Schema schema) {
        objectStack.push(annotationTarget, parentPathEntry, resolvedType, schema);
    }

    private boolean isA(Type testSubject, Type test) {
        return TypeUtil.isA(context, testSubject, test);
    }
}
