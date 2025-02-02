package org.soak.map.event.block;

import org.bukkit.block.sign.Side;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlock;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.entity.ChangeSignEvent;
import org.spongepowered.api.event.filter.cause.First;

public class SoakSignChangeEvent extends SoakEvent<ChangeSignEvent, SignChangeEvent> {

    public SoakSignChangeEvent(Class<SignChangeEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ChangeSignEvent> spongeEventClass() {
        return ChangeSignEvent.class;
    }

    @Override
    public void handle(ChangeSignEvent event) throws Exception {
        var opPlayer = event.cause().first(ServerPlayer.class);
        if (opPlayer.isEmpty()) {
            return;
        }
        var bukkitBlock = new SoakBlock(event.sign().serverLocation());
        var bukkitPlayer = SoakManager.<WrapperManager>getManager().getMemoryStore().get(opPlayer.get());
        var bukkitSide = event.isFrontSide() ? Side.FRONT : Side.BACK;

        var bukkitEvent = new SignChangeEvent(bukkitBlock, bukkitPlayer, event.text().all(), bukkitSide);
        fireEvent(bukkitEvent);
        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        var newLines = bukkitEvent.lines();
        event.text().set(newLines);
    }
}
