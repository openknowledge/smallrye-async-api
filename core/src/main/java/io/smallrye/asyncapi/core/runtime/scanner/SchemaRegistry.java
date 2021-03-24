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
package io.smallrye.asyncapi.core.runtime.scanner;

import static io.smallrye.asyncapi.core.runtime.util.TypeUtil.getSchemaAnnotation;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeVariable;
import org.jboss.jandex.WildcardType;

import io.smallrye.asyncapi.core.api.AsyncApiConfig;
import io.smallrye.asyncapi.core.api.constants.AsyncApiConstants;
import io.smallrye.asyncapi.core.api.models.schema.SchemaImpl;
import io.smallrye.asyncapi.core.runtime.io.AsyncApiParser;
import io.smallrye.asyncapi.core.runtime.io.schema.SchemaConstant;
import io.smallrye.asyncapi.core.runtime.scanner.dataobject.TypeResolver;
import io.smallrye.asyncapi.core.runtime.util.JandexUtil;
import io.smallrye.asyncapi.core.runtime.util.ModelUtil;
import io.smallrye.asyncapi.spec.models.AsyncAPI;
import io.smallrye.asyncapi.spec.models.Components;
import io.smallrye.asyncapi.spec.models.schema.Schema;

public class SchemaRegistry {

    // Initial value is null
    private static final ThreadLocal<SchemaRegistry> current = new ThreadLocal<>();

    /**
     * Create a new instance of a {@link SchemaRegistry} on this thread. The
     * registry returned by this method may also be obtained by subsequent calls
     * to {@link #currentInstance()}. Additional calls of this method will
     * replace the registry in the current thread context with a new instance.
     *
     * @param config current runtime configuration
     * @param aai the AsyncAPI being constructed by the scan
     * @param index indexed class information
     * @return the registry
     */
    public static SchemaRegistry newInstance(AsyncApiConfig config, AsyncAPI aai, IndexView index) {
        SchemaRegistry registry = new SchemaRegistry(config, aai, index);
        current.set(registry);
        return registry;
    }

    /**
     * Retrieve the {@link SchemaRegistry} previously created by
     * {@link SchemaRegistry#newInstance(AsyncApiConfig, AsyncAPI, IndexView)
     * newInstance} for the current thread, or <code>null</code> if none has yet
     * been created.
     *
     * @return a {@link SchemaRegistry} instance or null
     */
    public static SchemaRegistry currentInstance() {
        return current.get();
    }

    public static void remove() {
        current.remove();
    }

    /**
     * Check if the entityType is eligible for registration using the
     * typeResolver. The eligible kinds of types are
     *
     * <ul>
     * <li>{@link org.jboss.jandex.Type.Kind#CLASS CLASS}
     * <li>{@link org.jboss.jandex.Type.Kind#PARAMETERIZED_TYPE
     * PARAMETERIZED_TYPE}
     * <li>{@link org.jboss.jandex.Type.Kind#TYPE_VARIABLE TYPE_VARIABLE}
     * <li>{@link org.jboss.jandex.Type.Kind#WILDCARD_TYPE WILDCARD_TYPE}
     * </ul>
     * <p>
     * If eligible, schema references are enabled by MP Config property
     * <code>mp.asyncapi.extensions.smallrye.schema-references.enable</code>, and the
     * resolved type is available in the registry's {@link IndexView} then the
     * schema can be registered.
     * <p>
     * Only if the type has not already been registered earlier will it be
     * added.
     *
     * @param type the {@link Type} the {@link Schema} applies to
     * @param resolver a {@link TypeResolver} that will be used to resolve
     *        parameterized and wildcard types
     * @param schema {@link Schema} to add to the registry
     * @return the same schema if not eligible for registration, or a reference
     *         to the schema registered for the given Type
     */
    public static Schema checkRegistration(Type type, TypeResolver resolver, Schema schema) {
        return register(type, resolver, schema, (registry, key) -> registry.register(key, schema, null));
    }

