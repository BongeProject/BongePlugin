package org.soak.map.event.block;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlockSnapshot;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

public class SoakBlockBreakEvent extends SoakEvent<ChangeBlockEvent.All, BlockBreakEvent> {

    public SoakBlockBreakEvent(Class<BlockBreakEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ChangeBlockEvent.All> spongeEventClass() {
        return ChangeBlockEvent.All.class;
    }

    @Override
    public void handle(ChangeBlockEvent.All spongeEvent) {
        var opSpongePlayer = spongeEvent.cause().first(ServerPlayer.class);
        if (opSpongePlayer.isEmpty()) {
            return;
        }
        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get(opSpongePlayer.get());
        spongeEvent.transactions(Operations.BREAK.get()).forEach(transaction -> {
            var block = new SoakBlockSnapshot(transaction.original());
            var bukkitEvent = new BlockBreakEvent(block, player);
            fireEvent(bukkitEvent);
            if (bukkitEvent.isCancelled()) {
                transaction.invalidate();
            }
            //TODO cancel drops
        });
    }
}
