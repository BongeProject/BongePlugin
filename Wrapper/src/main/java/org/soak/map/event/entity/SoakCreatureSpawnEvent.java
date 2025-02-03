package org.soak.map.event.entity;

import org.bukkit.entity.Creature;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.SoakSpawnReasonMap;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.entity.AbstractEntity;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.SpawnType;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

public class SoakCreatureSpawnEvent extends SoakEvent<SpawnEntityEvent, CreatureSpawnEvent> {

    public SoakCreatureSpawnEvent(Class<CreatureSpawnEvent> bukkitEvent, EventPriority priority, Plugin plugin,
                                  Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<SpawnEntityEvent> spongeEventClass() {
        return SpawnEntityEvent.class;
    }

    @Override
    public void handle(SpawnEntityEvent event) throws Exception {
        var opSpawnType = event.cause().first(SpawnType.class);
        if (opSpawnType.isEmpty()) {
            return;
        }

        var reason = SoakSpawnReasonMap.toBukkit(opSpawnType.get(), event.cause());

        event.filterEntities(entity -> {
            if (!(entity instanceof org.spongepowered.api.entity.living.PathfinderAgent)) {
                return true;
            }
            Creature bukkitEntity = (Creature) AbstractEntity.wrap(entity);
            var bukkitEvent = new CreatureSpawnEvent(bukkitEntity, reason);

            fireEvent(bukkitEvent);
            return !bukkitEvent.isCancelled();
        });
    }
}
