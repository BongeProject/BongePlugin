package org.soak.map.event.data;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.map.event.SoakEvent;
import org.soak.wrapper.entity.AbstractEntity;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;

public class SoakEntityAirChangeEvent extends AbstractDataEvent<Integer, EntityAirChangeEvent> {

    public SoakEntityAirChangeEvent(Class<EntityAirChangeEvent> bukkitEvent, EventPriority priority, Plugin plugin,
                                    Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    public Key<? extends Value<Integer>> keyValue() {
        return Keys.REMAINING_AIR;
    }

    @Override
    protected boolean isCorrectHolder(DataHolder.Mutable holder) {
        return holder instanceof Entity;
    }

    @Override
    protected void fireEvent(ChangeDataHolderEvent.ValueChange spongeEvent, DataHolder.Mutable player,
                             Integer changedTo, Integer changedFrom) {
        var entity = AbstractEntity.wrap((Entity) player);
        var bukkitEvent = new EntityAirChangeEvent(entity, changedTo);
        fireEvent(bukkitEvent);
        if (bukkitEvent.isCancelled()) {
            spongeEvent.setCancelled(true);
            return;
        }
        if (bukkitEvent.getAmount() == changedFrom) {
            spongeEvent.proposeChanges(DataTransactionResult.builder()
                                               .success(Value.immutableOf(keyValue(), bukkitEvent.getAmount()))
                                               .build());
        }
    }

}
