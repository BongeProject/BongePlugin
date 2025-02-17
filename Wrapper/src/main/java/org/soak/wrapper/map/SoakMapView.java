package org.soak.wrapper.map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakResourceKeyMap;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.map.MapInfo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SoakMapView implements MapView {

    private final MapInfo info;

    public SoakMapView(MapInfo info) {
        this.info = info;
    }

    @Override
    public int getId() {
        throw NotImplementedException.createByLazy(MapView.class, "getId");
    }

    @Override
    public boolean isVirtual() {
        throw NotImplementedException.createByLazy(MapView.class, "isVirtual");
    }

    @Override
    public @NotNull Scale getScale() {
        return Objects.requireNonNull(Scale.valueOf(this.info.get(Keys.MAP_SCALE).orElse(0).byteValue()));
    }

    @Override
    public void setScale(@NotNull Scale scale) {
        this.info.offer(Keys.MAP_SCALE, (int) scale.getValue());
    }

    @Override
    public int getCenterX() {
        throw NotImplementedException.createByLazy(MapView.class, "getCenterX");
    }

    @Override
    public void setCenterX(int i) {
        throw NotImplementedException.createByLazy(MapView.class, "setCenterX", int.class);
    }

    @Override
    public int getCenterZ() {
        throw NotImplementedException.createByLazy(MapView.class, "getCenterY");
    }

    @Override
    public void setCenterZ(int i) {
        throw NotImplementedException.createByLazy(MapView.class, "getCenterY", int.class);

    }

    @Override
    public @Nullable World getWorld() {
        return this.info.get(Keys.MAP_WORLD).map(key -> Bukkit.getWorld(SoakResourceKeyMap.mapToBukkit(key))).orElse(null);
    }

    @Override
    public void setWorld(@NotNull World world) {
        this.info.offer(Keys.MAP_WORLD, SoakResourceKeyMap.mapToSponge(world.key()));
    }

    @Override
    public @NotNull List<MapRenderer> getRenderers() {
        return Collections.emptyList();
    }

    @Override
    public void addRenderer(@NotNull MapRenderer mapRenderer) {
        throw NotImplementedException.createByLazy(MapView.class, "addRenderer", MapRenderer.class);
    }

    @Override
    public boolean removeRenderer(@Nullable MapRenderer mapRenderer) {
        throw NotImplementedException.createByLazy(MapView.class, "removeRenderer", MapRenderer.class);
    }

    @Override
    public boolean isTrackingPosition() {
        return this.info.get(Keys.MAP_TRACKS_PLAYERS).orElse(false);
    }

    @Override
    public void setTrackingPosition(boolean b) {
        this.info.offer(Keys.MAP_TRACKS_PLAYERS, true);
    }

    @Override
    public boolean isUnlimitedTracking() {
        return this.info.get(Keys.MAP_UNLIMITED_TRACKING).orElse(false);
    }

    @Override
    public void setUnlimitedTracking(boolean b) {
        this.info.offer(Keys.MAP_UNLIMITED_TRACKING, b);
    }

    @Override
    public boolean isLocked() {
        return this.info.get(Keys.MAP_LOCKED).orElse(false);
    }

    @Override
    public void setLocked(boolean b) {
        this.info.offer(Keys.MAP_LOCKED, b);
    }
}
