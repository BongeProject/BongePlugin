package org.soak.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NMSUsageException extends RuntimeException {

    private final String developerNotes;

    public NMSUsageException(@NotNull String classUsed, @NotNull String methodUsed, Object... parametersUsed) {
        this(null, classUsed, methodUsed, parametersUsed);
    }

    public NMSUsageException(@Nullable String developerNotes, @NotNull String classUsed, @NotNull String methodUsed, Object... parametersUsed) {
        super("NMS required for " + classUsed + "#" + methodUsed + "(" + Stream.of(parametersUsed).map(Object::toString).collect(Collectors.joining(", ")) + ")");
        this.developerNotes = developerNotes;
    }

    public Optional<String> getDeveloperNotes() {
        return Optional.ofNullable(this.developerNotes);
    }
}
