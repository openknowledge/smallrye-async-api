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
package io.smallrye.asyncapi.core.runtime.scanner.dataobject;

import static org.jboss.jandex.DotName.createComponentized;

import java.math.BigDecimal;
import java.util.Optional;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

import io.smallrye.asyncapi.core.runtime.util.JandexUtil;
import io.smallrye.asyncapi.core.runtime.util.TypeUtil;
import io.smallrye.asyncapi.spec.annotations.schema.SchemaType;
import io.smallrye.asyncapi.spec.models.schema.Schema;

/**
 * @author Michael Edgar {@literal <michael@xlate.io>}
 */
public class BeanValidationScanner {

    public interface RequirementHandler {
        void setRequired(AnnotationTarget target, String propertyKey);
    }

    static final BeanValidationScanner INSTANCE = new BeanValidationScanner();

    static final BigDecimal NEGATIVE_ONE = BigDecimal.ZERO.subtract(BigDecimal.ONE);

    static final DotName BV_JAVAX = createComponentized(null, "javax");

    static final DotName BV_BASE = createComponentized(BV_JAVAX, "validation");

    static final DotName BV_GROUPS = createComponentized(BV_BASE, "groups");

    static final DotName BV_DEFAULT_GROUP = createComponentized(BV_GROUPS, "Default");

    static final DotName BV_CONTRAINTS = createComponentized(BV_BASE, "constraints");

    static final DotName BV_DECIMAL_MAX = createComponentized(BV_CONTRAINTS, "DecimalMax");

    static final DotName BV_DECIMAL_MIN = createComponentized(BV_CONTRAINTS, "DecimalMin");

    static final DotName BV_DIGITS = createComponentized(BV_CONTRAINTS, "Digits");

    static final DotName BV_MAX = createComponentized(BV_CONTRAINTS, "Max");

    static final DotName BV_MIN = createComponentized(BV_CONTRAINTS, "Min");

    static final DotName BV_NEGATIVE = createComponentized(BV_CONTRAINTS, "Negative");

    static final DotName BV_NEGATIVE_OR_ZERO = createComponentized(BV_CONTRAINTS, "NegativeOrZero");

    static final DotName BV_NOT_BLANK = createComponentized(BV_CONTRAINTS, "NotBlank");

    static final DotName BV_NOT_EMPTY = createComponentized(BV_CONTRAINTS, "NotEmpty");

    static final DotName BV_NOT_NULL = createComponentized(BV_CONTRAINTS, "NotNull");

    static final DotName BV_POSITIVE = createComponentized(BV_CONTRAINTS, "Positive");

    static final DotName BV_POSITIVE_OR_ZERO = createComponentized(BV_CONTRAINTS, "PositiveOrZero");

    static final DotName BV_SIZE = createComponentized(BV_CONTRAINTS, "Size");

    /**
     * Determine if any Java Bean Validation constraint annotations are present
     * on the {@link AnnotationTarget} that are applicable to the schema. This
     * method will apply the constraints to the schema only if no value has
     * previously been set.
     * <p>
     * If the schema's type attribute has not been previously set or the schema
     * contains a reference, this method will not apply any changes to the
     * schema.
     * <p>
     * Each of the constraints (defined in javax.validation.constraints) will
     * apply to the schema based on the schema's type.
     * <p>
     * When a bean validation @NotNull constraint
     * applies to the schema, the provided {@link RequirementHandler} will be
     * called in order for the component calling this method to determine if and
     * how to apply the requirement. E.g. a required Schema is communicated
     * differently for a parent schema and for a parameter described by the
     * schema.
     *
     * @param target the object from which to retrieve the constraint annotations
     * @param schema the schema to which the constraints will be applied
     * @param propertyKey the name of the property in parentSchema that refers to the
     *        schema
     * @param handler the handler to be called when a
     *        bean validation @NotNull constraint is encountered.
     */
    public static void applyConstraints(AnnotationTarget target, Schema schema, String propertyKey,
            RequirementHandler handler) {

        SchemaType schemaType = schema.getType();

        /*
         * The type be set. Attributes set in this function are not application
         * to $ref type schemas.
         */
        if (schemaType == null || schema.getRef() != null) {
            return;
        }

        switch (schemaType) {
            case ARRAY:
                applyArrayConstraints(target, schema, propertyKey, handler);
                break;
            case BOOLEAN:
                INSTANCE.notNull(target, schema, propertyKey, handler);
                break;
            case INTEGER:
                applyNumberConstraints(target, schema, propertyKey, handler);
                break;
            case NUMBER:
                applyNumberConstraints(target, schema, propertyKey, handler);
                break;
            case OBJECT:
                applyObjectConstraints(target, schema, propertyKey, handler);
                break;
            case STRING:
                applyStringConstraints(target, schema, propertyKey, handler);
                break;
        }
    }

