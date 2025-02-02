package org.soak.map.event.inventory.action.craft;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.SoakActionMap;
import org.soak.map.event.SoakEvent;
import org.soak.map.item.SoakRecipeMap;
import org.soak.map.item.inventory.SoakInventoryMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.inventory.view.AbstractInventoryView;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.CraftItemEvent;

public class SoakCraftItemEvent extends SoakEvent<CraftItemEvent.Preview, org.bukkit.event.inventory.CraftItemEvent> {

    public SoakCraftItemEvent(Class<org.bukkit.event.inventory.CraftItemEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<CraftItemEvent.Preview> spongeEventClass() {
        return CraftItemEvent.Preview.class;
    }

    @Override
    public void handle(CraftItemEvent.Preview event) throws Exception {
        var opPlayer = event.cause().first(ServerPlayer.class);
        if (opPlayer.isEmpty()) {
            return;
        }
        var player = opPlayer.get();

        var spongeRecipe = event.recipe().orElseThrow(() -> new IllegalStateException("CraftItemEvent fired but no recipe found"));
        var bukkitRecipe = SoakRecipeMap.toBukkit(spongeRecipe);
        var inventoryView = AbstractInventoryView.wrap(player.openInventory().orElseThrow(() -> new IllegalStateException("CraftItemEvent fired but player's inventory isnt open")));
        var slot = event.slot().orElseThrow(() -> new RuntimeException("Cannot get slot"));
        var slotIndex = slot.get(Keys.SLOT_INDEX).orElseThrow(() -> new RuntimeException("Cannot get slot index"));
        var slotType = SoakInventoryMap.toBukkit(slot);
        var clickType = ClickType.DROP; //TODO
        var action = InventoryAction.DROP_ONE_SLOT; //TODO

        var bEvent = new org.bukkit.event.inventory.CraftItemEvent(bukkitRecipe, inventoryView, slotType, slotIndex, clickType, action);
        fireEvent(bEvent);
    }
}