    /**
     * Attempt to register ONLY a reference to entityType using the typeResolver.
     * The eligible kinds of types are
     *
     * <ul>
     * <li>{@link org.jboss.jandex.Type.Kind#CLASS CLASS}
     * <li>{@link org.jboss.jandex.Type.Kind#PARAMETERIZED_TYPE
     * PARAMETERIZED_TYPE}
     * <li>{@link org.jboss.jandex.Type.Kind#TYPE_VARIABLE TYPE_VARIABLE}
     * <li>{@link org.jboss.jandex.Type.Kind#WILDCARD_TYPE WILDCARD_TYPE}
     * </ul>
     * <p>
     * If eligible, schema references are enabled by MP Config property
     * <code>mp.asyncapi.extensions.smallrye.schema-references.enable</code>, and the
     * resolved type is available in the registry's {@link IndexView} then the
     * schema reference can be registered.
     * <p>
     * Only if the type has not already been registered earlier will it be
     * added.
     *
     * @param type the {@link Type} the {@link Schema} applies to
     * @param resolver a {@link TypeResolver} that will be used to resolve
     *        parameterized and wildcard types
     * @param schema {@link Schema} to add to the registry
     * @return the same schema if not eligible for registration, or a reference
     *         to the schema registered for the given Type
     */
    public static Schema registerReference(Type type, TypeResolver resolver, Schema schema) {
        return register(type, resolver, schema, (registry, key) -> registry.registerReference(key));
    }

    static Schema register(Type type, TypeResolver resolver, Schema schema,
            BiFunction<SchemaRegistry, TypeKey, Schema> registrationAction) {
        Type resolvedType;

        if (type.kind() == Type.Kind.PARAMETERIZED_TYPE) {
            resolvedType = resolver.getResolvedType(type.asParameterizedType());
        } else {
            resolvedType = resolver.getResolvedType(type);
        }

        switch (resolvedType.kind()) {
            case CLASS:
            case PARAMETERIZED_TYPE:
            case TYPE_VARIABLE:
            case WILDCARD_TYPE:
                break;
            default:
                return schema;
        }

        SchemaRegistry registry = currentInstance();

        if (registry == null) {
            return schema;
        }

        TypeKey key = new TypeKey(resolvedType);

        if (registry.hasRef(key)) {
            schema = registry.lookupRef(key);
        } else if (registry.index.getClassByName(resolvedType.name()) == null) {
            return schema;
        } else {
            schema = registrationAction.apply(registry, key);
        }

        return schema;
    }

    /**
     * Convenience method to check if the current thread's <code>SchemaRegistry</code>
     * contains a schema for the given type (which may require type resolution using resolver).
     *
     * @param type
     * @param resolver
     * @return true when schema references are enabled and the type is present in the registry, otherwise false
     */
    public static boolean hasSchema(Type type, TypeResolver resolver) {
        SchemaRegistry registry = currentInstance();

        if (registry == null) {
            return false;
        }

        Type resolvedType;

        if (resolver != null) {
            if (type.kind() == Type.Kind.PARAMETERIZED_TYPE) {
                resolvedType = resolver.getResolvedType(type.asParameterizedType());
            } else {
                resolvedType = resolver.getResolvedType(type);
            }
        } else {
            resolvedType = type;
        }

        return registry.hasSchema(resolvedType);
    }

    /**
     * Information about a single generated schema.
     */
    static class GeneratedSchemaInfo {
        public final String name;

        public final Schema schema;

        public final Schema schemaRef;

        GeneratedSchemaInfo(String name, Schema schema, Schema schemaRef) {
            this.name = name;
            this.schema = schema;
            this.schemaRef = schemaRef;
        }
    }

    private final AsyncAPI aai;

    private final IndexView index;

    private final Map<TypeKey, GeneratedSchemaInfo> registry = new LinkedHashMap<>();

    private final Set<String> names = new LinkedHashSet<>();

    private SchemaRegistry(AsyncApiConfig config, AsyncAPI aai, IndexView index) {
        this.aai = aai;
        this.index = index;

        /*
         * If anything has been added in the component scan, add the names here
         * to prevent a collision.
         */
        Components components = aai.getComponents();

        if (components != null) {
            Map<String, Schema> schemas = components.getSchemas();
            if (schemas != null) {
                this.names.addAll(schemas.keySet());
            }
        }

        config.getSchemas()
                .entrySet()
                .forEach(entry -> {
                    String className = entry.getKey();
                    String jsonSchema = entry.getValue();
                    Schema schema;

                    try {
                        schema = AsyncApiParser.parseSchema(jsonSchema);
                    } catch (Exception e) {
                        ScannerLogging.logger.errorParsingSchema(className);
                        return;
                    }

                    Type type = Type.create(DotName.createSimple(className), Type.Kind.CLASS);
                    this.register(new TypeKey(type), schema, ((SchemaImpl) schema).getName());
                    ScannerLogging.logger.configSchemaRegistered(className);
                });
    }

