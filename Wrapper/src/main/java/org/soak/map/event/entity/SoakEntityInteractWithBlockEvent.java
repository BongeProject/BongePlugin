package org.soak.map.event.entity;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.SoakActionMap;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlockSnapshot;
import org.soak.wrapper.entity.AbstractEntity;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;

public class SoakEntityInteractWithBlockEvent extends SoakEvent<InteractBlockEvent, EntityInteractEvent> {

    public SoakEntityInteractWithBlockEvent(Class<EntityInteractEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<InteractBlockEvent> spongeEventClass() {
        return InteractBlockEvent.class;
    }

    @Override
    public void handle(InteractBlockEvent spongeEvent) throws Exception {
        var opSpongeEntity = spongeEvent.cause().first(Entity.class);
        if (opSpongeEntity.isEmpty() || opSpongeEntity.get() instanceof ServerPlayer) {
            return;
        }
        var spongeEntity = opSpongeEntity.get();
        var entity = AbstractEntity.wrap(spongeEntity);
        var block = new SoakBlockSnapshot(spongeEvent.block());

        var bukkitEvent = new EntityInteractEvent(entity, block);
        fireEvent(bukkitEvent);
        if (bukkitEvent.isCancelled() && spongeEvent instanceof Cancellable cancellableEvent) {
            cancellableEvent.setCancelled(true);
        }
    }
}
