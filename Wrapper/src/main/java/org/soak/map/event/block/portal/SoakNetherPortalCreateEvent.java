package org.soak.map.event.block.portal;

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
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.item.ItemTypes;

public class SoakNetherPortalCreateEvent extends SoakEvent<ChangeBlockEvent.All, PortalCreateEvent> {

    public SoakNetherPortalCreateEvent(Class<PortalCreateEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ChangeBlockEvent.All> spongeEventClass() {
        return ChangeBlockEvent.All.class;
    }

    @Override
    public void handle(ChangeBlockEvent.All spongeEvent) {
        var entity = spongeEvent.cause().first(Entity.class).map(AbstractEntity::wrap).orElse(null);
        var blocks = spongeEvent
                .transactions(Operations.PLACE.get())
                .filter(transaction -> transaction.finalReplacement().state().type().equals(BlockTypes.NETHER_PORTAL.get()))
                .map(Transaction::finalReplacement)
                .map(blockSnapshot -> new SoakBlockSnapshot(blockSnapshot).getState())
                .toList();
        if (blocks.isEmpty()) {
            return;
        }
        var world = blocks.getFirst().getWorld();
        var cause = spongeEvent
                .context()
                .get(EventContextKeys.USED_ITEM)
                .filter(item ->
                        item.type().equals(ItemTypes.FLINT_AND_STEEL.get()) ||
                                item.type().equals(ItemTypes.FIRE_CHARGE.get()))
                .map(snapshot -> PortalCreateEvent.CreateReason.FIRE)
                .orElse(PortalCreateEvent.CreateReason.NETHER_PAIR);

        var event = new PortalCreateEvent(blocks, world, entity, cause);
        fireEvent(event);
        if (event.isCancelled()) {
            spongeEvent.setCancelled(true);
        }
    }
}