    /**
     * Register the provided {@link Schema} for the provided {@link Type}. If an
     * existing schema has already been registered for the type, it will be
     * replaced by the schema given in this method.
     *
     * @param entityType the type the {@link Schema} applies to
     * @param schema {@link Schema} to add to the registry
     * @return a reference to the newly registered {@link Schema}
     */
    public Schema register(Type entityType, Schema schema) {
        TypeKey key = new TypeKey(entityType);

        if (hasRef(key)) {
            // This is a replacement registration
            remove(key);
        }

        return register(key, schema, null);
    }

    private Schema registerReference(TypeKey key) {
        String name = deriveName(key, null);
        Schema schemaRef = new SchemaImpl();
        schemaRef.setRef(AsyncApiConstants.REF_PREFIX_SCHEMA + name);

        registry.put(key, new GeneratedSchemaInfo(name, null, schemaRef));
        names.add(name);

        return schemaRef;
    }

    /**
     * Derive the schema's display name and add to both the registry and the
     * AsyncAPI document's schema map, contained in components. If a type is
     * registered using a name that already exists in the registry, a sequential
     * number will be appended to the schemas display name prior to adding.
     * <p>
     * Note, this method does NOT merge schemas found during the scanning of the
     * {@link Components}
     * annotation with those found during the model scan.
     *
     * @param key a value to be used for referencing the schema in the registry
     * @param schema {@link Schema} to add to the registry
     * @return a reference to the newly registered {@link Schema}
     */
    private Schema register(TypeKey key, Schema schema, String schemaName) {
        String name = deriveName(key, schemaName);
        Schema schemaRef = new SchemaImpl();
        schemaRef.setRef(AsyncApiConstants.REF_PREFIX_SCHEMA + name);

        registry.put(key, new GeneratedSchemaInfo(name, schema, schemaRef));
        names.add(name);

        ModelUtil.components(aai)
                .addSchema(name, schema);

        return schemaRef;
    }

    String deriveName(TypeKey key, String schemaName) {
        /*
         * We cannot use the 'name' on the SchemaImpl because it may be a
         * property name rather then a schema name.
         */
        if (schemaName == null) {
            AnnotationTarget targetSchema = index.getClassByName(key.type.name());
            AnnotationInstance schemaAnnotation = targetSchema != null ? getSchemaAnnotation(targetSchema) : null;

            if (schemaAnnotation != null) {
                schemaName = JandexUtil.stringValue(schemaAnnotation, SchemaConstant.PROP_NAME);
            }
        }

        String nameBase = schemaName != null ? schemaName : key.defaultName();
        String name = nameBase;
        int idx = 1;
        while (this.names.contains(name)) {
            name = nameBase + idx++;
        }

        return name;
    }

    public Schema lookupRef(Type instanceType) {
        return lookupRef(new TypeKey(instanceType));
    }

    public boolean hasRef(Type instanceType) {
        return hasRef(new TypeKey(instanceType));
    }

    public Schema lookupSchema(Type instanceType) {
        return lookupSchema(new TypeKey(instanceType));
    }

    public boolean hasSchema(Type instanceType) {
        return hasSchema(new TypeKey(instanceType));
    }

    private Schema lookupRef(TypeKey key) {
        GeneratedSchemaInfo info = registry.get(key);

        if (info == null) {
            throw ScannerMessages.msg.notRegistered(key.type.name());
        }

        return info.schemaRef;
    }

    private Schema lookupSchema(TypeKey key) {
        GeneratedSchemaInfo info = registry.get(key);

        if (info == null) {
            throw ScannerMessages.msg.notRegistered(key.type.name());
        }

        return info.schema;
    }

    private boolean hasRef(TypeKey key) {
        return registry.containsKey(key);
    }

    private boolean hasSchema(TypeKey key) {
        return registry.containsKey(key) && registry.get(key).schema != null;
    }

    private void remove(TypeKey key) {
        GeneratedSchemaInfo info = this.registry.remove(key);
        this.names.remove(info.name);
    }

