package org.soak.map;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.ResourceKey;

public class SoakResourceKeyMap {

    public static ResourceKey mapToSponge(NamespacedKey key) {
        return ResourceKey.of(key.namespace(), key.getKey());
    }

    public static NamespacedKey mapToBukkit(ResourceKey key) {
        return NamespacedKey.fromString(key.formatted());
    }

    public static ResourceKey mapToSponge(@NotNull Key key) {
        return ResourceKey.resolve(key.asString());
    }
}
