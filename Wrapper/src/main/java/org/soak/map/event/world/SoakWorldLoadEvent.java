package org.soak.map.event.world;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.world.LoadWorldEvent;

public class SoakWorldLoadEvent extends SoakEvent<LoadWorldEvent, WorldLoadEvent> {

    public SoakWorldLoadEvent(Class<WorldLoadEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<LoadWorldEvent> spongeEventClass() {
        return LoadWorldEvent.class;
    }

    @Override
    public void handle(LoadWorldEvent spongeEvent) throws Exception {
        var bukkitWorld = SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongeEvent.world());
        var bukkitEvent = new WorldLoadEvent(bukkitWorld);
        fireEvent(bukkitEvent);
        //TODO -> Bukkit event is able to be cancelled, sponge is not
    }
}
