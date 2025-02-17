package org.soak.map.item.inventory;

import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.soak.WrapperManager;
import org.soak.generate.bukkit.InventoryTypeList;
import org.soak.generate.bukkit.SlotTypeList;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.inventory.SoakInventory;
import org.soak.wrapper.inventory.carrier.SoakPlayerInventory;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.*;

public class SoakInventoryMap {

    public static InventoryType toBukkit(Container container) {
        return InventoryTypeList.value(container);
    }

    public static InventoryType.SlotType toBukkit(Slot slot) {
        return SlotTypeList.value(slot);
    }

    public static @NotNull SoakInventory<?> toBukkit(Carrier carrier) {
        if (carrier instanceof ServerPlayer player) {
            return (SoakPlayerInventory) SoakManager.<WrapperManager>getManager().getMemoryStore().get(player).getInventory();
        }
        return SoakInventory.wrap(carrier.inventory());
    }
}
