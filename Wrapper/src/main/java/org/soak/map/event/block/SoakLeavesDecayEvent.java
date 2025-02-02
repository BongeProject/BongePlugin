package org.soak.map.event.block;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.map.event.SoakEvent;
import org.soak.wrapper.block.SoakBlockSnapshot;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.tag.BlockTypeTags;

public class SoakLeavesDecayEvent extends SoakEvent<ChangeBlockEvent.All, LeavesDecayEvent> {

    public SoakLeavesDecayEvent(Class<LeavesDecayEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ChangeBlockEvent.All> spongeEventClass() {
        return ChangeBlockEvent.All.class;
    }

    @Override
    public void handle(ChangeBlockEvent.All event) throws Exception {
        var leaveTransactions = event.transactions(Operations.DECAY.get()).filter(transaction -> transaction.original().state().type().is(BlockTypeTags.LEAVES)).toList();
        for (var leaveTransaction : leaveTransactions) {
            var bukkitBlock = new SoakBlockSnapshot(leaveTransaction.original());
            var bukkitEvent = new LeavesDecayEvent(bukkitBlock);
            fireEvent(bukkitEvent);
            if (bukkitEvent.isCancelled()) {
                leaveTransaction.invalidate();
            }
        }
    }
}
