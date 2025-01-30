package org.soak.map.event;

import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;

public abstract class SoakEvent<E extends Event, BE extends org.bukkit.event.Event> extends AbstractSoakEvent<BE> implements EventListener<E> {

    public SoakEvent(Class<BE> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }
}
