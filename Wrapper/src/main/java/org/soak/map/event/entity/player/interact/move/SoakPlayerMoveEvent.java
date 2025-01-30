package org.soak.map.event.entity.player.interact.move;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.math.vector.Vector3d;

public class SoakPlayerMoveEvent extends SoakEvent<MoveEntityEvent, PlayerMoveEvent> {

    public SoakPlayerMoveEvent(Class<PlayerMoveEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    public void handle(MoveEntityEvent event) throws Exception {
        var opPlayer = event.cause().first(ServerPlayer.class);
        if (opPlayer.isEmpty()) {
            return;
        }
        var spongePlayer = opPlayer.get();

        var distance = event.originalPosition().distanceSquared(event.destinationPosition());
        var isFlying = spongePlayer.get(Keys.IS_ELYTRA_FLYING).orElse(false);
        if (distance > (isFlying ? 300 : 100)) {
            //if too large then count as teleport

            //TODO find a better way to do this
            //these values are found based on the "moved to quickly" message as of the open source minecraft community
            //https://wiki.vg/Protocol#Set_Player_Position
            return;
        }

        var bukkitPlayer = SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongePlayer);
        var spongeOriginalPosition = event.originalPosition();
        var newPositionWorld = bukkitPlayer.getWorld();
        var originalPosition = new Location(newPositionWorld,
                spongeOriginalPosition.x(),
                spongeOriginalPosition.y(),
                spongeOriginalPosition.z());
        var originalRotation = spongePlayer.rotation();
        originalPosition.setPitch((float) originalRotation.x());
        originalPosition.setYaw((float) originalRotation.y());

        var spongeNewPosition = event.destinationPosition();
        var newPosition = new Location(newPositionWorld,
                spongeNewPosition.x(),
                spongeNewPosition.y(),
                spongeNewPosition.z());
        newPosition.setPitch(originalPosition.getPitch());
        newPosition.setYaw(originalPosition.getYaw());

        var bukkitEvent = new PlayerMoveEvent(bukkitPlayer, originalPosition, newPosition);
        fireEvent(bukkitEvent);
        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
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
            spongePlayer.setRotation(newRotation);
        }

        //change .... getFrom????
        //guess of implementation ->
        //when the event is cancelled the player will teleport back to this specified position


        //but why???? surly setting to "to" position is easier than cancelling the event and setting "from"????
    }
}
