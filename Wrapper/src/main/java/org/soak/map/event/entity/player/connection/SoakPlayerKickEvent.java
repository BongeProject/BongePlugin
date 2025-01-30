package org.soak.map.event.entity.player.connection;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.living.player.KickPlayerEvent;

public class SoakPlayerKickEvent extends SoakEvent<KickPlayerEvent, PlayerKickEvent> {

    public SoakPlayerKickEvent(Class<PlayerKickEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    public void handle(KickPlayerEvent event) throws Exception {
        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get(event.player());
        var message = event.message();

        var bukkitEvent = new PlayerKickEvent(player, message, message); //TODO -> PlayerKickEvent.Cause
        fireEvent(bukkitEvent);

        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
        }
        if (!bukkitEvent.leaveMessage().equals(message)) {
            event.setMessage(bukkitEvent.leaveMessage());
        }
    }
}
