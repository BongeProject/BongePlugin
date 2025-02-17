package org.soak.map.event.data;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;

public class SoakToggleSprintEvent extends AbstractDataEvent<Boolean, PlayerToggleSprintEvent> {

    public SoakToggleSprintEvent(Class<PlayerToggleSprintEvent> bukkitEvent, EventPriority priority, Plugin plugin,
                                 Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    public Key<? extends Value<Boolean>> keyValue() {
        return Keys.IS_SPRINTING;
    }

    @Override
    protected boolean isCorrectHolder(DataHolder.Mutable holder) {
        return holder instanceof ServerPlayer;
    }

    @Override
    protected void fireEvent(ChangeDataHolderEvent.ValueChange spongeEvent, DataHolder.Mutable spongePlayer,
                             Boolean changedTo, Boolean changedFrom) {
        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get((ServerPlayer) spongePlayer);
        var event = new PlayerToggleSprintEvent(player, changedTo);
        fireEvent(event);

        if (event.isCancelled()) {
            spongeEvent.setCancelled(event.isCancelled());
        }
    }
}
