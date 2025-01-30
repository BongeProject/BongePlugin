package org.soak.map.event;

import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.plugin.SoakManager;
import org.soak.utils.BasicEntry;

public class AbstractSoakEvent<BE extends Event> {
    private final EventPriority priority;
    private final Plugin plugin;
    private final Listener listener;
    private final EventExecutor executor;
    private final Class<BE> bukkitEvent;
    private final boolean ignoreCancelled;

    public AbstractSoakEvent(Class<BE> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        this.plugin = plugin;
        this.priority = priority;
        this.listener = listener;
        this.bukkitEvent = bukkitEvent;
        this.executor = executor;
        this.ignoreCancelled = ignoreCancelled;
    }

    public EventPriority priority() {
        return priority;
    }

    public Plugin plugin() {
        return plugin;
    }

    public Listener listener() {
        return listener;
    }

    public Class<BE> bukkitEvent() {
        return bukkitEvent;
    }

    public void fireEvent(BE event) {
        if (!this.ignoreCancelled && event instanceof Cancellable cancellable && cancellable.isCancelled()) {
            return;
        }
        try {
            executor.execute(this.listener, event);
        } catch (EventException e) {
            SoakManager.getManager().displayError(e, this.plugin, new BasicEntry<>("event", event.getEventName()));
        }
    }
}
