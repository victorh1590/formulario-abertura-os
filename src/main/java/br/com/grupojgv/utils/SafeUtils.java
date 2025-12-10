package br.com.grupojgv.utils;

import java.util.Optional;

public class SafeUtils {
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    public static <T> Optional<T> tryGet(ThrowingSupplier<T> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
