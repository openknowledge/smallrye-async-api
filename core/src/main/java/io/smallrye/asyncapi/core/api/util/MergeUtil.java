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
package io.smallrye.asyncapi.core.api.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jboss.logging.Logger;

import io.smallrye.asyncapi.core.api.models.ModelImpl;
import io.smallrye.asyncapi.spec.models.AsyncAPI;
import io.smallrye.asyncapi.spec.models.Constructible;
import io.smallrye.asyncapi.spec.models.Extensible;
import io.smallrye.asyncapi.spec.models.Reference;
import io.smallrye.asyncapi.spec.models.parameter.Parameter;
import io.smallrye.asyncapi.spec.models.security.SecurityRequirement;
import io.smallrye.asyncapi.spec.models.server.Server;
import io.smallrye.asyncapi.spec.models.tag.Tag;

public class MergeUtil {

    private static final Logger LOG = Logger.getLogger(MergeUtil.class);

    private static final Set<String> EXCLUDED_PROPERTIES = new HashSet<>();
    static {
        EXCLUDED_PROPERTIES.add("class");
        EXCLUDED_PROPERTIES.add("openapi");
    }

    private MergeUtil() {
    }

    /**
     * Merges documents and returns the result.
     *
     * @param document1 AsyncAPIImpl instance
     * @param document2 AsyncAPIImpl instance
     * @return Merged AsyncAPIImpl instance
     */
    public static final AsyncAPI merge(AsyncAPI document1, AsyncAPI document2) {
        return mergeObjects(document1, document2);
    }

