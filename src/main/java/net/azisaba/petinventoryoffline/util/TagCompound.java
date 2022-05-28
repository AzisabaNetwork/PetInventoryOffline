package net.azisaba.petinventoryoffline.util;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TagCompound {
    private final Object tagCompound;

    public TagCompound(@NotNull Object tagCompound) {
        this.tagCompound = tagCompound;
    }

    @SuppressWarnings("unchecked")
    public Map<String, ?> getCompoundData() {
        try {
            return (Map<String, ?>) tagCompound.getClass().getMethod("getCompoundData").invoke(tagCompound);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getAs(@NotNull String key, @NotNull Class<?> clazz) {
        try {
            return (T) tagCompound.getClass().getMethod("getAs", String.class, Class.class).invoke(tagCompound, key, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> boolean containsKeyAs(@NotNull String key, @NotNull Class<T> clazz) {
        try {
            return (boolean) tagCompound.getClass().getMethod("containsKeyAs", String.class, Class.class).invoke(tagCompound, key, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object getWrappedObject() {
        return tagCompound;
    }
}