    private static void applyStringConstraints(AnnotationTarget target, Schema schema, String propertyKey,
            RequirementHandler handler) {
        INSTANCE.decimalMax(target, schema);
        INSTANCE.decimalMin(target, schema);
        INSTANCE.digits(target, schema);
        INSTANCE.notBlank(target, schema);
        INSTANCE.notNull(target, schema, propertyKey, handler);
        INSTANCE.sizeString(target, schema);
        INSTANCE.notEmptyString(target, schema);
    }

    private static void applyObjectConstraints(AnnotationTarget target, Schema schema, String propertyKey,
            RequirementHandler handler) {
        INSTANCE.notNull(target, schema, propertyKey, handler);
        INSTANCE.sizeObject(target, schema);
        INSTANCE.notEmptyObject(target, schema);
    }

    private static void applyArrayConstraints(AnnotationTarget target, Schema schema, String propertyKey,
            RequirementHandler handler) {
        INSTANCE.notNull(target, schema, propertyKey, handler);
        INSTANCE.sizeArray(target, schema);
        INSTANCE.notEmptyArray(target, schema);
    }

    private static void applyNumberConstraints(AnnotationTarget target, Schema schema, String propertyKey,
            RequirementHandler handler) {
        INSTANCE.decimalMax(target, schema);
        INSTANCE.decimalMin(target, schema);
        INSTANCE.digits(target, schema);
        INSTANCE.max(target, schema);
        INSTANCE.min(target, schema);
        INSTANCE.negative(target, schema);
        INSTANCE.negativeOrZero(target, schema);
        INSTANCE.notNull(target, schema, propertyKey, handler);
        INSTANCE.positive(target, schema);
        INSTANCE.positiveOrZero(target, schema);
    }

    void decimalMax(AnnotationTarget target, Schema schema) {
        AnnotationInstance constraint = getConstraint(target, BV_DECIMAL_MAX);

        if (constraint != null && schema.getMaximum() == null) {
            String decimalValue = JandexUtil.stringValue(constraint, VALUE);
            try {
                BigDecimal decimal = new BigDecimal(decimalValue);
                schema.setMaximum(decimal);

                Optional<Boolean> inclusive = JandexUtil.booleanValue(constraint, INCLUSIVE);

                if (schema.getExclusiveMaximum() == null && inclusive.isPresent() && !inclusive.get()
                        .booleanValue()) {
                    schema.setExclusiveMaximum(Boolean.TRUE);
                }
            } catch (@SuppressWarnings("unused") NumberFormatException e) {
                DataObjectLogging.logger.invalidAnnotationFormat(decimalValue);
            }
        }
    }

    void decimalMin(AnnotationTarget target, Schema schema) {
        AnnotationInstance constraint = getConstraint(target, BV_DECIMAL_MIN);

        if (constraint != null && schema.getMinimum() == null) {
            String decimalValue = JandexUtil.stringValue(constraint, VALUE);
            try {
                BigDecimal decimal = new BigDecimal(decimalValue);
                schema.setMinimum(decimal);
                Optional<Boolean> inclusive = JandexUtil.booleanValue(constraint, INCLUSIVE);

                if (schema.getExclusiveMinimum() == null && inclusive.isPresent() && !inclusive.get()
                        .booleanValue()) {
                    schema.setExclusiveMinimum(Boolean.TRUE);
                }
            } catch (@SuppressWarnings("unused") NumberFormatException e) {
                DataObjectLogging.logger.invalidAnnotationFormat(decimalValue);
            }
        }

    }

