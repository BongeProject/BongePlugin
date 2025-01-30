package org.soak.map.event.inventory.action.furnance;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.map.item.SoakItemStackMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlock;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.entity.CookingEvent;

public class SoakBurnItemEvent extends SoakEvent<CookingEvent.ConsumeFuel, FurnaceBurnEvent> {

    public SoakBurnItemEvent(Class<FurnaceBurnEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    public void handle(CookingEvent.ConsumeFuel event) throws Exception {
        if (event.fuel().isEmpty()) {
            //How does this happen?
            return;
        }
        var furnace = new SoakBlock(event.blockEntity()
                .locatableBlock()
                .location()
                .onServer()
                .orElseThrow(() -> new RuntimeException("Location for server could not be created")));
        var consumedItem = SoakItemStackMap.toBukkit(event.fuel().get());
        var burnTime = event.fuel().get().get(Keys.BURN_TIME); //probably not right
        var bukkitEvent = new FurnaceBurnEvent(furnace, consumedItem, burnTime.orElse(0));
        fireEvent(bukkitEvent);
        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
