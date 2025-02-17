package org.soak.map.event.server;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.StatusResponse;

public class SoakServerListPingEvent extends SoakEvent<ClientPingServerEvent, ServerListPingEvent> {

    public SoakServerListPingEvent(Class<ServerListPingEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ClientPingServerEvent> spongeEventClass() {
        return ClientPingServerEvent.class;
    }

    @Override
    public void handle(ClientPingServerEvent spongeEvent) throws Exception {
        var address = spongeEvent.client().address();

        ServerListPingEvent bEvent = new ServerListPingEvent(
                address.getHostName(),
                address.getAddress(),
                spongeEvent.response().description(),
                spongeEvent
                        .response()
                        .players()
                        .map(StatusResponse.Players::online)
                        .orElse(0),
                spongeEvent
                        .response()
                        .players()
                        .map(StatusResponse.Players::max)
                        .orElse(0));

        fireEvent(bEvent);
    }
}
