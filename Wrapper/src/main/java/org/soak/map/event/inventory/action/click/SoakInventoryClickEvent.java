package org.soak.map.event.inventory.action.click;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.map.item.inventory.SoakInventoryMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.inventory.view.AbstractInventoryView;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent;

public class SoakInventoryClickEvent extends SoakEvent<ClickContainerEvent, InventoryClickEvent> {

    public SoakInventoryClickEvent(Class<InventoryClickEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ClickContainerEvent> spongeEventClass() {
        return ClickContainerEvent.class;
    }

    private InventoryAction mapAction(ClickContainerEvent event) {
        //TODO
        //this is horrible -> i understand why .... but still
        if (event instanceof ClickContainerEvent.Shift) {
            return InventoryAction.MOVE_TO_OTHER_INVENTORY;
        }
        if (event instanceof ClickContainerEvent.Drop.Full) {
            return InventoryAction.DROP_ALL_SLOT;
        }
        if (event instanceof ClickContainerEvent.Drop.Single) {
            return InventoryAction.DROP_ALL_SLOT;
        }
        if (event instanceof ClickContainerEvent.Primary) {
            var curserTransaction = event.cursorTransaction();
            var cursorAfter = curserTransaction.finalReplacement();
            var cursorBefore = curserTransaction.original();
            if (cursorBefore.isEmpty() && cursorAfter.isEmpty()) {
                return InventoryAction.NOTHING;
            }
            if (!event.transactions().isEmpty()) {
                var slotAfter = event.transactions().get(0).finalReplacement();
                if (cursorBefore.isEmpty()) {
                    if (slotAfter.equals(cursorBefore)) {
                        return InventoryAction.PICKUP_ALL;
                    }
                    if ((slotAfter.quantity() / 2) == cursorAfter.quantity()) {
                        return InventoryAction.PICKUP_HALF;
                    }
                    if (cursorAfter.quantity() == 1) {
                        return InventoryAction.PICKUP_ONE;
                    }
                    return InventoryAction.PICKUP_SOME;
                }

            }
        }

        return InventoryAction.UNKNOWN;
    }

    private ClickType mapClickType(ClickContainerEvent event) {
        if (event instanceof ClickContainerEvent.Shift.Secondary) {
            return ClickType.SHIFT_LEFT;
        }
        if (event instanceof ClickContainerEvent.Shift.Primary) {
            return ClickType.SHIFT_RIGHT;
        }
        if (event instanceof ClickContainerEvent.Recipe) {
            return ClickType.UNKNOWN;
        }
        if (event instanceof ClickContainerEvent.Creative) {
            return ClickType.CREATIVE;
        }
        if (event instanceof ClickContainerEvent.Double) {
            return ClickType.DOUBLE_CLICK;
        }
        if (event instanceof ClickContainerEvent.Drag) {
            return ClickType.UNKNOWN;
        }
        if (event instanceof ClickContainerEvent.Drop) {
            return ClickType.DROP;
        }
        if (event instanceof ClickContainerEvent.Middle) {
            return ClickType.MIDDLE;
        }
        if (event instanceof ClickContainerEvent.NumberPress) {
            return ClickType.NUMBER_KEY;
        }
        if (event instanceof ClickContainerEvent.SelectTrade) {
            return ClickType.UNKNOWN;
        }
        if (event instanceof ClickContainerEvent.Primary) {
            return ClickType.RIGHT;
        }
        if (event instanceof ClickContainerEvent.Secondary) {
            return ClickType.LEFT;
        }
        return ClickType.UNKNOWN;
    }

    @Override
    public void handle(ClickContainerEvent event) throws Exception {
        var clickType = mapClickType(event);
        if (clickType == ClickType.UNKNOWN) {
            return;
        }
        var slot = event.slot();
        if (slot.isEmpty()) {
            return;
        }
        var opSlotIndex = slot.get().get(Keys.SLOT_INDEX);
        if (opSlotIndex.isEmpty()) {
            return;
        }
        var inventoryView = AbstractInventoryView.wrap(event.container());
        var action = mapAction(event);
        var slotType = SoakInventoryMap.toBukkit(slot.get());


        var bukkitEvent = new InventoryClickEvent(inventoryView, slotType, opSlotIndex.get(), clickType, action);
        fireEvent(bukkitEvent);
        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
