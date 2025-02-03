package org.soak.utils;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DataOverride<T> implements Supplier<T> {

    private T override;
    private final Supplier<T> getter;
    private boolean useOverride;

    private DataOverride(Supplier<T> getter, boolean useOverride, T override) {
        this.useOverride = useOverride;
        this.override = override;
        this.getter = getter;
    }

    public DataOverride(Supplier<T> getter) {
        this.getter = getter;
    }

    @Override
    public @Nullable T get() {
        if (useOverride) {
            return override;
        }
        return getter.get();
    }

    public DataOverride<T> copy() {
        return new DataOverride<>(getter, useOverride, override);
    }

    public void reset() {
        this.useOverride = false;
    }

    public void set(@Nullable T value) {
        this.override = value;
        this.useOverride = true;
    }

    public void applyTo(Consumer<T> consumer) {
        if (this.useOverride) {
            consumer.accept(this.override);
        }
    }
}
