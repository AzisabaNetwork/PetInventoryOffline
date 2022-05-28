package net.azisaba.petinventoryoffline.util;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;

public class Util {
    public static <T> T tryGet(@NotNull Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
