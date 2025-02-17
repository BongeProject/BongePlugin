package org.soak.map.event.inventory.hand.action;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.map.item.SoakItemStackMap;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;

public class SoakItemUseEvent extends SoakEvent<UseItemStackEvent.Finish, PlayerItemConsumeEvent> {

    public SoakItemUseEvent(Class<PlayerItemConsumeEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<UseItemStackEvent.Finish> spongeEventClass() {
        return UseItemStackEvent.Finish.class;
    }

    @Override
    public void handle(UseItemStackEvent.Finish event) throws Exception {
        var opSpongePlayer = event.cause().first(ServerPlayer.class);
        if (opSpongePlayer.isEmpty()) {
            return;
        }
        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get(opSpongePlayer.get());
        var item = SoakItemStackMap.toBukkit(event.itemStackInUse());

        var bukkitEvent = new PlayerItemConsumeEvent(player, item);
        fireEvent(bukkitEvent);
        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
        }
        //TODO set item being consumed
        //TODO set item replacement (effects)
    }
}
