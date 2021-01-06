package io.smallrye.asyncapi.core.runtime.scanner;

public abstract class IterableStandin<E> implements Iterable<E> {
    E value;
}
