package org.soak.map.event.block;

import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.soak.WrapperManager;
import org.soak.map.SoakBlockMap;
import org.soak.map.event.EventSingleListenerWrapper;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlock;
import org.soak.wrapper.block.SoakBlockSnapshot;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.tag.BlockTypeTags;
import org.spongepowered.api.world.LocatableBlock;

import java.util.stream.Collectors;

public class SoakBlockBurnEvent {

    private final EventSingleListenerWrapper<BlockBurnEvent> singleEventListener;

    public SoakBlockBurnEvent(EventSingleListenerWrapper<BlockBurnEvent> wrapper) {
        this.singleEventListener = wrapper;
    }

    @Listener(order = Order.FIRST)
    public void firstEvent(ChangeBlockEvent.All spongeEvent, @First LocatableBlock causedBy) {
        fireEvent(spongeEvent, causedBy, EventPriority.HIGHEST);
    }

    @Listener(order = Order.EARLY)
    public void earlyEvent(ChangeBlockEvent.All spongeEvent, @First LocatableBlock causedBy) {
        fireEvent(spongeEvent, causedBy, EventPriority.HIGH);
    }

    @Listener(order = Order.DEFAULT)
    public void normalEvent(ChangeBlockEvent.All spongeEvent, @First LocatableBlock causedBy) {
        fireEvent(spongeEvent, causedBy, EventPriority.NORMAL);
    }

    @Listener(order = Order.LATE)
    public void lateEvent(ChangeBlockEvent.All spongeEvent, @First LocatableBlock causedBy) {
        fireEvent(spongeEvent, causedBy, EventPriority.LOW);
    }

    @Listener(order = Order.LAST)
    public void lastEvent(ChangeBlockEvent.All spongeEvent, @First LocatableBlock causedBy) {
        fireEvent(spongeEvent, causedBy, EventPriority.LOWEST);
    }

    private void fireEvent(ChangeBlockEvent.All spongeEvent, LocatableBlock causedBy, EventPriority priority) {
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

            SoakManager.<WrapperManager>getManager().getServer().getSoakPluginManager().callEvent(this.singleEventListener, bukkitEvent, priority);
            if (bukkitEvent.isCancelled()) {
                transaction.invalidate();
            }
        }
    }
}
