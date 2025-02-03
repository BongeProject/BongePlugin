package org.soak.map.event.block;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlock;
import org.soak.wrapper.block.SoakBlockSnapshot;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.tag.BlockTypeTags;
import org.spongepowered.api.world.LocatableBlock;

public class SoakBlockBurnEvent extends SoakEvent<ChangeBlockEvent.All, BlockBurnEvent> {

    public SoakBlockBurnEvent(Class<BlockBurnEvent> bukkitEvent, EventPriority priority, Plugin plugin,
                              Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ChangeBlockEvent.All> spongeEventClass() {
        return ChangeBlockEvent.All.class;
    }

    @Override
    public void handle(ChangeBlockEvent.All spongeEvent) {
        var opCausedBy = spongeEvent.cause().first(LocatableBlock.class);
        if (opCausedBy.isEmpty()) {
            return;
        }
        var causedBy = opCausedBy.get();

        if (!causedBy.blockState().type().is(BlockTypeTags.FIRE)) {
            return;
        }
        var changed = spongeEvent.transactions(Operations.BREAK.get()).toList();
        if (changed.isEmpty()) {
            return;
        }

        var fireBukkitBlock = new SoakBlock(causedBy.serverLocation());
        for (var transaction : changed) {
            var burntBukkitBlock = new SoakBlockSnapshot(transaction.original());
            BlockBurnEvent bukkitEvent = new BlockBurnEvent(burntBukkitBlock, fireBukkitBlock);
            fireEvent(bukkitEvent);
            if (bukkitEvent.isCancelled()) {
                transaction.invalidate();
            }
        }
    }
}