    void digits(AnnotationTarget target, Schema schema) {
        AnnotationInstance constraint = getConstraint(target, BV_DIGITS);

        if (constraint != null && schema.getPattern() == null) {
            // Both attributes are required - safe to use primitives.
            final int integerPart = JandexUtil.intValue(constraint, "integer");
            final int fractionPart = JandexUtil.intValue(constraint, "fraction");
            final StringBuilder pattern = new StringBuilder(50);

            pattern.append('^');

            if (integerPart > 0) {
                pattern.append("\\d");

                if (integerPart > 1) {
                    pattern.append("{1,")
                            .append(integerPart)
                            .append('}');
                }
            }

            if (fractionPart > 0) {
                pattern.append("([.]\\d");

                if (fractionPart > 1) {
                    pattern.append("{1,")
                            .append(fractionPart)
                            .append("}");
                }

                pattern.append(")?");
            }

            pattern.append('$');
            schema.setPattern(pattern.toString());
        }
    }

    void max(AnnotationTarget target, Schema schema) {
        AnnotationInstance constraint = getConstraint(target, BV_MAX);

        if (constraint != null && schema.getMaximum() == null) {
            AnnotationValue value = constraint.value(VALUE);
            schema.setMaximum(new BigDecimal(value.asLong()));
        }
    }

    void min(AnnotationTarget target, Schema schema) {
        AnnotationInstance constraint = getConstraint(target, BV_MIN);

        if (constraint != null && schema.getMinimum() == null) {
            AnnotationValue value = constraint.value(VALUE);
            schema.setMinimum(new BigDecimal(value.asLong()));
        }
    }

    void negative(AnnotationTarget target, Schema schema) {
        AnnotationInstance constraint = getConstraint(target, BV_NEGATIVE);

        if (constraint != null && schema.getMaximum() == null) {
            Boolean exclusive = schema.getExclusiveMaximum();

            if (exclusive != null && exclusive) {
                schema.setMaximum(BigDecimal.ZERO);
            } else {
                schema.setMaximum(NEGATIVE_ONE);
            }
        }
    }

    void negativeOrZero(AnnotationTarget target, Schema schema) {
        AnnotationInstance constraint = getConstraint(target, BV_NEGATIVE_OR_ZERO);

        if (constraint != null && schema.getMaximum() == null) {
            Boolean exclusive = schema.getExclusiveMaximum();

            if (exclusive != null && exclusive) {
                schema.setMaximum(BigDecimal.ONE);
            } else {
                schema.setMaximum(BigDecimal.ZERO);
            }
        }
    }

    void notBlank(AnnotationTarget target, Schema schema) {
        AnnotationInstance constraint = getConstraint(target, BV_NOT_BLANK);

        if (constraint != null) {
            if (schema.getPattern() == null) {
                schema.setPattern("\\S");
            }
        }
    }

    void notEmptyArray(AnnotationTarget target, Schema schema) {
        AnnotationInstance constraint = getConstraint(target, BV_NOT_EMPTY);

        if (constraint != null && schema.getMinItems() == null) {
            schema.setMinItems(1);
        }
    }

    void notEmptyObject(AnnotationTarget target, Schema schema) {
        if (!allowsAdditionalProperties(schema)) {
            return;
        }

        AnnotationInstance constraint = getConstraint(target, BV_NOT_EMPTY);

        if (constraint != null && schema.getMinProperties() == null) {
            schema.setMinProperties(1);
        }
    }

    void notEmptyString(AnnotationTarget target, Schema schema) {
        AnnotationInstance constraint = getConstraint(target, BV_NOT_EMPTY);

        if (constraint != null) {
            if (schema.getMinLength() == null) {
                schema.setMinLength(1);
            }
        }
    }

    void notNull(AnnotationTarget target, Schema schema, String propertyKey, RequirementHandler handler) {
        AnnotationInstance constraint = getConstraint(target, BV_NOT_NULL);

        if (constraint != null) {
            if (handler != null && propertyKey != null) {
                handler.setRequired(target, propertyKey);
            }
        }
    }

