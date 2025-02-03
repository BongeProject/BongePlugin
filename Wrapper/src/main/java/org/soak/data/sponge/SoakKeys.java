package org.soak.data.sponge;

import org.soak.data.BukkitPersistentData;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.event.lifecycle.RegisterDataEvent;
import org.spongepowered.api.util.Ticks;

public class SoakKeys {

    public static final Key<Value<Ticks>> PORTAL_COOLDOWN = PortalCooldownCustomData.generateKey();
    public static final Key<Value<BukkitPersistentData>> BUKKIT_DATA = Key.builder()
            .elementType(BukkitPersistentData.class)
            .key(ResourceKey.of(SoakManager.getManager().getOwnContainer(), "plugin_data"))
            .build();

    public static void init(RegisterDataEvent event) {
        PortalCooldownCustomData.init(event);
    }
}
