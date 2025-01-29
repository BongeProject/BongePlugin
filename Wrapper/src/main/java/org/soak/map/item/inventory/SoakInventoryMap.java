package org.soak.map.item.inventory;

import org.bukkit.event.inventory.InventoryType;
import org.soak.generate.bukkit.InventoryTypeList;
import org.soak.generate.bukkit.SlotTypeList;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ContainerType;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.Slot;

public class SoakInventoryMap {

    public static InventoryType toBukkit(Container container) {
        return InventoryTypeList.value(container);
    }

    public static InventoryType.SlotType toBukkit(Slot slot) {
        return SlotTypeList.value(slot);
    }
}
