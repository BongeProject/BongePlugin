package org.soak.map.event.block;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlockSnapshot;
import org.soak.wrapper.entity.AbstractEntity;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

public class SoakBlockPlaceByEntityEvent extends SoakEvent<ChangeBlockEvent.All, EntityBlockFormEvent> {

    public SoakBlockPlaceByEntityEvent(Class<EntityBlockFormEvent> bukkitEvent, EventPriority priority, Plugin plugin
            , Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ChangeBlockEvent.All> spongeEventClass() {
        return ChangeBlockEvent.All.class;
    }

    @Override
    public void handle(ChangeBlockEvent.All spongeEvent) throws Exception {
        var opSpongeEntity = spongeEvent.cause().first(Entity.class);
        if (opSpongeEntity.isEmpty()) {
            return;
        }
        if (!(opSpongeEntity.get() instanceof Player)) {
            return;
        }
        var entity = AbstractEntity.wrap(opSpongeEntity.get());

        spongeEvent.transactions(Operations.PLACE.get()).forEach(transaction -> {
            var originalBlock = new SoakBlockSnapshot(transaction.original());
            var newBlock = new SoakBlockSnapshot(transaction.custom().orElseGet(transaction::finalReplacement));

            var bukkitEvent = new EntityBlockFormEvent(entity, originalBlock, newBlock.getState());
            fireEvent(bukkitEvent);
            if (bukkitEvent.isCancelled()) {
                transaction.invalidate();
            }
        });
    }
}
