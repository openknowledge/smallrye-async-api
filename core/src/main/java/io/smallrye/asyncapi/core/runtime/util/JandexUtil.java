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
package io.smallrye.asyncapi.core.runtime.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Type;

import io.smallrye.asyncapi.core.api.constants.AsyncApiConstants;
import io.smallrye.asyncapi.core.runtime.io.schema.SchemaConstant;
import io.smallrye.asyncapi.spec.annotations.schema.SchemaType;
import io.smallrye.asyncapi.spec.models.schema.Schema;

public class JandexUtil {

    private static final Pattern COMPONENT_KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9\\.\\-_]+$");

    /**
     * Simple enum to indicate the type of a $ref being read/written.
     */
    public enum RefType {
        PARAMETER("parameter"),
        SCHEMA("schemas"),
        SCHEMA_PROPERTY("schemaProperty"),
        MESSAGE("message"),
        MESSAGE_TRAIT("messageTrait"),
        OPERATION_TRAIT("operationTrait");

        String componentPath;

        RefType(String componentPath) {
            this.componentPath = componentPath;
        }
    }

    /**
     * Convenience method to retrieve the named parameter from an annotation.
     * The value will be unwrapped from its containing {@link AnnotationValue}.
     *
     * @param <T> the type of the parameter being retrieved
     * @param annotation the annotation from which to fetch the parameter
     * @param name the name of the parameter
     * @return an unwrapped annotation parameter value
     */
    @SuppressWarnings({ "unchecked", "squid:S3776" })
    public static <T> T value(AnnotationInstance annotation, String name) {
        final AnnotationValue value = annotation.value(name);

        if (value == null) {
            return null;
        }

        final boolean isArray = (AnnotationValue.Kind.ARRAY == value.kind());

        switch (isArray ? value.componentKind() : value.kind()) {
            case BOOLEAN:
                return (T) (isArray ? value.asBooleanArray() : value.asBoolean());
            case BYTE:
                return (T) (isArray ? value.asByteArray() : value.asByte());
            case CHARACTER:
                return (T) (isArray ? value.asCharArray() : value.asChar());
            case CLASS:
                return (T) (isArray ? value.asClassArray() : value.asClass());
            case DOUBLE:
                return (T) (isArray ? value.asDoubleArray() : value.asDouble());
            case ENUM:
                return (T) (isArray ? value.asEnumArray() : value.asEnum());
            case FLOAT:
                return (T) (isArray ? value.asFloatArray() : value.asFloat());
            case INTEGER:
                return (T) (isArray ? value.asIntArray() : value.asInt());
            case LONG:
                return (T) (isArray ? value.asLongArray() : value.asLong());
            case NESTED:
                return (T) (isArray ? value.asNestedArray() : value.asNested());
            case SHORT:
                return (T) (isArray ? value.asShortArray() : value.asShort());
            case STRING:
                return (T) (isArray ? value.asStringArray() : value.asString());
            case UNKNOWN:
            default:
                return null;
        }
    }

    /**
     * Reads a string property named "ref" value from the given annotation and converts it
     * to a value appropriate for setting on a model's "$ref" property.
     *
     * @param annotation AnnotationInstance
     * @param refType RefType
     * @return String value
     */
    public static String refValue(AnnotationInstance annotation, RefType refType) {
        AnnotationValue value = annotation.value(AsyncApiConstants.REF);
        if (value == null) {
            return null;
        }

        String ref = value.asString();

        if (!COMPONENT_KEY_PATTERN.matcher(ref)
                .matches()) {
            return ref;
        }

        if (refType != null) {
            ref = "#/components/" + refType.componentPath + "/" + ref;
        } else {
            throw UtilMessages.msg.refTypeNotNull();
        }

        return ref;
    }

    /**
     * Reads a String property value from the given annotation instance. If no value is found
     * this will return null.
     *
     * @param annotation AnnotationInstance
     * @param propertyName String
     * @return String value
     */
    public static String stringValue(AnnotationInstance annotation, String propertyName) {
        AnnotationValue value = annotation.value(propertyName);
        if (value == null) {
            return null;
        } else {
            return value.asString();
        }
    }

    /**
     * Reads a Boolean property value from the given annotation instance. If no value is found
     * this will return null.
     *
     * @param annotation AnnotationInstance
     * @param propertyName String
     * @return Boolean value
     */
    public static Optional<Boolean> booleanValue(AnnotationInstance annotation, String propertyName) {
        AnnotationValue value = annotation.value(propertyName);
        if (value != null) {
            return Optional.of(value.asBoolean());
        }
        return Optional.empty();
    }

    /**
     * Reads a Integer property value from the given annotation instance. If no value is found
     * this will return null.
     *
     * @param annotation AnnotationInstance
     * @param propertyName String
     * @return Integer value
     */
    public static Integer intValue(AnnotationInstance annotation, String propertyName) {
        AnnotationValue value = annotation.value(propertyName);
        if (value == null) {
            return null;
        } else {
            return value.asInt();
        }
    }

    /**
     * Reads a Boolean property from the given annotation instance. If no value is found
     * this will return false.
     *
     * @param annotation AnnotationInstance
     * @param propertyName String
     * @return Boolean value
     */
    public static boolean booleanValueWithDefault(AnnotationInstance annotation, String propertyName) {
        AnnotationValue value = annotation.value(propertyName);
        return value != null && value.asBoolean();
    }

    /**
     * Reads a String array property value from the given annotation instance. If no value is found
     * this will return null.
     *
     * @param annotation AnnotationInstance
     * @param propertyName String
     * @return List of Strings
     */
    public static Optional<List<String>> stringListValue(AnnotationInstance annotation, String propertyName) {
        AnnotationValue value = annotation.value(propertyName);
        if (value != null) {
            return Optional.of(new ArrayList<>(Arrays.asList(value.asStringArray())));
        }
        return Optional.empty();
    }

    /**
     * Reads a String property value from the given annotation instance. If no value is found
     * this will return null.
     *
     * @param annotation AnnotationInstance
     * @param propertyName String
     * @param clazz Class type of the Enum
     * @param <T> Type parameter
     * @return Value of property
     */
    public static <T extends Enum<?>> T enumValue(AnnotationInstance annotation, String propertyName, Class<T> clazz) {
        AnnotationValue value = annotation.value(propertyName);
        if (value == null) {
            return null;
        }
        return enumValue(value.asString(), clazz);
    }

    /**
     * Converts a string value to the given enum type. If the string does not match
     * one of the the enum's values name (case-insensitive) or toString value, null
     * will be returned.
     *
     * @param strVal String
     * @param clazz Class type of the Enum
     * @param <T> Type parameter
     * @return Value of property
     */
    public static <T extends Enum<?>> T enumValue(String strVal, Class<T> clazz) {
        T[] constants = clazz.getEnumConstants();
        for (T t : constants) {
            if (t.toString()
                    .equals(strVal)) {
                return t;
            }
        }
        for (T t : constants) {
            if (t.name()
                    .equalsIgnoreCase(strVal)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Many OAI annotations can either be found singly or as a wrapped array. This method will
     * look for both and return a list of all found. Both the single and wrapper annotation names
     * must be provided.
     *
     * @param target the annotated target (e.g. ClassInfo, MethodInfo)
     * @param singleAnnotationName DotName
     * @param repeatableAnnotationName DotName
     * @return List of AnnotationInstance's
     */
    public static List<AnnotationInstance> getRepeatableAnnotation(AnnotationTarget target, DotName singleAnnotationName,
            DotName repeatableAnnotationName) {

        List<AnnotationInstance> annotations = new ArrayList<>();

        AnnotationInstance annotation = TypeUtil.getAnnotation(target, singleAnnotationName);

        if (annotation != null) {
            annotations.add(annotation);
        }

        if (repeatableAnnotationName != null) {
            AnnotationInstance[] nestedArray = TypeUtil.getAnnotationValue(target, repeatableAnnotationName,
                    AsyncApiConstants.VALUE);

            if (nestedArray != null) {
                annotations.addAll(Arrays.asList(nestedArray));
            }
        }

        return annotations;
    }

    /**
     * Gets the name of an item from its ref. For example, the ref might be "#/components/parameters/departureDate"
     * which would result in a name of "departureDate".
     *
     * @param annotation AnnotationInstance
     * @return Name of item from ref
     */
    public static String nameFromRef(AnnotationInstance annotation) {
        String ref = annotation.value(AsyncApiConstants.REF).asString();
        return ModelUtil.nameFromRef(ref);
    }

    /**
     * Returns true if the given annotation instance is a "ref". An annotation is a ref if it has
     * a non-null value for the "ref" property.
     *
     * @param annotation AnnotationInstance
     * @return Whether it's a "ref"
     */
    public static boolean isRef(AnnotationInstance annotation) {
        return annotation.value(AsyncApiConstants.REF) != null;
    }

    /**
     * Gets a single class annotation from the given class. Returns null if no matching annotation
     * is found.
     *
     * @param ct ClassInfo
     * @param name DotName
     * @return AnnotationInstance
     */
    public static AnnotationInstance getClassAnnotation(ClassInfo ct, DotName name) {
        if (name == null) {
            return null;
        }
        Collection<AnnotationInstance> annotations = ct.classAnnotations();
        for (AnnotationInstance annotationInstance : annotations) {
            if (annotationInstance.name()
                    .equals(name)) {
                return annotationInstance;
            }
        }
        return null;
    }

    /**
     * Returns true if the given @Schema annotation is a simple class schema. This means that
     * the annotation only has one field defined, and that field is "implementation".
     *
     * @param annotation AnnotationInstance
     * @return Is it a simple class @Schema
     */
    public static boolean isSimpleClassSchema(AnnotationInstance annotation) {
        return annotation.values()
                .isEmpty() && hasImplementation(annotation);
    }

    /**
     * Returns true if the given @Schema annotation is a simple array schema. This is defined
     * as a schema with only a "type" field and "implementation" field defined *and* the type must
     * be array.
     *
     * @param annotation AnnotationInstance
     * @return Is it a simple array @Schema
     */
    public static boolean isSimpleArraySchema(AnnotationInstance annotation) {
        // May only have 'type' display property
        if (annotation.values()
                .size() != 1) {
            return false;
        }

        return isArraySchema(annotation);
    }

    /**
     * Returns true if the given {@link Schema @Schema}
     * annotation is an array schema. This is defined as a schema with a "type" field and "implementation"
     * field defined *and* the type must be array.
     *
     * @param annotation AnnotationInstance
     * @return Is it an array {@link Schema @Schema}
     */
    public static boolean isArraySchema(AnnotationInstance annotation) {
        if (!hasImplementation(annotation)) {
            return false;
        }

        SchemaType type = JandexUtil.enumValue(annotation, SchemaConstant.PROP_TYPE, SchemaType.class);

        return (type == SchemaType.ARRAY);
    }

    /**
     * Returns true if the given {@link Schema @Schema}
     * annotation has defined an "implementation" field.
     *
     * @param annotation AnnotationInstance
     * @return true if the annotation defines an implementation, otherwise false
     */
    public static boolean hasImplementation(AnnotationInstance annotation) {
        return annotation.value(SchemaConstant.PROP_IMPLEMENTATION) != null;
    }

    /**
     * Builds an insertion-order map of a class's inheritance chain, starting
     * with the klazz argument.
     *
     * @param index index for superclass retrieval
     * @param klazz the class to retrieve inheritance
     * @param type type of the klazz
     * @return map of a class's inheritance chain/ancestry
     */
    public static Map<ClassInfo, Type> inheritanceChain(IndexView index, ClassInfo klazz, Type type) {
        Map<ClassInfo, Type> chain = new LinkedHashMap<>();

        do {
            chain.put(klazz, type);
        } while ((type = klazz.superClassType()) != null && (klazz = index.getClassByName(TypeUtil.getName(type))) != null);

        return chain;
    }
}
