package org.soak.map.event.entity.move;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.entity.AbstractEntity;
import org.spongepowered.api.entity.vehicle.Vehicle;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.RotateEntityEvent;
import org.spongepowered.math.vector.Vector3d;

public class SoakVehicleRotateEvent extends SoakEvent<RotateEntityEvent, VehicleMoveEvent> {

    public SoakVehicleRotateEvent(Class<VehicleMoveEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    public void handle(RotateEntityEvent event) throws Exception {
        var entity = event.entity();
        if (!(entity instanceof Vehicle)) {
            return;
        }
        var bukkitPlayer = (org.bukkit.entity.Vehicle) AbstractEntity.wrap(entity);
        var originalPosition = bukkitPlayer.getLocation();
        var originalRotation = event.fromRotation();
        originalPosition.setPitch((float) originalRotation.x());
        originalPosition.setYaw((float) originalRotation.y());

        var newPosition = originalPosition.clone();
        var newRotation = event.fromRotation();
        newPosition.setPitch((float) newRotation.x());
        newPosition.setYaw((float) newRotation.y());

        var bukkitEvent = new VehicleMoveEvent(bukkitPlayer, originalPosition, newPosition);
        fireEvent(bukkitEvent);
        var to = bukkitEvent.getTo();
        var pitch = to.getPitch();
        var yaw = to.getYaw();
        var eventRotation = new Vector3d(pitch, yaw, newRotation.z());
        if (!eventRotation.equals(newRotation)) {
            event.setToRotation(new Vector3d(to.getPitch(), to.getYaw(), newRotation.z()));
        }
    }
}
