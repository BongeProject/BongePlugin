package org.soak.map;

import org.bukkit.Fluid;
import org.spongepowered.api.fluid.FluidType;
import org.spongepowered.api.fluid.FluidTypes;
import org.spongepowered.api.registry.RegistryTypes;

public class SoakFluidTypeMap {

    public static Fluid toBukkit(FluidType type) {
        if (type.equals(FluidTypes.EMPTY.get())) {
            return null;
        }
        if (type.equals(FluidTypes.FLOWING_LAVA.get())) {
            return Fluid.FLOWING_LAVA;
        }
        if (type.equals(FluidTypes.LAVA.get())) {
            return Fluid.LAVA;
        }
        if (type.equals(FluidTypes.FLOWING_WATER.get())) {
            return Fluid.FLOWING_WATER;
        }
        if (type.equals(FluidTypes.WATER.get())) {
            return Fluid.WATER;
        }
        throw new RuntimeException("No mapping for " + type.key(RegistryTypes.FLUID_TYPE).formatted());
    }

    public static FluidType toSponge(Fluid fluid) {
        return switch (fluid) {
            case WATER -> FluidTypes.WATER.get();
            case FLOWING_WATER -> FluidTypes.FLOWING_WATER.get();
            case LAVA -> FluidTypes.LAVA.get();
            case FLOWING_LAVA -> FluidTypes.FLOWING_LAVA.get();
            default -> throw new RuntimeException("No mapping found for " + fluid.name());
        };
    }
}
