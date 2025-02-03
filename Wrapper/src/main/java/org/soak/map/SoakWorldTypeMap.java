package org.soak.map;

import org.bukkit.World;
import org.spongepowered.api.registry.DefaultedRegistryReference;
import org.spongepowered.api.world.WorldType;
import org.spongepowered.api.world.WorldTypes;

public class SoakWorldTypeMap {

    public static DefaultedRegistryReference<WorldType> toSponge(World.Environment environment) {
        return switch (environment) {
            case NORMAL -> WorldTypes.OVERWORLD;
            case NETHER -> WorldTypes.THE_NETHER;
            case THE_END -> WorldTypes.THE_END;
            default -> throw new RuntimeException("Unknown world type of " + environment.name());
        };
    }

    public static World.Environment toBukkit(WorldType type) {
        if (type.equals(WorldTypes.OVERWORLD.get()) || type.equals(WorldTypes.OVERWORLD_CAVES.get())) {
            return World.Environment.NORMAL;
        }
        if (type.equals(WorldTypes.THE_NETHER.get())) {
            return World.Environment.NETHER;
        }
        if (type.equals(WorldTypes.THE_END.get())) {
            return World.Environment.THE_END;
        }
        return World.Environment.CUSTOM;
    }
}
