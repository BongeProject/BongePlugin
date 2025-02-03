package org.soak;

import io.papermc.paper.ServerBuildInfo;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.soak.exception.NotImplementedException;

import java.time.Instant;
import java.util.Optional;
import java.util.OptionalInt;

@SuppressWarnings("NonExtendableApiUsage")
public class SoakServerBuildInfo implements ServerBuildInfo {

    @Override
    public @NotNull Key brandId() {
        return Key.key("soak", "sponge");
    }

    @Override
    public boolean isBrandCompatible(@NotNull Key key) {
        throw NotImplementedException.createByLazy(ServerBuildInfo.class, "isBrandCompatible", Key.class);
    }

    @Override
    public @NotNull String brandName() {
        return "soak";
    }

    @Override
    public @NotNull String minecraftVersionId() {
        return Bukkit.getMinecraftVersion();
    }

    @Override
    public @NotNull String minecraftVersionName() {
        return Bukkit.getMinecraftVersion();
    }

    @Override
    public @NotNull OptionalInt buildNumber() {
        return OptionalInt.empty();
    }

    @Override
    public @NotNull Instant buildTime() {
        throw NotImplementedException.createByLazy(ServerBuildInfo.class, "buildTime");
    }

    @Override
    public @NotNull Optional<String> gitBranch() {
        return Optional.empty();
    }

    @Override
    public @NotNull Optional<String> gitCommit() {
        return Optional.empty();
    }

    @Override
    public @NotNull String asString(StringRepresentation stringRepresentation) {
        return switch (stringRepresentation) {
            case VERSION_SIMPLE -> minecraftVersionId();
            case VERSION_FULL -> "soak:" + minecraftVersionId();
        };
    }
}
