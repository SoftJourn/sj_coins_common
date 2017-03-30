package com.softjourn.common.functions;

import java.util.Optional;
import java.util.function.Function;

public interface OptionalUtil {

    static <T, S> Function<T, Optional<S>> nullable(Function<T, S> getter) {
        return value -> Optional.ofNullable(getter.apply(value));
    }

    static <T, R> R allChainOrElse(T value, Function<T, R> getter, R otherwise) {
        return allChainOrElse(value, getter, Function.identity(), otherwise);
    }

    static <T, S, R> R allChainOrElse(T value, Function<T, S> getter1, Function<S, R> getter2, R otherwise) {
        return allChainOrElse(value, getter1, getter2, Function.identity(), otherwise);
    }

    static <T, S, S1, R> R allChainOrElse(T value,
                                          Function<T, S> getter1,
                                          Function<S, S1> getter2,
                                          Function<S1, R> getter3,
                                          R otherwise) {
        return Optional.ofNullable(value)
                .flatMap(nullable(getter1))
                .flatMap(nullable(getter2))
                .flatMap(nullable(getter3))
                .orElse(otherwise);
    }
}
