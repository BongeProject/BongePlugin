package org.soak.generate.bukkit;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.slot.EquipmentSlot;
import org.spongepowered.api.item.inventory.slot.OutputSlot;

import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Predicate;

public class SlotTypeList {

    public static final Collection<SlotTypeEntry> SLOT_TYPE_MAPPINGS = new LinkedTransferQueue<>();
    public static Class<? extends Enum<?>> LOADED_CLASS;

    public static final SlotTypeEntry ARMOR = register("ARMOR", EquipmentSlot.class);
    public static final SlotTypeEntry CRAFTING = register("CRAFTING", slot -> {
        return slot.parent().getClass().getSimpleName().contains("Chest");
    });
    public static final SlotTypeEntry RESULT = register("RESULT", slot -> slot instanceof OutputSlot);

    public static final SlotTypeEntry CONTAINER = register("CONTAINER", Slot.class);

    private static SlotTypeEntry register(String id, Class<? extends Slot> clazz) {
        return register(id, clazz::isInstance);
    }

    private static SlotTypeEntry register(String id, Predicate<Slot> check) {
        var result = new SlotTypeEntry(id, check);
        SLOT_TYPE_MAPPINGS.add(result);
        return result;
    }

    public static <T> DynamicType.Builder<T> createSlotTypeList() throws Exception {
        var slotTypes = SLOT_TYPE_MAPPINGS.stream().map(SlotTypeEntry::id).toList();

        return (DynamicType.Builder<T>) new ByteBuddy().makeEnumeration(slotTypes)
                .name("org.bukkit.event.inventory.InventoryType$SlotType");
    }

    public static <T extends Enum<T>> EnumSet<T> values() {
        if (LOADED_CLASS == null) {
            throw new RuntimeException("EntityTypeList.LOADED_CLASS must be set");
        }
        return EnumSet.allOf((Class<T>) LOADED_CLASS);
    }

    public static <T extends Enum<T>> T value(Slot container) {
        return (T) SLOT_TYPE_MAPPINGS.stream()
                .filter(entry -> entry.is(container))
                .findFirst()
                .map(entry -> entry.toType())
                .orElseThrow();
    }
}
