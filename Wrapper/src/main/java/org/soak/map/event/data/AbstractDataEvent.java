package org.soak.map.event.data;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.map.event.SoakEvent;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;

public abstract class AbstractDataEvent<T, BE extends Event> extends SoakEvent<ChangeDataHolderEvent.ValueChange, BE> {

    public AbstractDataEvent(Class<BE> bukkitEvent, EventPriority priority, Plugin plugin,
                             org.bukkit.event.Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    public abstract Key<? extends Value<T>> keyValue();

    protected abstract boolean isCorrectHolder(DataHolder.Mutable holder);

    @Override
    public void handle(ChangeDataHolderEvent.ValueChange event) {
        var holder = event.targetHolder();
        if (!isCorrectHolder(holder)) {
            return;
        }
        var result = event.endResult();
        var opData = result.successfulData()
                .stream()
                .filter(immutable -> immutable.key().equals(keyValue()))
                .findAny()
                .map(imm -> (T)imm.get());
        if (opData.isEmpty()) {
            return;
        }
        var changedTo = opData.get();
        var original = event.originalChanges().successfulValue(keyValue()).map(Value::get).orElse(null);

        fireEvent(event, holder, changedTo, original);
    }

    @Override
    protected Class<ChangeDataHolderEvent.ValueChange> spongeEventClass() {
        return ChangeDataHolderEvent.ValueChange.class;
    }

    protected abstract void fireEvent(ChangeDataHolderEvent.ValueChange spongeEvent, DataHolder.Mutable player,
                                      T changedTo, T changedFrom);
}