    /**
     * This class is used as the key when storing {@link Schema}s in the
     * registry. The purpose is to replicate the same behavior as the
     * {@link Type} classes <code>equals</code> and <code>hashCode</code>
     * functions, with the exception that the {@link Type}'s annotations are not
     * considered in these versions of the methods.
     */
    static class TypeKey {
        private final Type type;

        private int hashCode = 0;

        TypeKey(Type type) {
            this.type = type;
        }

        public String defaultName() {
            StringBuilder name = new StringBuilder(type.name()
                    .local());

            switch (type.kind()) {
                case PARAMETERIZED_TYPE:
                    appendParameterNames(name, type.asParameterizedType());
                    break;
                case WILDCARD_TYPE:
                    name.append(wildcardName(type.asWildcardType()));
                    break;
                default:
                    break;
            }

            return name.toString();
        }

        static void appendParameterNames(StringBuilder name, ParameterizedType type) {
            for (Type param : type.asParameterizedType()
                    .arguments()) {
                switch (param.kind()) {
                    case PARAMETERIZED_TYPE:
                        name.append(param.name()
                                .local());
                        appendParameterNames(name, param.asParameterizedType());
                        break;
                    case WILDCARD_TYPE:
                        name.append(wildcardName(param.asWildcardType()));
                        break;
                    default:
                        name.append(param.name()
                                .local());
                        break;
                }
            }
        }

        static String wildcardName(WildcardType type) {
            Type superBound = type.superBound();

            if (superBound != null) {
                return "Super" + superBound.name()
                        .local();
            } else {
                Type extendsBound = type.extendsBound();

                if (!DotName.createSimple("java.lang.Object")
                        .equals(extendsBound.name())) {
                    return "Extends" + extendsBound.name()
                            .local();
                }

                return extendsBound.name()
                        .local();
            }
        }

        /**
         * Determine if the two {@link Type}s are equal.
         *
         * @see Type#equals
         * @see ParameterizedType#equals
         * @see TypeVariable#equals
         * @see WildcardType#equals
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }

            TypeKey other = (TypeKey) o;

            if (type == other.type) {
                return true;
            }

            if (type == null || type.getClass() != other.type.getClass()) {
                return false;
            }

            if (!type.name()
                    .equals(other.type.name())) {
                return false;
            }

            if (type instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) type;
                ParameterizedType otherType = (ParameterizedType) other.type;
                Type typeOwner = paramType.owner();
                Type otherOwner = otherType.owner();

                return (typeOwner == otherOwner || (typeOwner != null && typeOwner.equals(otherOwner)))
                        && Objects.equals(paramType.arguments(), otherType.arguments());
            }

            if (type instanceof TypeVariable) {
                TypeVariable varType = (TypeVariable) type;
                TypeVariable otherType = (TypeVariable) other.type;

                String id = varType.identifier();
                String otherId = otherType.identifier();

                return id.equals(otherId) && Objects.equals(varType.bounds(), otherType.bounds());
            }

            if (type instanceof WildcardType) {
                WildcardType wildType = (WildcardType) type;
                WildcardType otherType = (WildcardType) other.type;

                return Objects.equals(wildType.extendsBound(), otherType.extendsBound())
                        && Objects.equals(wildType.superBound(), otherType.superBound());
            }

            return true;
        }

        /**
         * @see Type#equals
         * @see ParameterizedType#equals
         * @see TypeVariable#equals
         * @see WildcardType#equals
         */
        @Override
        public int hashCode() {
            int hash = this.hashCode;

            if (hash != 0) {
                return hash;
            }

            hash = type.name()
                    .hashCode();

            if (type instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) type;
                Type owner = paramType.owner();
                hash = 31 * hash + Objects.hashCode(paramType.arguments());
                hash = 31 * hash + (owner != null ? owner.hashCode() : 0);
            }

            if (type instanceof TypeVariable) {
                TypeVariable varType = (TypeVariable) type;
                hash = 31 * hash + varType.identifier()
                        .hashCode();
                hash = 31 * hash + Objects.hashCode(varType.bounds());
            }

            if (type instanceof WildcardType) {
                WildcardType wildType = (WildcardType) type;
                hash = 31 * hash + Objects.hash(wildType.extendsBound(), wildType.superBound());
            }

            this.hashCode = hash;
            return hash;
        }
    }

}
