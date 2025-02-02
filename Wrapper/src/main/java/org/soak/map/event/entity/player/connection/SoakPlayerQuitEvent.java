package org.soak.map.event.entity.player.connection;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class SoakPlayerQuitEvent extends SoakEvent<ServerSideConnectionEvent.Leave, PlayerQuitEvent> {

    public SoakPlayerQuitEvent(Class<PlayerQuitEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ServerSideConnectionEvent.Leave> spongeEventClass() {
        return ServerSideConnectionEvent.Leave.class;
    }

    @Override
    public void handle(ServerSideConnectionEvent.Leave event) throws Exception {
        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get(event.player());
        var message = event.message();

        var bukkitEvent = new PlayerQuitEvent(player, message); //TODO PlayerQuitEvent.QuitReason
        fireEvent(bukkitEvent);

        var newQuitMessage = bukkitEvent.quitMessage();

        if (newQuitMessage != null && !newQuitMessage.equals(message)) {
            event.setMessage(newQuitMessage);
        }
        if (newQuitMessage == null) {
            event.setAudience(null);
        }
    }
}
