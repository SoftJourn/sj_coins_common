package com.softjourn.common.functions;

import java.util.Optional;
import java.util.function.Function;

public interface OptionalUtil {

    static <T, S> Function<T, Optional<S>> nullable(Function<T, S> getter) {
        return value -> Optional.ofNullable(getter.apply(value));
    }
}
