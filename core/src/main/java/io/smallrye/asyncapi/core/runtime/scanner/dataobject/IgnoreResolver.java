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

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationTarget.Kind;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

import io.smallrye.asyncapi.core.api.constants.JacksonConstants;
import io.smallrye.asyncapi.core.api.constants.JsonbConstants;
import io.smallrye.asyncapi.core.runtime.util.TypeUtil;

public class IgnoreResolver {

    private final AugmentedIndexView index;

    private final IgnoreAnnotationHandler[] ignoreHandlers;

    public IgnoreResolver(AugmentedIndexView index) {
        this.index = index;
        this.ignoreHandlers = new IgnoreAnnotationHandler[] { new JsonbTransientHandler(), new JsonIgnorePropertiesHandler(),
                new JsonIgnoreHandler(), new JsonIgnoreTypeHandler(), new TransientIgnoreHandler() };
    }

    public enum Visibility {
        IGNORED,
        EXPOSED,
        UNSET
    }

    private final class JsonbTransientHandler implements IgnoreAnnotationHandler {
        @Override
        public Visibility shouldIgnore(AnnotationTarget target, AnnotationTarget reference) {
            return TypeUtil.hasAnnotation(target, getName()) ? Visibility.IGNORED : Visibility.UNSET;
        }

        @Override
        public DotName getName() {
            return JsonbConstants.JSONB_TRANSIENT;
        }
    }

    /**
     * Handler for Jackson's {@link com.fasterxml.jackson.annotation.JsonIgnoreProperties JsonIgnoreProperties}
     */
    private final class JsonIgnorePropertiesHandler implements IgnoreAnnotationHandler {

        @Override
        public Visibility shouldIgnore(AnnotationTarget target, AnnotationTarget reference) {
            Visibility visibility = declaringClassIgnore(target);

            if (visibility != Visibility.UNSET) {
                return visibility;
            }

            return nestingPropertyIgnore(reference, propertyName(target));
        }

        /**
         * Declaring class ignore
         *
         * <pre>
         * <code>
         *  &#64;JsonIgnoreProperties("ignoreMe")
         *  class A {
         *    String ignoreMe;
         *  }
         * </code>
         * </pre>
         *
         * @param target
         * @return
         */
        private Visibility declaringClassIgnore(AnnotationTarget target) {
            AnnotationInstance declaringClassJIP = TypeUtil.getAnnotation(TypeUtil.getDeclaringClass(target), getName());
            return shouldIgnoreTarget(declaringClassJIP, propertyName(target));
        }

        /**
         * Look for nested/enclosing type @com.fasterxml.jackson.annotation.JsonIgnoreProperties.
         *
         * <pre>
         * <code>
         * class A {
         *   &#64;com.fasterxml.jackson.annotation.JsonIgnoreProperties("ignoreMe")
         *   B foo;
         * }
         *
         * class B {
         *   String ignoreMe; // Ignored during scan via A.
         *   String doNotIgnoreMe;
         * }
         * </code>
         * </pre>
         *
         * @param nesting
         * @param propertyName
         * @return
         */
        private Visibility nestingPropertyIgnore(AnnotationTarget nesting, String propertyName) {
            if (nesting == null) {
                return Visibility.UNSET;
            }
            AnnotationInstance nestedTypeJIP = TypeUtil.getAnnotation(nesting, getName());
            return shouldIgnoreTarget(nestedTypeJIP, propertyName);
        }

        private String propertyName(AnnotationTarget target) {
            if (target.kind() == Kind.FIELD) {
                return target.asField()
                        .name();
            }
            // Assuming this is a getter or setter
            String name = target.asMethod()
                    .name()
                    .substring(3);
            return Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }

        private Visibility shouldIgnoreTarget(AnnotationInstance jipAnnotation, String targetName) {
            if (jipAnnotation == null || jipAnnotation.value() == null) {
                return Visibility.UNSET;
            }

            if (Arrays.asList(jipAnnotation.value()
                    .asStringArray())
                    .contains(targetName)) {
                return Visibility.IGNORED;
            } else {
                return Visibility.EXPOSED;
            }
        }

        @Override
        public DotName getName() {
            return JacksonConstants.JSON_IGNORE_PROPERTIES;
        }
    }

