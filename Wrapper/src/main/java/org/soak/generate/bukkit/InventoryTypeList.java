package org.soak.generate.bukkit;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.kyori.adventure.text.Component;
import org.soak.utils.InventoryHelper;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.concurrent.LinkedTransferQueue;

/*
This is not a direct 1-1 mapping

Sponge's ContainerType are the types of inventories that can be displayed, so the Hopper and Dropper both have the
same GUI, therefore come under the same ContainerType
However Bukkit has each of the vanilla's inventories that a player can typically access, however does not have the
hidden
containers, such as GENERIC_9X1 .... there is some messy code to duplicate the vanilla containers where needed and
filter
out the Sponge ContainerTypes where bukkit doesn't have it. Despite that, modded ContainerTypes should work as intended
 */

public class InventoryTypeList {

    public static Class<? extends Enum<?>> LOADED_CLASS;
    public static final Collection<InventoryTypeEntry> INVENTORY_TYPE_MAPPINGS = new LinkedTransferQueue<>();

    public static final InventoryTypeEntry CREATIVE = register(new InventoryTypeEntry("CREATIVE",
                                                                                      InventoryHelper.VanillaInventoryIds.PLAYER_INVENTORY::isContainer).setDefaultName(
            Component::empty));

    private static InventoryTypeEntry register(InventoryTypeEntry entry) {
        INVENTORY_TYPE_MAPPINGS.add(entry);
        return entry;
    }

    public static DynamicType.Unloaded<? extends Enum<?>> createInventoryTypeList() throws Exception {
        var containerTypeIterator = ContainerTypes.registry().stream().iterator();
        var containerTypes = new HashSet<>(INVENTORY_TYPE_MAPPINGS.stream().map(InventoryTypeEntry::enumId).toList());

        while (containerTypeIterator.hasNext()) {
            var containerType = containerTypeIterator.next();
            var key = containerType.key(RegistryTypes.CONTAINER_TYPE);
            var name = CommonGenerationCode.toName(key);
            if (name.startsWith("GENERIC_9X") && !(name.equals("GENERIC_9X3") || name.equals("GENERIC_9X6"))) {
                continue;
            }
            if (name.equals("CRAFTER_3X3")) {
                name = "WORKBENCH";
            }
            if (name.equals("BREWING_STAND")) {
                name = "BREWING";
            }
            if (name.equals("ENCHANTMENT")) {
                name = "ENCHANTING";
            }
            if (name.equals("CARTOGRAPHY_TABLE")) {
                name = "CARTOGRAPHY";
            }
            containerTypes.add(name);
            INVENTORY_TYPE_MAPPINGS.add(InventoryTypeEntry.fromContainerType(name, containerType));
        }

        var classCreator = new ByteBuddy().makeEnumeration(containerTypes)
                .name("org.bukkit.event.inventory.InventoryType");

        //register methods
        classCreator = createDefaultTitle(classCreator);

        var slotTypeCreator = SlotTypeList.createSlotTypeList()
                .innerTypeOf(classCreator.toTypeDescription())
                .asMemberType();
        return classCreator.declaredTypes(slotTypeCreator.toTypeDescription()).make().include(slotTypeCreator.make());
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> createDefaultTitle(DynamicType.Builder<? extends Enum<?>> classCreator)
            throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(InventoryTypeList.class, classCreator, "defaultTitle", Component.class);
    }

    public static <T extends Enum<T>> EnumSet<T> values() {
        if (LOADED_CLASS == null) {
            throw new RuntimeException("EntityTypeList.LOADED_CLASS must be set");
        }
        //noinspection unchecked
        return EnumSet.allOf((Class<T>) LOADED_CLASS);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T value(Container container) {
        return (T) INVENTORY_TYPE_MAPPINGS.stream()
                .filter(entry -> entry.fromContainer().test(container))
                .findFirst()
                .map(InventoryTypeEntry::toType)
                .orElseThrow();
    }

    public static InventoryTypeEntry getTypeMapping(Enum<?> enumEntry) {
        return INVENTORY_TYPE_MAPPINGS.stream()
                .filter(entry -> entry.enumId().equals(enumEntry.name()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find entry of " + enumEntry.name()));
    }

    public static Component defaultTitle(Enum<?> enumEntry) {
        return getTypeMapping(enumEntry).defaultName();
    }
}