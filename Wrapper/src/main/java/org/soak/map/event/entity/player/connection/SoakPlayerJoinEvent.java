package org.soak.map.event.entity.player.connection;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class SoakPlayerJoinEvent extends SoakEvent<ServerSideConnectionEvent.Join, PlayerJoinEvent> {

    public SoakPlayerJoinEvent(Class<PlayerJoinEvent> bukkitEvent, EventPriority priority, Plugin plugin,
                               Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ServerSideConnectionEvent.Join> spongeEventClass() {
        return ServerSideConnectionEvent.Join.class;
    }

    @Override
    public void handle(ServerSideConnectionEvent.Join event) throws Exception {
        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get(event.player());
        var message = event.message();

        var bukkitEvent = new PlayerJoinEvent(player, message);
        fireEvent(bukkitEvent);
        var newJoinMessage = bukkitEvent.joinMessage();

        if (newJoinMessage != null && !newJoinMessage.equals(message)) {
            event.setMessage(newJoinMessage);
        }
        if (newJoinMessage == null) {
            event.setMessageCancelled(true);
        }
    }
}
