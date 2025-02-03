package org.soak.map.event.entity.player.chat;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.SoakSubjectMap;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.ChatTypes;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.PlayerChatEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SoakAsyncChatEvent extends SoakEvent<PlayerChatEvent.Submit, AsyncChatEvent> {

    public SoakAsyncChatEvent(Class<AsyncChatEvent> bukkitEvent, EventPriority priority, Plugin plugin,
                              Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<PlayerChatEvent.Submit> spongeEventClass() {
        return PlayerChatEvent.Submit.class;
    }

    @Override
    public void handle(PlayerChatEvent.Submit event) throws Exception {
        var opPlayer = event.player();
        if (opPlayer.isEmpty()) {
            return;
        }
        var player = opPlayer.get();
        var bukkitPlayer = SoakManager.<WrapperManager>getManager().getMemoryStore().get(player);
        Set<Audience> receivers = new HashSet<>(event.filter()
                                                        .map(filter -> Sponge.server()
                                                                .onlinePlayers()
                                                                .stream()
                                                                .filter(filter)
                                                                .map(spongePlayer -> (Player) SoakManager.<WrapperManager>getManager()
                                                                        .getMemoryStore()
                                                                        .get(spongePlayer))
                                                                .collect(Collectors.toSet()))
                                                        .orElse(Collections.emptySet()));
        if (event.chatType().equals(ChatTypes.CHAT)) {
            receivers.add(SoakSubjectMap.mapToBukkit(Sponge.systemSubject()));
        }
        var message = event.message();
        var originalMessage = event.originalMessage();
        var signedMessage = event.isSigned() ?
                SignedMessage.system(PlainTextComponentSerializer.plainText().serialize(event.originalMessage()),
                                     event.originalMessage()) :
                null; //TODO ideally get signed message
        var chatRender = ChatRenderer.defaultRenderer(); //TODO find out the alternative

        var bukkitEvent = new AsyncChatEvent(Bukkit.getServer().isPrimaryThread(),
                                             bukkitPlayer,
                                             receivers,
                                             chatRender,
                                             message,
                                             originalMessage,
                                             signedMessage);
        fireEvent(bukkitEvent);
        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
        }
        if (!message.equals(bukkitEvent.message())) {
            event.setMessage(bukkitEvent.message());
        }
    }
}
