package org.soak.utils;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.soak.wrapper.inventory.meta.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.map.MapInfo;
import org.spongepowered.api.world.server.ServerWorld;

public class RegisterUtils {

    public static void registerSerializable() {
        ConfigurationSerialization.registerClass(AbstractItemMeta.class, "ItemMeta");
        ConfigurationSerialization.registerClass(SoakRepairable.class);
        ConfigurationSerialization.registerClass(FireworkEffectMeta.class);
        ConfigurationSerialization.registerClass(FireworkMeta.class);
        ConfigurationSerialization.registerClass(SoakLeatherArmorMeta.class);
        ConfigurationSerialization.registerClass(SoakPotionItemMeta.class);
        ConfigurationSerialization.registerClass(SoakSkullMeta.class);
    }

    public static void registerMapViews(){
        Sponge.server().worldManager().worlds().forEach(RegisterUtils::registerMapView);
    }

    public static void registerMapView(ServerWorld world){
        var mapInfo = Sponge.server().mapStorage().createNewMapInfo().orElseThrow(() -> new IllegalStateException("Cannot create MapView"));
        mapInfo.offer(Keys.MAP_WORLD, world.key());
        mapInfo.offer(Keys.MAP_TRACKS_PLAYERS, true);
    }
}
