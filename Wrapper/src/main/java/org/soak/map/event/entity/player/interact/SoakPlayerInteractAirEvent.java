package org.soak.map.event.entity.player.interact;

import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.SoakActionMap;
import org.soak.map.event.SoakEvent;
import org.soak.map.item.SoakItemStackMap;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;

public class SoakPlayerInteractAirEvent extends SoakEvent<InteractEvent, PlayerInteractEvent> {

    public SoakPlayerInteractAirEvent(Class<PlayerInteractEvent> bukkitEvent, EventPriority priority, Plugin plugin,
                                      Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<InteractEvent> spongeEventClass() {
        return InteractEvent.class;
    }

    @Override
    public void handle(InteractEvent spongeEvent) throws Exception {
        var opSpongePlayer = spongeEvent.cause().first(ServerPlayer.class);
        if (opSpongePlayer.isEmpty()) {
            return;
        }
        if (spongeEvent instanceof InteractEntityEvent) {
            return;
        }
        var spongePlayer = opSpongePlayer.get();
        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongePlayer);
        var clickedFace = BlockFace.SELF;
        var spongeHand = spongeEvent.context()
                .get(EventContextKeys.USED_HAND)
                .orElseThrow(() -> new RuntimeException("Unknown hand type from event"));
        var action = SoakActionMap.toBukkit(spongeHand, true);
        var hand = SoakActionMap.toBukkit(spongeHand);
        var spongeItem = spongePlayer.itemInHand(spongeHand);
        var item = SoakItemStackMap.toBukkit(spongeItem);

        var bukkitEvent = new PlayerInteractEvent(player, action, item, null, clickedFace, hand);
        fireEvent(bukkitEvent);
        if (spongeEvent instanceof Cancellable) {
            if (bukkitEvent.useItemInHand() == Event.Result.DENY) {
                ((Cancellable) spongeEvent).setCancelled(true);
            }
        }
    }
}