    void positive(AnnotationTarget target, Schema schema) {
        AnnotationInstance constraint = getConstraint(target, BV_POSITIVE);

        if (constraint != null && schema.getMinimum() == null) {
            Boolean exclusive = schema.getExclusiveMinimum();

            if (exclusive != null && exclusive) {
                schema.setMinimum(BigDecimal.ZERO);
            } else {
                schema.setMinimum(BigDecimal.ONE);
            }
        }
    }

    void positiveOrZero(AnnotationTarget target, Schema schema) {
        AnnotationInstance constraint = getConstraint(target, BV_POSITIVE_OR_ZERO);

        if (constraint != null && schema.getMinimum() == null) {
            Boolean exclusive = schema.getExclusiveMinimum();

            if (exclusive != null && exclusive) {
                schema.setMinimum(NEGATIVE_ONE);
            } else {
                schema.setMinimum(BigDecimal.ZERO);
            }
        }
    }

    void sizeArray(AnnotationTarget target, Schema schema) {
        AnnotationInstance constraint = getConstraint(target, BV_SIZE);

        if (constraint != null) {
            Integer min = JandexUtil.intValue(constraint, "min");
            Integer max = JandexUtil.intValue(constraint, "max");

            if (min != null && schema.getMinItems() == null) {
                schema.setMinItems(min);
            }

            if (max != null && schema.getMaxItems() == null) {
                schema.setMaxItems(max);
            }
        }
    }

    void sizeObject(AnnotationTarget target, Schema schema) {
        if (!allowsAdditionalProperties(schema)) {
            return;
        }

        AnnotationInstance constraint = getConstraint(target, BV_SIZE);

        if (constraint != null) {
            Integer min = JandexUtil.intValue(constraint, "min");
            Integer max = JandexUtil.intValue(constraint, "max");

            if (min != null && schema.getMinProperties() == null) {
                schema.setMinProperties(min);
            }

            if (max != null && schema.getMaxProperties() == null) {
                schema.setMaxProperties(max);
            }
        }
    }

    void sizeString(AnnotationTarget target, Schema schema) {
        AnnotationInstance constraint = getConstraint(target, BV_SIZE);

        if (constraint != null) {
            Integer min = JandexUtil.intValue(constraint, "min");
            Integer max = JandexUtil.intValue(constraint, "max");

            if (min != null && schema.getMinLength() == null) {
                schema.setMinLength(min);
            }

            if (max != null && schema.getMaxLength() == null) {
                schema.setMaxLength(max);
            }
        }
    }

    boolean allowsAdditionalProperties(Schema schema) {
        Boolean additionalProperties = schema.getAdditionalPropertiesBoolean();

        if (additionalProperties != null) {
            return additionalProperties;
        }

        return schema.getAdditionalPropertiesSchema() != null;
    }

    /**
     * Retrieves a constraint {@link AnnotationInstance} from the current
     * target. If the annotation is found and applies to multiple bean
     * validation groups or to a single group other than the Default,
     * returns null.
     *
     * @param target the object from which to retrieve the constraint annotation
     * @param annotationName name of the annotation
     * @return the first occurrence of the named constraint if no groups or only
     *         the Default group is specified, or null
     */
    AnnotationInstance getConstraint(AnnotationTarget target, DotName annotationName) {
        AnnotationInstance constraint = TypeUtil.getAnnotation(target, annotationName);

        if (constraint != null) {
            AnnotationValue groupValue = constraint.value("groups");

            if (groupValue == null) {
                return constraint;
            }

            Type[] groups = groupValue.asClassArray();

            switch (groups.length) {
                case 0:
                    return constraint;
                case 1:
                    if (groups[0].name()
                            .equals(BV_DEFAULT_GROUP)) {
                        return constraint;
                    }
                    break;
                default:
                    break;
            }
        }

        return null;
    }

    private static final String VALUE = "value";

    private static final String INCLUSIVE = "inclusive";
}
