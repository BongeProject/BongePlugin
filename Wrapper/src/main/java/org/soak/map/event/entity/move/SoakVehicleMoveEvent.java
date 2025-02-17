package org.soak.map.event.entity.move;

import org.bukkit.Location;
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
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.math.vector.Vector3d;

public class SoakVehicleMoveEvent extends SoakEvent<MoveEntityEvent, VehicleMoveEvent> {

    public SoakVehicleMoveEvent(Class<VehicleMoveEvent> bukkitEvent, EventPriority priority, Plugin plugin,
                                Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<MoveEntityEvent> spongeEventClass() {
        return MoveEntityEvent.class;
    }

    @Override
    public void handle(MoveEntityEvent event) throws Exception {
        var entity = event.entity();
        if (!(entity instanceof Vehicle)) {
            return;
        }

        var bukkitVehicle = (org.bukkit.entity.Vehicle) AbstractEntity.wrap(entity);
        var spongeOriginalPosition = event.originalPosition();
        var newPositionWorld = bukkitVehicle.getWorld();
        var originalPosition = new Location(newPositionWorld,
                                            spongeOriginalPosition.x(),
                                            spongeOriginalPosition.y(),
                                            spongeOriginalPosition.z());
        var originalRotation = entity.rotation();
        originalPosition.setPitch((float) originalRotation.x());
        originalPosition.setYaw((float) originalRotation.y());

        var spongeNewPosition = event.destinationPosition();
        var newPosition = new Location(newPositionWorld,
                                       spongeNewPosition.x(),
                                       spongeNewPosition.y(),
                                       spongeNewPosition.z());
        newPosition.setPitch(originalPosition.getPitch());
        newPosition.setYaw(originalPosition.getYaw());

        var bukkitEvent = new VehicleMoveEvent(bukkitVehicle, originalPosition, newPosition);
        fireEvent(bukkitEvent);
        var to = bukkitEvent.getTo();
        event.setDestinationPosition(new Vector3d(to.getX(), to.getY(), to.getZ()));
        Vector3d newRotation = originalRotation;
        if (originalRotation.x() != to.getPitch()) {
            newRotation = new Vector3d(to.getPitch(), newRotation.y(), newRotation.z());
        }
        if (originalRotation.y() != to.getYaw()) {
            newRotation = new Vector3d(newRotation.x(), to.getYaw(), newRotation.z());
        }
        if (!newRotation.equals(originalRotation)) {
            entity.setRotation(newRotation);
        }

        //change .... getFrom????
        //guess of implementation ->
        //when the event is cancelled the player will teleport back to this specified position


        //but why???? surly setting to "to" position is easier than cancelling the event and setting "from"????
    }
}
