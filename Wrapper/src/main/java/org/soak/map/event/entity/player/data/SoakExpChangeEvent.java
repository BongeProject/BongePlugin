package org.soak.map.event.entity.player.data;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.living.Humanoid;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;

public class SoakExpChangeEvent extends AbstractDataEvent<Integer, PlayerExpChangeEvent> {

    public SoakExpChangeEvent(Class<PlayerExpChangeEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ChangeDataHolderEvent.ValueChange> spongeEventClass() {
        return ChangeDataHolderEvent.ValueChange.class;
    }

    @Override
    public Key<? extends Value<Integer>> keyValue() {
        return Keys.EXPERIENCE;
    }

    @Override
    protected boolean isCorrectHolder(DataHolder.Mutable holder) {
        return holder instanceof Humanoid;
    }

    @Override
    protected void fireEvent(ChangeDataHolderEvent.ValueChange spongeEvent, DataHolder.Mutable player, Integer changedTo, Integer changedFrom) {
        var human = SoakManager.<WrapperManager>getManager().getMemoryStore().get((ServerPlayer) player);
        //TODO get entity that caused exp
        var event = new PlayerExpChangeEvent(human, changedTo);

        fireEvent(event);
    }
}
