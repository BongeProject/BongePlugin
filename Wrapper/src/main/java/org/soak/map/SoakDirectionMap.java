package org.soak.map;

import org.bukkit.block.BlockFace;
import org.spongepowered.api.util.Axis;
import org.spongepowered.api.util.Direction;

public class SoakDirectionMap {

    public static Axis toSponge(org.bukkit.Axis axis) {
        return Axis.valueOf(axis.name());
    }

    public static org.bukkit.Axis toBukkit(Axis axis) {
        return org.bukkit.Axis.valueOf(axis.name());
    }

    public static float toYaw(BlockFace face) {
        return switch (face) {
            case NORTH -> 337.5F;
            case EAST -> 67.5F;
            case SOUTH -> 157.5F;
            case WEST -> 247.5F;
            case NORTH_EAST -> 22.5F;
            case NORTH_WEST -> 292.5F;
            case SOUTH_EAST -> 112.5F;
            case SOUTH_WEST -> 202.5F;
            default -> 0.0f;
        };
    }

    public static Direction toSponge(BlockFace face) {
        return switch (face) {
            case NORTH -> Direction.NORTH;
            case EAST -> Direction.EAST;
            case SOUTH -> Direction.SOUTH;
            case WEST -> Direction.WEST;
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case NORTH_EAST -> Direction.NORTHEAST;
            case NORTH_WEST -> Direction.NORTHWEST;
            case SOUTH_EAST -> Direction.SOUTHEAST;
            case SOUTH_WEST -> Direction.SOUTHWEST;
            case WEST_NORTH_WEST -> Direction.WEST_NORTHWEST;
            case NORTH_NORTH_WEST -> Direction.NORTH_NORTHWEST;
            case NORTH_NORTH_EAST -> Direction.NORTH_NORTHEAST;
            case EAST_NORTH_EAST -> Direction.EAST_NORTHEAST;
            case EAST_SOUTH_EAST -> Direction.EAST_SOUTHEAST;
            case SOUTH_SOUTH_EAST -> Direction.SOUTH_SOUTHEAST;
            case SOUTH_SOUTH_WEST -> Direction.SOUTH_SOUTHWEST;
            case WEST_SOUTH_WEST -> Direction.WEST_SOUTHWEST;
            case SELF -> Direction.NONE;
        };
    }

    public static BlockFace toBukkit(Direction direction) {
        return switch (direction) {
            case NORTH -> BlockFace.NORTH;
            case NORTH_NORTHEAST -> BlockFace.NORTH_NORTH_EAST;
            case NORTHEAST -> BlockFace.NORTH_EAST;
            case EAST_NORTHEAST -> BlockFace.EAST_NORTH_EAST;
            case EAST -> BlockFace.EAST;
            case EAST_SOUTHEAST -> BlockFace.EAST_SOUTH_EAST;
            case SOUTHEAST -> BlockFace.SOUTH_EAST;
            case SOUTH_SOUTHEAST -> BlockFace.SOUTH_SOUTH_EAST;
            case SOUTH -> BlockFace.SOUTH;
            case SOUTH_SOUTHWEST -> BlockFace.SOUTH_SOUTH_WEST;
            case SOUTHWEST -> BlockFace.SOUTH_WEST;
            case WEST_SOUTHWEST -> BlockFace.WEST_SOUTH_WEST;
            case WEST -> BlockFace.WEST;
            case WEST_NORTHWEST -> BlockFace.WEST_NORTH_WEST;
            case NORTHWEST -> BlockFace.NORTH_WEST;
            case NORTH_NORTHWEST -> BlockFace.NORTH_NORTH_WEST;
            case UP -> BlockFace.UP;
            case DOWN -> BlockFace.DOWN;
            case NONE -> BlockFace.SELF;
        };
    }
}
