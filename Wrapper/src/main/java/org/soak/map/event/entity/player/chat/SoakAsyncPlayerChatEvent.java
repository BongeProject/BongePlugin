package org.soak.map.event.entity.player.chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.SoakMessageMap;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.PlayerChatEvent;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class SoakAsyncPlayerChatEvent extends SoakEvent<PlayerChatEvent.Submit, AsyncPlayerChatEvent> {

    private static final String DEFAULT_FORMAT = "<%1$s> %2$s";

    public SoakAsyncPlayerChatEvent(Class<AsyncPlayerChatEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    public void handle(PlayerChatEvent.Submit event) throws Exception {
        var opPlayer = event.player();
        if (opPlayer.isEmpty()) {
            return;
        }
        var player = opPlayer.get();
        var bukkitPlayer = SoakManager.<WrapperManager>getManager().getMemoryStore().get(player);
        Set<Player> receivers = event
                .filter()
                .map(filter -> Sponge
                        .server()
                        .onlinePlayers()
                        .stream()
                        .filter(filter)
                        .map(spongePlayer -> (Player) SoakManager
                                .<WrapperManager>getManager()
                                .getMemoryStore()
                                .get(spongePlayer))
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
        var message = SoakMessageMap.mapToBukkit(event.message());

        var bukkitEvent = new AsyncPlayerChatEvent(Bukkit.getServer().isPrimaryThread(), bukkitPlayer, message, receivers);
        fireEvent(bukkitEvent);

        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
        }
        if (!message.equals(bukkitEvent.getMessage())) {
            var displayName = bukkitPlayer.getDisplayName();
            var eventMessage = bukkitEvent.getMessage();
            var formattedMessage = String.format(bukkitEvent.getMessage(), displayName, eventMessage);
            event.setMessage(SoakMessageMap.toComponent(formattedMessage));
        }
    }
}
