package org.soak.map.event.command;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.entity.living.human.SoakPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.command.ExecuteCommandEvent;

public class SoakPreCommandEvent extends SoakEvent<ExecuteCommandEvent.Pre, PlayerCommandPreprocessEvent> {

    public SoakPreCommandEvent(Class<PlayerCommandPreprocessEvent> bukkitEvent, EventPriority priority, Plugin plugin
            , Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ExecuteCommandEvent.Pre> spongeEventClass() {
        return ExecuteCommandEvent.Pre.class;
    }

    @Override
    public void handle(ExecuteCommandEvent.Pre event) throws Exception {
        var root = event.commandCause().root();
        if (!(root instanceof ServerPlayer serverPlayer)) {
            return;
        }

        var command = "/" + event.command() + " " + event.arguments();
        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get(serverPlayer);

        var bukkitEvent = new PlayerCommandPreprocessEvent(player, command);
        fireEvent(bukkitEvent);
        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
        }

        var newCommand = bukkitEvent.getMessage();
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

        if (!bukkitEvent.getPlayer().equals(player)) {
            event.setCancelled(true);
            var newBukkitPlayer = (SoakPlayer) bukkitEvent.getPlayer();
            try {
                Sponge.server().commandManager().process(newBukkitPlayer.spongeEntity(), newArguments);
            } catch (CommandException e) {
                SoakManager.getManager().displayError(e, this.plugin());
            }
        }
    }
}
