package org.soak.wrapper.plugin.messaging;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.messaging.PluginMessageListenerRegistration;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.jetbrains.annotations.NotNull;
import org.mose.collection.stream.builder.CollectionStreamBuilder;
import org.soak.WrapperManager;
import org.soak.exception.NotImplementedException;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.network.channel.ChannelManager;

import java.util.Set;

public class SoakMessenger extends StandardMessenger {

    private final ChannelManager manager;

    public SoakMessenger(ChannelManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean isReservedChannel(@NotNull String channel) {
        return super.isReservedChannel(channel);
    }

    @Override
    public void registerOutgoingPluginChannel(@NotNull Plugin plugin, @NotNull String channel) {
        SoakManager.getManager()
                .getLogger()
                .warn(plugin.getName() + " attempted to register a network channel of '" + channel + "'. Skipping");
    }

    @Override
    public void unregisterOutgoingPluginChannel(@NotNull Plugin plugin, @NotNull String channel) {
        SoakManager.getManager()
                .getLogger()
                .warn(plugin.getName() + " attempted to unregister a network channel of '" + channel + "'. Skipping");
    }

    @Override
    public void unregisterOutgoingPluginChannel(@NotNull Plugin plugin) {
        SoakManager.getManager()
                .getLogger()
                .warn(plugin.getName() + " attempted to unregister all its network channels. Skipping");
    }

    @Override
    public @NotNull PluginMessageListenerRegistration registerIncomingPluginChannel(@NotNull Plugin plugin,
                                                                                    @NotNull String channel,
                                                                                    @NotNull PluginMessageListener listener) {
        SoakManager.getManager()
                .getLogger()
                .warn(plugin.getName() + " attempted to register a network channel receiver of '" + channel + "'. " + "Skipping");
        return new PluginMessageListenerRegistration(this, plugin, channel, listener);
    }

    @Override
    public void unregisterIncomingPluginChannel(@NotNull Plugin plugin, @NotNull String channel,
                                                @NotNull PluginMessageListener listener) {
        SoakManager.getManager()
                .getLogger()
                .warn(plugin.getName() + " attempted to unregister a network channel receiver of '" + channel + "'. " + "Skipping");
    }

    @Override
    public void unregisterIncomingPluginChannel(@NotNull Plugin plugin, @NotNull String channel) {
        SoakManager.getManager()
                .getLogger()
                .warn(plugin.getName() + " attempted to unregister a network channel receiver of '" + channel + "'. " + "Skipping");
    }

    @Override
    public void unregisterIncomingPluginChannel(@NotNull Plugin plugin) {
        SoakManager.getManager()
                .getLogger()
                .warn(plugin.getName() + " attempted to unregister all its network channel receivers. Skipping");
    }

    @Override
    public @NotNull Set<String> getOutgoingChannels() {
        return CollectionStreamBuilder.builder()
                .collection(this.manager.channels())
                .basicMap(channel -> channel.key().formatted())
                .buildSet();
    }

    @Override
    public @NotNull Set<String> getOutgoingChannels(@NotNull Plugin plugin) {
        return CollectionStreamBuilder.builder()
                .collection(this.manager.channels())
                .map(stream -> stream.filter(channel -> channel.key()
                        .namespace()
                        .equals(plugin.getPluginMeta().getName())).map(channel -> channel.key().formatted()))
                .buildSet();
    }

    @Override
    public @NotNull Set<String> getIncomingChannels() {
        return this.getOutgoingChannels();
    }

    @Override
    public @NotNull Set<String> getIncomingChannels(@NotNull Plugin plugin) {
        return this.getOutgoingChannels(plugin);
    }

    @Override
    public @NotNull Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(@NotNull Plugin plugin) {
        return super.getIncomingChannelRegistrations(plugin);
    }

    @Override
    public @NotNull Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(@NotNull String channel) {
        return super.getIncomingChannelRegistrations(channel);
    }

    @Override
    public @NotNull Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(@NotNull Plugin plugin,
                                                                                           @NotNull String channel) {
        return super.getIncomingChannelRegistrations(plugin, channel);
    }

    @Override
    public boolean isRegistrationValid(@NotNull PluginMessageListenerRegistration registration) {
        return super.isRegistrationValid(registration);
    }

    @Override
    public boolean isIncomingChannelRegistered(@NotNull Plugin plugin, @NotNull String channelName) {
        var channelId = ResourceKey.of(plugin.getPluginMeta().getName(), channelName);
        return this.manager.channels().stream().anyMatch(channel -> channel.key().equals(channelId));
    }

    @Override
    public boolean isOutgoingChannelRegistered(@NotNull Plugin plugin, @NotNull String channel) {
        return this.isIncomingChannelRegistered(plugin, channel);
    }

    @Override
    public void dispatchIncomingMessage(@NotNull Player source, @NotNull String channelName, byte[] message) {
        throw NotImplementedException.createByLazy(Messenger.class,
                                                   "dispatchIncomingMessage",
                                                   Player.class,
                                                   String.class,
                                                   byte[].class);
    }
}
