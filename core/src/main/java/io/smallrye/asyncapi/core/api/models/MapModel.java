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
package io.smallrye.asyncapi.core.api.models;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public interface MapModel<V> extends Map<String, V> {

    Map<String, V> getMap();

    void setMap(Map<String, V> map);

    default <T> T invokeFunction(Function<Map<String, V>, T> function) {
        Map<String, V> map = getMap();
        if (map == null) {
            map = new LinkedHashMap<>();
            setMap(map);
        }
        return function.apply(map);
    }

    default void invoke(Consumer<Map<String, V>> function) {
        Map<String, V> map = getMap();
        if (map == null) {
            map = new LinkedHashMap<>();
            setMap(map);
        }
        function.accept(map);
    }

    @Override
    default int size() {
        return invokeFunction(Map::size);
    }

    @Override
    default boolean isEmpty() {
        return invokeFunction(Map::isEmpty);
    }

    @Override
    default boolean containsValue(Object value) {
        return invokeFunction(map -> map.containsValue(value));
    }

    @Override
    default void clear() {
        invoke(Map::clear);
    }

    @Override
    default Set<String> keySet() {
        return invokeFunction(Map::keySet);
    }

    @Override
    default Collection<V> values() {
        return invokeFunction(Map::values);
    }

    @Override
    default Set<Entry<String, V>> entrySet() {
        return invokeFunction(Map::entrySet);
    }

    @Override
    default V get(Object key) {
        return invokeFunction(map -> map.get(key));
    }

    @Override
    default boolean containsKey(Object key) {
        return invokeFunction(map -> map.containsKey(key));
    }

    @Override
    default V put(String key, V value) {
        return invokeFunction(map -> map.put(key, value));
    }

    @Override
    default void putAll(Map<? extends String, ? extends V> m) {
        invoke(map -> map.putAll(m));
    }

    @Override
    default V remove(Object key) {
        return invokeFunction(map -> map.remove(key));
    }
}
