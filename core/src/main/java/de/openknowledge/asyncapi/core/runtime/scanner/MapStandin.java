package de.openknowledge.asyncapi.core.runtime.scanner;

import java.util.Map;

public abstract class MapStandin<K, V> implements Map<K, V> {
    K key;

    V value;
}
