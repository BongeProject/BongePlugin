package org.soak.map.event.entity.player.interact;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.soak.WrapperManager;
import org.soak.map.SoakActionMap;
import org.soak.map.SoakDirectionMap;
import org.soak.map.event.SoakEvent;
import org.soak.map.item.SoakItemStackMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlockSnapshot;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

public class SoakPlayerInteractBlockEvent extends SoakEvent<InteractBlockEvent, PlayerInteractEvent> {

    public SoakPlayerInteractBlockEvent(Class<PlayerInteractEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    public void handle(InteractBlockEvent event) throws Exception {
        var opSpongePlayer = event.cause().first(ServerPlayer.class);
        if (opSpongePlayer.isEmpty()) {
            return;
        }
        var spongePlayer = opSpongePlayer.get();

        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongePlayer);
        var spongePosition = event.block().position();
        var interactionPoint = new Vector(
                spongePosition.x(),
                spongePosition.y(),
                spongePosition.z());
        var clickedFace = SoakDirectionMap.toBukkit(event.targetSide());
        var spongeHand = event.context()
                .get(EventContextKeys.USED_HAND)
                .orElseThrow(() -> new RuntimeException("Unknown hand type from event"));
        var action = SoakActionMap.toBukkit(spongeHand, false);
        var hand = SoakActionMap.toBukkit(spongeHand);
        var spongeItem = spongePlayer.itemInHand(spongeHand);
        var item = SoakItemStackMap.toBukkit(spongeItem);
        var block = new SoakBlockSnapshot(event.block());

        var bukkitEvent = new PlayerInteractEvent(player, action, item, block, clickedFace, hand, interactionPoint);
        fireEvent(bukkitEvent);
        if (event instanceof Cancellable) {
            if (bukkitEvent.useInteractedBlock() == Event.Result.DENY) {
                ((Cancellable) event).setCancelled(true);
            }
        }
    }
}
