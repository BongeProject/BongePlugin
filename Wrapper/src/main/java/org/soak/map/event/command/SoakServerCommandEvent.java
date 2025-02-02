package org.soak.map.event.command;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.SoakSubjectMap;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.ExecuteCommandEvent;
import org.spongepowered.api.service.permission.Subject;

public class SoakServerCommandEvent extends SoakEvent<ExecuteCommandEvent.Pre, ServerCommandEvent> {

    public SoakServerCommandEvent(Class<ServerCommandEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ExecuteCommandEvent.Pre> spongeEventClass() {
        return ExecuteCommandEvent.Pre.class;
    }

    @Override
    public void handle(ExecuteCommandEvent.Pre event) throws Exception {
        var root = event.commandCause().root();
        if (root instanceof ServerPlayer) {
            return;
        }
        var command = event.command() + " " + event.arguments();
        var sender = SoakSubjectMap.mapToBukkit((Subject) root);

        var bukkitEvent = new ServerCommandEvent(sender, command);
        fireEvent(bukkitEvent);
        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
        }

        var newCommand = bukkitEvent.getCommand();
        var newCommandSplit = newCommand.split(" ", 2);
        var newRoot = newCommandSplit[0];
        String newArguments = "";
        if (newCommandSplit.length == 2) {
            newArguments = newCommandSplit[1];
        }

        if (!command.equals(newCommand)) {
            event.setCommand(newRoot);
            event.setArguments(newArguments);
        }
    }
}
