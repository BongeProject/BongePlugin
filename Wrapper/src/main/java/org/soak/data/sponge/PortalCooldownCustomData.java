package org.soak.data.sponge;

import io.papermc.paper.util.Tick;
import org.jetbrains.annotations.ApiStatus;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataProvider;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.persistence.DataStore;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.lifecycle.RegisterDataEvent;
import org.spongepowered.api.util.Ticks;

import java.util.Comparator;

@ApiStatus.Internal
public class PortalCooldownCustomData {

    public static void createTickScheduler() {
        Sponge.asyncScheduler()
                .executor(SoakManager.getManager().getOwnContainer())
                .scheduleAtFixedRate(() -> Sponge.server()
                        .worldManager()
                        .worlds()
                        .stream()
                        .flatMap(world -> world.entities().stream())
                        .filter(entity -> entity.get(SoakKeys.PORTAL_COOLDOWN)
                                .map(ticks -> ticks.ticks() > 0)
                                .orElse(false))
                        .forEach(entity -> {
                            var ticksLeft = entity.get(SoakKeys.PORTAL_COOLDOWN).orElseThrow().ticks();
                            if (ticksLeft == 0) {
                                entity.remove(SoakKeys.PORTAL_COOLDOWN);
                                return;
                            }
                            entity.offer(SoakKeys.PORTAL_COOLDOWN, Ticks.of(ticksLeft - 1));
                        }), 0, 1, Tick.tick());
    }

    static Key<Value<Ticks>> generateKey() {
        return Key.builder()
                .elementType(Ticks.class)
                .comparator(Comparator.comparing(Ticks::ticks))
                .key(ResourceKey.of(SoakManager.getManager().getOwnContainer(), "portal_cooldown"))
                .build();
    }

    static void init(RegisterDataEvent event) {
        var resourceKey = ResourceKey.of(SoakManager.getManager().getOwnContainer(), "portal_cooldown");

        var store = DataStore.builder()
                .pluginData(resourceKey)
                .holder(Entity.class)
                .key(SoakKeys.PORTAL_COOLDOWN, "portal_cooldown")
                .build();

        var provider = DataProvider.mutableBuilder().key(SoakKeys.PORTAL_COOLDOWN).dataHolder(Entity.class).build();

        var registration = DataRegistration.builder()
                .dataKey(SoakKeys.PORTAL_COOLDOWN)
                .store(store)
                .provider(provider)
                .build();

        event.register(registration);
    }
}
