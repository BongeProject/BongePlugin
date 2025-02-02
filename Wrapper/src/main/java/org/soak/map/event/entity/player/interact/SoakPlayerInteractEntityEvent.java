package org.soak.map.event.entity.player.interact;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.SoakActionMap;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.entity.AbstractEntity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

public class SoakPlayerInteractEntityEvent extends SoakEvent<InteractEntityEvent, PlayerInteractEntityEvent> {

    public SoakPlayerInteractEntityEvent(Class<PlayerInteractEntityEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<InteractEntityEvent> spongeEventClass() {
        return InteractEntityEvent.class;
    }

    @Override
    public void handle(InteractEntityEvent spongeEvent) throws Exception {
        var opSpongePlayer = spongeEvent.cause().first(ServerPlayer.class);
        if (opSpongePlayer.isEmpty()) {
            return;
        }
        var spongePlayer = opSpongePlayer.get();
        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongePlayer);
        var spongeEntity = spongeEvent.entity();
        var entity = AbstractEntity.wrap(spongeEntity);
        var spongeHand = spongeEvent.context()
                .get(EventContextKeys.USED_HAND)
                .orElseThrow(() -> new RuntimeException("Unknown hand type from event"));
        var hand = SoakActionMap.toBukkit(spongeHand);

        var bukkitEvent = new PlayerInteractEntityEvent(player, entity, hand);
        fireEvent(bukkitEvent);
        if (bukkitEvent.isCancelled()) {
            spongeEvent.setCancelled(true);
        }
    }
}
