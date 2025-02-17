package org.soak.utils;

import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ContainerType;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.registry.DefaultedRegistryReference;

public class InventoryHelper {

    public enum VanillaInventoryIds {
        PLAYER_INVENTORY(0);

        private final int id;

        VanillaInventoryIds(int id) {
            this.id = id;
        }

        public int id() {
            return this.id;
        }

        public boolean isInventory(Inventory inventory) {
            Inventory target = inventory;
            while (target != null) {
                if (target instanceof Container container) {
                    return isContainer(container);
                }
                target = target.parent();
            }
            System.err.println("Cannot find container from " + inventory.getClass().getName());
            return false;
        }

        public boolean isContainer(Container container) {
            try {
                int containerId = ReflectionHelper.getField(container, "containerId");
                return containerId == id();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return false;
            }
        }
    }

    public static DefaultedRegistryReference<ContainerType> toChestContainerType(int row) {
        return switch (row) {
            case 1 -> ContainerTypes.GENERIC_9X1;
            case 2 -> ContainerTypes.GENERIC_9X2;
            case 3 -> ContainerTypes.GENERIC_9X3;
            case 4 -> ContainerTypes.GENERIC_9X4;
            case 5 -> ContainerTypes.GENERIC_9X5;
            case 6 -> ContainerTypes.GENERIC_9X6;
            default -> throw new IndexOutOfBoundsException("Unknown chest size of " + row);
        };
    }
}
