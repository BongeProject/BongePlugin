package org.soak.map.event.block.portal;

import org.bukkit.block.BlockState;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlockSnapshot;
import org.soak.wrapper.entity.AbstractEntity;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.util.List;

public class SoakEndPortalCreateEvent extends SoakEvent<ChangeBlockEvent.All, PortalCreateEvent> {

    public SoakEndPortalCreateEvent(Class<PortalCreateEvent> bukkitEvent, EventPriority priority, Plugin plugin,
                                    Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ChangeBlockEvent.All> spongeEventClass() {
        return ChangeBlockEvent.All.class;
    }

    @Override
    public void handle(ChangeBlockEvent.All spongeEvent) {
        var entity = spongeEvent.cause().first(Entity.class).map(AbstractEntity::wrap).orElse(null);
        List<BlockState> blocks = spongeEvent.transactions(Operations.PLACE.get())
                .filter(transaction -> transaction.finalReplacement()
                        .state()
                        .type()
                        .equals(BlockTypes.END_PORTAL.get()))
                .map(Transaction::finalReplacement)
                .map(SoakBlockSnapshot::new)
                .map(SoakBlockSnapshot::getState)
                .toList();
        if (blocks.isEmpty()) {
            return;
        }
        var world = blocks.getFirst().getWorld();

        var event = new PortalCreateEvent(blocks, world, entity, PortalCreateEvent.CreateReason.END_PLATFORM);
        fireEvent(event);
        if (event.isCancelled()) {
            spongeEvent.setCancelled(true);
        }
    }
}
