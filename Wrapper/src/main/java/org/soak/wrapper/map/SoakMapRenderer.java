package org.soak.wrapper.map;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.soak.exception.NotImplementedException;

public class SoakMapRenderer extends MapRenderer {

    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
        throw NotImplementedException.createByLazy(MapRenderer.class, "render", MapView.class, MapCanvas.class, Player.class);
    }
}
