package org.soak.map.item;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.soak.map.SoakResourceKeyMap;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.registry.RegistryTypes;

public class SoakEnchantmentTypeMap {

    //despite both bukkit and sponge having a way to get based on Key, both Sponge and Bukkit don't share the same
    // keys for everything ... so enjoy the if statements for exceptions

    public static EnchantmentType toSponge(Enchantment enchantment) {
        ResourceKey keyType = SoakResourceKeyMap.mapToSponge(enchantment.getKey());
        return RegistryTypes.ENCHANTMENT_TYPE.get().value(keyType);
    }

    public static Enchantment toBukkit(EnchantmentType type) {
        NamespacedKey key = SoakResourceKeyMap.mapToBukkit(type.key(RegistryTypes.ENCHANTMENT_TYPE));

        var enchantmentType = Enchantment.getByKey(key);
        if (enchantmentType == null) {
            throw new RuntimeException("Cannot find enchantment for " + key.asString());
        }
        return enchantmentType;
    }
}
