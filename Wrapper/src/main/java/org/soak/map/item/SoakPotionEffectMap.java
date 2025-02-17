package org.soak.map.item;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.item.potion.PotionType;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.util.Ticks;

public class SoakPotionEffectMap {

    public static org.bukkit.potion.PotionEffect toBukkit(PotionEffect effect) {
        return new org.bukkit.potion.PotionEffect(toBukkit(effect.type()),
                                                  (int) effect.duration().ticks(),
                                                  effect.amplifier(),
                                                  effect.isAmbient(),
                                                  effect.showsIcon());
    }

    public static PotionEffect buildSponge(org.bukkit.potion.PotionEffect effect) {
        return PotionEffect.builder()
                .amplifier(effect.getAmplifier())
                .duration(Ticks.of(effect.getDuration()))
                .potionType(toSponge(effect.getType()))
                .ambient(effect.isAmbient())
                .showIcon(effect.hasIcon())
                .build();
    }

    public static PotionEffectType toSponge(org.bukkit.potion.PotionEffectType bukkitType) {
        @SuppressWarnings("deprecation") var key = ResourceKey.minecraft(bukkitType.getName().toLowerCase());
        //noinspection deprecation
        return RegistryTypes.POTION_EFFECT_TYPE.get()
                .findValue(key)
                .orElseThrow(() -> new RuntimeException("Cannot find mapping for " + bukkitType.getName()));
    }

    public static org.bukkit.potion.PotionEffectType toBukkit(@SuppressWarnings("TypeMayBeWeakened") PotionEffectType type) {
        var key = type.key(RegistryTypes.POTION_EFFECT_TYPE);
        return org.bukkit.potion.PotionEffectType.getByName(key.value());
    }

    public static PotionType toSponge(org.bukkit.potion.PotionType bukkitType) {
        return RegistryTypes.POTION_TYPE.get()
                .findValue(ResourceKey.minecraft(bukkitType.name().toLowerCase()))
                .orElseThrow(() -> new RuntimeException("Could not find sponge potion type of " + bukkitType.name()));
    }

    public static org.bukkit.potion.PotionType toBukkit(@SuppressWarnings("TypeMayBeWeakened") PotionType type) {
        String bukkitName = type.key(RegistryTypes.POTION_TYPE).value().toUpperCase();
        return org.bukkit.potion.PotionType.valueOf(bukkitName);
    }
}
