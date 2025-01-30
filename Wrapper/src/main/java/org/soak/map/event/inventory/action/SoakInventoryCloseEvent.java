package org.soak.map.event.inventory.action;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.inventory.view.AbstractInventoryView;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.item.inventory.container.InteractContainerEvent;

public class SoakInventoryCloseEvent extends SoakEvent<InteractContainerEvent.Close, InventoryCloseEvent> {

    public SoakInventoryCloseEvent(Class<InventoryCloseEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    public void handle(InteractContainerEvent.Close event) throws Exception {
        var inventoryView = AbstractInventoryView.wrap(event.container());
        InventoryCloseEvent bukkitEvent = new InventoryCloseEvent(inventoryView);
        fireEvent(bukkitEvent);
    }
}
