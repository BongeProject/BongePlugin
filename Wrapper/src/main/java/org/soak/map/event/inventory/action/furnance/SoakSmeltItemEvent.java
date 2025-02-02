package org.soak.map.event.inventory.action.furnance;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.map.item.SoakItemStackMap;
import org.soak.map.item.SoakRecipeMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlock;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.entity.CookingEvent;

import java.util.List;

public class SoakSmeltItemEvent extends SoakEvent<CookingEvent.Finish, FurnaceSmeltEvent> {

    public SoakSmeltItemEvent(Class<FurnaceSmeltEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<CookingEvent.Finish> spongeEventClass() {
        return CookingEvent.Finish.class;
    }

    @Override
    public void handle(CookingEvent.Finish event) throws Exception {
        var furnace = new SoakBlock(event.blockEntity()
                .locatableBlock()
                .location()
                .onServer()
                .orElseThrow(() -> new RuntimeException("Location for server could not be created")));
        var original = event.recipe()
                .map(cooking -> cooking.ingredient().displayedItems())
                .filter(items -> items.size() == 1)
                .map(List::getFirst)
                .map(SoakItemStackMap::toBukkit)
                .orElseThrow(() -> new RuntimeException("Could not find the before item"));
        var recipe = (CookingRecipe<?>) event.recipe().map(SoakRecipeMap::toBukkit).orElse(null);
        if (event.transactions().size() != 1) {
            throw new RuntimeException("Furnace cooked " + event.transactions().size() + " itemstacks");
        }
        var result = SoakItemStackMap.toBukkit(event.transactions().getFirst().finalReplacement());
        var bukkitEvent = new FurnaceSmeltEvent(furnace, original, result, recipe);
        fireEvent(bukkitEvent);

        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
