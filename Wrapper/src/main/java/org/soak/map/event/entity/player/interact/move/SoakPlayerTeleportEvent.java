package org.soak.map.event.entity.player.interact.move;

import org.bukkit.Location;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.ChangeEntityWorldEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.math.vector.Vector3d;

public class SoakPlayerTeleportEvent extends SoakEvent<MoveEntityEvent, PlayerTeleportEvent> {

    public SoakPlayerTeleportEvent(Class<PlayerTeleportEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<MoveEntityEvent> spongeEventClass() {
        return MoveEntityEvent.class;
    }

    @Override
    public void handle(MoveEntityEvent event) throws Exception {
        if (event instanceof RespawnPlayerEvent.Recreate) {
            return;
        }
        var entity = event.entity();
        if (!(entity instanceof ServerPlayer spongePlayer)) {
            return;
        }

        var distance = event.originalPosition().distanceSquared(event.destinationPosition());
        var isFlying = spongePlayer.get(Keys.IS_ELYTRA_FLYING).orElse(false);
        if (distance <= (isFlying ? 300 : 100)) {
            //if too small then count as movement

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
        originalPosition.setDirection(new Vector(originalRotation.x(), originalRotation.y(), originalRotation.z()));


        var spongeNewPosition = event.destinationPosition();
        if (event instanceof ChangeEntityWorldEvent.Reposition) {
            newPositionWorld = SoakManager.<WrapperManager>getManager()
                    .getMemoryStore()
                    .get(((ChangeEntityWorldEvent.Reposition) event).destinationWorld());
        }
        var newPosition = new Location(newPositionWorld,
                spongeNewPosition.x(),
                spongeNewPosition.y(),
                spongeNewPosition.z());

        var bukkitEvent = new PlayerTeleportEvent(bukkitPlayer, originalPosition, newPosition);
        fireEvent(bukkitEvent);
        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        var to = bukkitEvent.getTo();
        event.setDestinationPosition(new Vector3d(to.getX(), to.getY(), to.getZ()));

        //change .... getFrom????
        //guess of implementation ->
        //when the event is cancelled the player will teleport back to this specified position


        //but why???? surly setting to "to" position is easier than cancelling the event and setting "from"????
    }
}
