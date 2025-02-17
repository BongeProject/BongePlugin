package org.soak.map.event.world;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.world.UnloadWorldEvent;

public class SoakWorldUnloadEvent extends SoakEvent<UnloadWorldEvent, WorldUnloadEvent> {

    public SoakWorldUnloadEvent(Class<WorldUnloadEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<UnloadWorldEvent> spongeEventClass() {
        return UnloadWorldEvent.class;
    }

    @Override
    public void handle(UnloadWorldEvent spongeEvent) throws Exception {
        var bukkitWorld = SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongeEvent.world());
        var bukkitEvent = new WorldUnloadEvent(bukkitWorld);
        fireEvent(bukkitEvent);
        //TODO -> Bukkit event is able to be cancelled, sponge is not
    }
}
