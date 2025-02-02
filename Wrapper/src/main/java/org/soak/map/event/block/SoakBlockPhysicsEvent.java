package org.soak.map.event.block;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlockSnapshot;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;

public class SoakBlockPhysicsEvent extends SoakEvent<ChangeBlockEvent.All, BlockPhysicsEvent> {

    public SoakBlockPhysicsEvent(Class<BlockPhysicsEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ChangeBlockEvent.All> spongeEventClass() {
        return ChangeBlockEvent.All.class;
    }

    @Override
    public void handle(ChangeBlockEvent.All spongeEvent) {
        spongeEvent.transactions(Operations.MODIFY.get()).forEach(transaction -> { //used modify .... may not be correct
            var original = new SoakBlockSnapshot(transaction.original());
            var newBlock = new SoakBlockSnapshot(transaction.custom().orElseGet(transaction::finalReplacement));
            var bukkitEvent = new BlockPhysicsEvent(original, newBlock.getBlockData()); //todo get source block
            fireEvent(bukkitEvent);
            if (bukkitEvent.isCancelled()) {
                transaction.invalidate();
            }
            //TODO cancel drops
        });
    }
}