    /**
     * Handler for Jackson's @{@link com.fasterxml.jackson.annotation.JsonIgnore JsonIgnore}
     */
    private final class JsonIgnoreHandler implements IgnoreAnnotationHandler {

        @Override
        public Visibility shouldIgnore(AnnotationTarget target, AnnotationTarget reference) {
            AnnotationInstance annotationInstance = TypeUtil.getAnnotation(target, getName());
            if (annotationInstance != null && valueAsBooleanOrTrue(annotationInstance)) {
                return Visibility.IGNORED;
            }
            return Visibility.UNSET;
        }

        @Override
        public DotName getName() {
            return JacksonConstants.JSON_IGNORE;
        }
    }

    /**
     * Handler for <code>com.fasterxml.jackson.annotation.JsonIgnoreType</code>
     */
    private final class JsonIgnoreTypeHandler implements IgnoreAnnotationHandler {
        private final Set<DotName> ignoredTypes = new LinkedHashSet<>();

        @Override
        public Visibility shouldIgnore(AnnotationTarget target, AnnotationTarget reference) {
            Type classType;

            switch (target.kind()) {
                case FIELD:
                    classType = target.asField()
                            .type();
                    break;
                case METHOD:
                    MethodInfo method = target.asMethod();
                    if (method.returnType()
                            .kind()
                            .equals(Type.Kind.VOID)) {
                        if (method.parameters()
                                .isEmpty()) {
                            // Constructor or other method without type information
                            return Visibility.IGNORED;
                        } else {
                            // Setter method
                            classType = method.parameters()
                                    .get(0);
                        }
                    } else {
                        // Getter method
                        classType = method.returnType();
                    }
                    break;
                default:
                    return Visibility.UNSET;
            }

            // Primitive and non-indexed types will result in a null
            if (classType.kind() == Type.Kind.PRIMITIVE || classType.kind() == Type.Kind.VOID
                    || (classType.kind() == Type.Kind.ARRAY &&
                            classType.asArrayType()
                                    .component()
                                    .kind() == Type.Kind.PRIMITIVE)
                    || !index.containsClass(classType)) {
                return Visibility.UNSET;
            }

            // Find the real class implementation where the @JsonIgnoreType annotation may be.
            ClassInfo classInfo = index.getClass(classType);

            if (ignoredTypes.contains(classInfo.name())) {
                DataObjectLogging.logger.ignoringType(classInfo.name());
                return Visibility.IGNORED;
            }

            AnnotationInstance annotationInstance = TypeUtil.getAnnotation(classInfo, getName());
            if (annotationInstance != null && valueAsBooleanOrTrue(annotationInstance)) {
                // Add the ignored field or class name
                DataObjectLogging.logger.ignoringTypeAndAddingToSet(classInfo.name());
                ignoredTypes.add(classInfo.name());
                return Visibility.IGNORED;
            }
            return Visibility.UNSET;
        }

        @Override
        public DotName getName() {
            return JacksonConstants.JSON_IGNORE_TYPE;
        }
    }

    private final class TransientIgnoreHandler implements IgnoreAnnotationHandler {
        @Override
        public Visibility shouldIgnore(AnnotationTarget target, AnnotationTarget reference) {
            if (target.kind() == Kind.FIELD) {
                FieldInfo field = target.asField();
                // If field has transient modifier, e.g. `transient String foo;`, then hide it.
                if (Modifier.isTransient(field.flags())) {
                    return Visibility.IGNORED;
                }
            }
            return Visibility.UNSET;
        }

        @Override
        public DotName getName() {
            return DotName.createSimple(TransientIgnoreHandler.class.getName());
        }
    }

    private boolean valueAsBooleanOrTrue(AnnotationInstance annotation) {
        return Optional.ofNullable(annotation.value())
                .map(AnnotationValue::asBoolean)
                .orElse(true);
    }

    private interface IgnoreAnnotationHandler {
        Visibility shouldIgnore(AnnotationTarget target, AnnotationTarget reference);

        DotName getName();
    }

    public Visibility isIgnore(AnnotationTarget annotationTarget, AnnotationTarget reference) {
        for (IgnoreAnnotationHandler handler : ignoreHandlers) {
            Visibility v = handler.shouldIgnore(annotationTarget, reference);

            if (v != Visibility.UNSET) {
                return v;
            }
        }
        return Visibility.UNSET;
    }

}
