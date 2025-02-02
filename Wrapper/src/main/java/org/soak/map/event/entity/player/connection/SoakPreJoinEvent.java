package org.soak.map.event.entity.player.connection;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

import java.net.InetAddress;
import java.util.UUID;

public class SoakPreJoinEvent extends SoakEvent<ServerSideConnectionEvent.Login, AsyncPlayerPreLoginEvent> {

    public SoakPreJoinEvent(Class<AsyncPlayerPreLoginEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ServerSideConnectionEvent.Login> spongeEventClass() {
        return ServerSideConnectionEvent.Login.class;
    }

    @Override
    public void handle(ServerSideConnectionEvent.Login event) throws Exception {
        String profileName = event.profile().name().orElse("");
        UUID uuid = event.profile().uniqueId();
        InetAddress address = event.connection().address().getAddress();

        AsyncPlayerPreLoginEvent bukkitEvent = new AsyncPlayerPreLoginEvent(profileName, address, uuid);
        fireEvent(bukkitEvent);
    }
}
