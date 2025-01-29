package org.soak.generate.bukkit;

import org.spongepowered.api.item.inventory.Slot;

import java.util.function.Predicate;

public class SlotTypeEntry {

    private final String enumId;
    private final Predicate<Slot> type;

    public SlotTypeEntry(String enumId, Predicate<Slot> type) {
        this.type = type;
        this.enumId = enumId;
    }

    public String id() {
        return this.enumId;
    }

    public boolean is(Slot slot) {
        return type.test(slot);
    }

    public <T extends Enum<T>> T toType() {
        return (T) SlotTypeList.values().stream().filter(t -> t.name().equals(this.id())).findFirst().orElseThrow();
    }
}
