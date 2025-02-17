package org.soak.map.item;

import org.bukkit.inventory.ItemFlag;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;

public class SoakItemFlagMap {

    public static ItemFlag toBukkit(Key<?> key) {
        if (Keys.HIDE_ENCHANTMENTS.equals(key)) {
            return ItemFlag.HIDE_ENCHANTS;
        }
        if (Keys.HIDE_ATTRIBUTES.equals(key)) {
            return ItemFlag.HIDE_ATTRIBUTES;
        }
        if (Keys.HIDE_UNBREAKABLE.equals(key)) {
            return ItemFlag.HIDE_UNBREAKABLE;
        }
        if (Keys.HIDE_CAN_DESTROY.equals(key)) {
            return ItemFlag.HIDE_DESTROYS;
        }
        if (Keys.HIDE_CAN_PLACE.equals(key)) {
            return ItemFlag.HIDE_PLACED_ON;
        }
        if (Keys.HIDE_MISCELLANEOUS.equals(key)) {
            return ItemFlag.HIDE_ADDITIONAL_TOOLTIP;
        }
        throw new RuntimeException("Unknown mapping from sponge key to ItemFlag of " + key.key().formatted());
    }

    public static Key<Value<Boolean>> toSponge(ItemFlag flag) {
        return switch (flag) {
            case HIDE_ENCHANTS -> Keys.HIDE_STORED_ENCHANTMENTS;
            case HIDE_ATTRIBUTES -> Keys.HIDE_ATTRIBUTES;
            case HIDE_UNBREAKABLE -> Keys.HIDE_UNBREAKABLE;
            case HIDE_DESTROYS -> Keys.HIDE_CAN_DESTROY;
            case HIDE_PLACED_ON -> Keys.HIDE_CAN_PLACE;
            case HIDE_ADDITIONAL_TOOLTIP -> Keys.HIDE_MISCELLANEOUS;
            case HIDE_DYE -> Keys.HIDE_ENCHANTMENTS;
            default -> throw new RuntimeException("Unknown ItemFlag of " + flag.name());
        };
    }
}
