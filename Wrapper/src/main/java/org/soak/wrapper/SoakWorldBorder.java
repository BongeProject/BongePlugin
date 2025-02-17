package org.soak.wrapper;


import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.wrapper.world.SoakWorld;
import org.spongepowered.api.world.server.ServerWorld;

import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

//why isnt this in the world package Bukkit????
public class SoakWorldBorder implements WorldBorder {

    private @NotNull org.spongepowered.api.world.border.WorldBorder sponge;
    private final @Nullable World world;

    public SoakWorldBorder(@NotNull org.spongepowered.api.world.border.WorldBorder sponge, @Nullable World world) {
        this.sponge = sponge;
        this.world = world;
    }

    private void update(Function<org.spongepowered.api.world.border.WorldBorder.Builder,
            org.spongepowered.api.world.border.WorldBorder.Builder> function) {
        this.sponge = function.apply(org.spongepowered.api.world.border.WorldBorder.builder().from(this.sponge))
                .build();
        var world = this.spongeWorld();
        if (world != null) {
            world.setBorder(this.sponge);
        }
    }

    public @Nullable ServerWorld spongeWorld() {
        if (this.world == null) {
            return null;
        }
        return ((SoakWorld) this.world).sponge();
    }

    @Override
    public @Nullable World getWorld() {
        return this.world;
    }

    @Override
    public void reset() {
        this.sponge = org.spongepowered.api.world.border.WorldBorder.defaultBorder();
        var spongeWorld = this.spongeWorld();
        if (spongeWorld == null) {
            return;
        }
        spongeWorld.setBorder(this.sponge);
    }

    @Override
    public double getSize() {
        return this.sponge.diameter();
    }

    @Override
    public void setSize(double size) {
        setSize(size, 0);
    }

    @Override
    public void setSize(double size, long seconds) {
        setSize(size, TimeUnit.SECONDS, seconds);
    }

    @Override
    public void setSize(double size, @NotNull TimeUnit timeUnit, long units) {
        update(builder -> builder.targetDiameter(size)
                .timeToTargetDiameter(Duration.of(units, timeUnit.toChronoUnit())));
    }

    @Override
    public @NotNull Location getCenter() {
        var center = this.sponge.center();
        return new Location(this.world, center.x(), 0, center.y());
    }

    @Override
    public void setCenter(@NotNull Location location) {
        setCenter(location.x(), location.z());
    }

    @Override
    public void setCenter(double v, double v1) {
        update(builder -> builder.center(v, v1));
    }

    @Override
    public double getDamageBuffer() {
        throw NotImplementedException.createByLazy(WorldBorder.class, "getDamageBuffer");
    }

    @Override
    public void setDamageBuffer(double v) {
        throw NotImplementedException.createByLazy(WorldBorder.class, "setDamageBuffer", double.class);
    }

    @Override
    public double getDamageAmount() {
        return this.sponge.damagePerBlock();
    }

    @Override
    public void setDamageAmount(double v) {
        update(builder -> builder.damagePerBlock(v));
    }

    @Override
    public int getWarningTime() {
        return (int) this.sponge.warningTime().toSeconds();
    }

    @Override
    public void setWarningTime(int i) {
        update(builder -> builder.warningTime(Duration.of(i, TimeUnit.SECONDS.toChronoUnit())));
    }

    @Override
    public int getWarningDistance() {
        return this.sponge.warningDistance();
    }

    @Override
    public void setWarningDistance(int i) {
        update(builder -> builder.warningDistance(i));
    }

    @Override
    public boolean isInside(@NotNull Location location) {
        var spongeWorld = this.spongeWorld();
        if (spongeWorld != null) {
            if (location.getWorld() != null && !spongeWorld.equals(((SoakWorld) location.getWorld()).sponge())) {
                return false;
            }
        }

        //worldBorder doesnt respect .equals
        if (isDefault()) {
            //default border == unlimited?
            return true;
        }

        //this doesnt work with default world border
        var worldBorderCenter = new Location(location.getWorld(),
                                             this.sponge.center().x(),
                                             location.getY(),
                                             this.sponge.center().y());
        var distance = location.distance(worldBorderCenter);
        var radius = this.sponge.safeZone() / 2;
        return radius <= distance;
    }

    private boolean isDefault() {
        var thisBuilder = this.sponge.toBuilder();
        var compareBuilder = org.spongepowered.api.world.border.WorldBorder.builder().overworldDefaults();

        return Stream.of(thisBuilder.getClass().getDeclaredFields())
                .filter(field -> !Modifier.isFinal(field.getModifiers()))
                .allMatch(field -> {
                    field.setAccessible(true);
                    try {
                        var thisValue = field.get(thisBuilder);
                        var compareValue = field.get(compareBuilder);
                        field.setAccessible(false);
                        return thisValue.equals(compareValue);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

    }

    @Override
    public double getMaxSize() {
        throw NotImplementedException.createByLazy(WorldBorder.class, "getMaxSize");
    }

    @Override
    public double getMaxCenterCoordinate() {
        throw NotImplementedException.createByLazy(WorldBorder.class, "getMaxCenterCoordinate");
    }
}
