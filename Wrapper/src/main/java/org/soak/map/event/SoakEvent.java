package org.soak.map.event;

import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.SoakOrderMap;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.EventListenerRegistration;

public abstract class SoakEvent<E extends Event, BE extends org.bukkit.event.Event> extends AbstractSoakEvent<BE> implements EventListener<E> {

    public SoakEvent(Class<BE> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }
    
    protected abstract Class<E> spongeEventClass();
    
    public EventListenerRegistration<E> spongeEvent(){
        var spongeContainer = SoakManager.<WrapperManager>getManager().getSoakContainer(this.plugin()).getTrueContainer();
        var order = SoakOrderMap.toSponge(this.priority());
        var spongeClass = spongeEventClass();
        
        return EventListenerRegistration.builder(spongeClass).plugin(spongeContainer).order(order).listener(this).build();
    }
}
