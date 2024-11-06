package org.soak.wrapper.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.WrapperManager;
import org.soak.command.BukkitRawCommand;
import org.soak.plugin.SoakManager;
import org.soak.utils.GenericHelper;
import org.soak.wrapper.plugin.SoakPluginManager;
import org.spongepowered.api.Sponge;

import java.util.Collections;
import java.util.Objects;

public class SoakCommandMap extends SimpleCommandMap {


    public SoakCommandMap() {
        super(Bukkit.getServer(), Collections.emptyMap());
    }

    @Override
    public boolean register(@NotNull String label, @NotNull String fallbackPrefix, @NotNull Command command) {
        return registerCommand(command, fallbackPrefix, label);
    }

    private boolean registerCommand(@NotNull Command command, @NotNull String fallback, @Nullable String label) {
        this.knownCommands.put(Objects.requireNonNullElseGet(label, command::getLabel), command);
        boolean result = command.register(this);

        var rawRegister = Sponge.server().commandManager().registrar(org.spongepowered.api.command.Command.Raw.class).orElseThrow(() -> new RuntimeException("Cannot register late command of '" + label + "'"));
        var plugin = GenericHelper.fromStackTrace();
        var soakPlugin = SoakManager.getManager().getContainer(plugin).orElseThrow(() -> new RuntimeException("Cannot get the soakPlugin from '" + plugin.metadata().id() + "'"));
        var bukkitRawWrapper = new BukkitRawCommand(soakPlugin, command);
        rawRegister.register(plugin, bukkitRawWrapper, command.getName(), command.getAliases().toArray(String[]::new));
        return result;
    }
}