    /**
     * Generic merge of two objects of the same type.
     *
     * @param object1 First object
     * @param object2 Second object
     * @param <T> Type parameter
     * @return Merged object
     */
    @SuppressWarnings({ "rawtypes" })
    public static <T> T mergeObjects(T object1, T object2) {
        if (object1 == null && object2 != null) {
            return object2;
        }
        if (object1 != null && object2 == null) {
            return object1;
        }
        if (object1 == null && object2 == null) {
            return null;
        }

        // It's uncommon, but in some cases (like Link Parameters or Examples) the values could
        // be different types.  In this case, just take the 2nd one (the override).
        if (!object1.getClass().equals(object2.getClass())) {
            return object2;
        }

        PropertyDescriptor[] descriptors = new PropertyDescriptor[0];
        try {
            descriptors = Introspector.getBeanInfo(object1.getClass()).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            UtilLogging.logger.failedToIntrospectBeanInfo(object1.getClass(), e);
        }

        for (PropertyDescriptor descriptor : descriptors) {
            if (EXCLUDED_PROPERTIES.contains(descriptor.getName())) {
                continue;
            }
            Class ptype = descriptor.getPropertyType();
            Method writeMethod = descriptor.getWriteMethod();
            if (writeMethod != null) {
                if (Constructible.class.isAssignableFrom(ptype)) {
                    try {
                        Object val1 = descriptor.getReadMethod().invoke(object1);
                        Object val2 = descriptor.getReadMethod().invoke(object2);
                        Object newValue = mergeObjects(val1, val2);
                        if (newValue != null) {
                            writeMethod.invoke(object1, newValue);
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                } else if (Map.class.isAssignableFrom(ptype)) {
                    try {
                        Map values1 = (Map) descriptor.getReadMethod().invoke(object1);
                        Map values2 = (Map) descriptor.getReadMethod().invoke(object2);
                        Map newValues = mergeMaps(values1, values2);
                        writeMethod.invoke(object1, newValues);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                } else if (List.class.isAssignableFrom(ptype)) {
                    try {
                        List values1 = (List) descriptor.getReadMethod().invoke(object1);
                        List values2 = (List) descriptor.getReadMethod().invoke(object2);
                        List newValues = mergeLists(values1, values2).orElse(null);
                        writeMethod.invoke(object1, newValues);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        Object newValue = descriptor.getReadMethod().invoke(object2);
                        if (newValue != null) {
                            writeMethod.invoke(object1, newValue);
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return object1;
    }

    /**
     * Merges two Maps. Any values missing from Map1 but present in Map2 will be added. If a value
     * is present in both maps, it will be overridden or merged.
     *
     * @param values1
     * @param values2
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Map mergeMaps(Map values1, Map values2) {
        if (values1 == null && values2 == null) {
            return null;
        }
        if (values1 != null && values2 == null) {
            return values1;
        }
        if (values1 == null && values2 != null) {
            return values2;
        }

        if (!(values1 instanceof ModelImpl)) {
            values1 = new LinkedHashMap<>(values1);
        }
        if (!(values2 instanceof ModelImpl)) {
            values2 = new LinkedHashMap<>(values2);
        }

        for (Object key : values2.keySet()) {
            if (values1.containsKey(key)) {
                Object pval1 = values1.get(key);
                Object pval2 = values2.get(key);
                if (pval1 instanceof Map) {
                    values1.put(key, mergeMaps((Map) pval1, (Map) pval2));
                } else if (pval1 instanceof List) {
                    values1.put(key, mergeLists((List) pval1, (List) pval2).orElse(null));
                } else if (pval1 instanceof Constructible) {
                    values1.put(key, mergeObjects(pval1, pval2));
                } else {
                    values1.put(key, pval2);
                }
            } else {
                Object pval2 = values2.get(key);
                values1.put(key, pval2);
            }
        }

        if (values1 instanceof Constructible) {
            if (values1 instanceof Reference) {
                Reference ref1 = (Reference) values1;
                Reference ref2 = (Reference) values2;
                if (ref2.getRef() != null) {
                    ref1.setRef(ref2.getRef());
                }
            }
            if (values1 instanceof Extensible) {
                Extensible extensible1 = (Extensible) values1;
                Extensible extensible2 = (Extensible) values2;
                extensible1.setExtensions(mergeMaps(extensible1.getExtensions(), extensible2.getExtensions()));
            }
        }

        return values1;
    }

    /**
     * Merges two Lists. Any values missing from List1 but present in List2 will be added. Depending on
     * the type of list, further processing and de-duping may be required.
     *
     * @param values1
     * @param values2
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Optional<List> mergeLists(List values1, List values2) {
        if (values1 == null && values2 == null) {
            return Optional.empty();
        }
        if (values1 != null && values2 == null) {
            return Optional.of(values1);
        }
        if (values1 == null && values2 != null) {
            return Optional.of(values2);
        }
        if (values1.equals(values2)) {
            // Do not merge identical lists
            return Optional.of(values1);
        }

        if (values1.get(0) instanceof String) {
            return Optional.of(mergeStringLists(values1, values2));
        }

        if (values1.get(0) instanceof Tag) {
            return Optional.of(mergeTagLists(values1, values2));
        }

        if (values1.get(0) instanceof Server) {
            return Optional.of(mergeServerLists(values1, values2));
        }

        if (values1.get(0) instanceof SecurityRequirement) {
            return Optional.of(mergeSecurityRequirementLists(values1, values2));
        }

        if (values1.get(0) instanceof Parameter) {
            return Optional.of(mergeParameterLists(values1, values2));
        }

        List merged = new ArrayList<>(values1.size() + values2.size());
        merged.addAll(values1);
        merged.addAll(values2);
        return Optional.of(merged);
    }

    /**
     * Merge a list of strings. In all cases, string lists are really sets. So this is just
     * combining the two lists and then culling duplicates.
     *
     * @param values1
     * @param values2
     */
    private static List<String> mergeStringLists(List<String> values1, List<String> values2) {
        Set<String> set = new LinkedHashSet<>();
        set.addAll(values1);
        set.addAll(values2);
        return new ArrayList<>(set);
    }

    /**
     * Merge two lists of Tags. Tags are a special case because they are named and you cannot
     * have two Tags with the same name. This will append any tags from values2 that don't
     * exist in values1. It will *merge* any tags found in values2 that already exist in
     * values1.
     *
     * @param values1
     * @param values2
     */
    private static List<Tag> mergeTagLists(List<Tag> values1, List<Tag> values2) {
        values1 = new ArrayList<>(values1);

        for (Tag value2 : values2) {
            Tag match = null;
            for (Tag value1 : values1) {
                if (value1.getName() != null && value1.getName().equals(value2.getName())) {
                    match = value1;
                    break;
                }
            }
            if (match == null) {
                values1.add(value2);
            } else {
                mergeObjects(match, value2);
            }
        }
        return values1;
    }

    /**
     * Merge two lists of Servers. Servers are a special case because they must be unique
     * by the 'url' property each must have.
     *
     * @param values1
     * @param values2
     */
    private static List<Server> mergeServerLists(List<Server> values1, List<Server> values2) {
        values1 = new ArrayList<>(values1);

        for (Server value2 : values2) {
            Server match = null;
            for (Server value1 : values1) {
                if (value1.getUrl() != null && value1.getUrl().equals(value2.getUrl())) {
                    match = value1;
                    break;
                }
            }
            if (match == null) {
                values1.add(value2);
            } else {
                mergeObjects(match, value2);
            }
        }
        return values1;
    }

    /**
     * Merge two lists of Security Requirements. Security Requirement lists are are a
     * special case because
     * values1.
     *
     * @param values1
     * @param values2
     */
    private static List<SecurityRequirement> mergeSecurityRequirementLists(List<SecurityRequirement> values1,
            List<SecurityRequirement> values2) {

        values1 = new ArrayList<>(values1);

        for (SecurityRequirement value2 : values2) {
            if (values1.contains(value2)) {
                continue;
            }
            values1.add(value2);
        }
        return values1;
    }

    /**
     * Merge two lists of Parameters. Parameters are a special case because they must be unique
     * by the name in 'in' each have
     *
     * @param values1
     * @param values2
     */
    private static List<Parameter> mergeParameterLists(List<Parameter> values1, List<Parameter> values2) {
        values1 = new ArrayList<>(values1);

        for (Parameter value2 : values2) {
            Parameter match = null;
            for (Parameter value1 : values1) {
                if (value1.getName() == null || !value1.getName().equals(value2.getName())) {
                    continue;
                }

                match = value1;
                break;
            }
            if (match == null) {
                values1.add(value2);
            } else {
                mergeObjects(match, value2);
            }
        }
        return values1;
    }
}
