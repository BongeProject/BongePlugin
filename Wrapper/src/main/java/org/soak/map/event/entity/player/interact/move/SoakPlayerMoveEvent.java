package org.soak.map.event.entity.player.interact.move;

import org.bukkit.Location;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.soak.WrapperManager;
import org.soak.map.event.EventSingleListenerWrapper;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.math.vector.Vector3d;

public class SoakPlayerMoveEvent {

    private final EventSingleListenerWrapper<PlayerMoveEvent> singleEventListener;

    public SoakPlayerMoveEvent(EventSingleListenerWrapper<PlayerMoveEvent> listener) {
        this.singleEventListener = listener;
    }

    @Listener(order = Order.FIRST)
    @Exclude(RespawnPlayerEvent.Recreate.class)
    public void firstEvent(MoveEntityEvent event, @Getter("entity") ServerPlayer player) {
        fireEvent(event, player, EventPriority.HIGHEST);
    }

    @Listener(order = Order.EARLY)
    @Exclude(RespawnPlayerEvent.Recreate.class)
    public void earlyEvent(MoveEntityEvent event, @Getter("entity") ServerPlayer player) {
        fireEvent(event, player, EventPriority.HIGH);
    }

    @Listener(order = Order.DEFAULT)
    @Exclude(RespawnPlayerEvent.Recreate.class)
    public void normalEvent(MoveEntityEvent event, @Getter("entity") ServerPlayer player) {
        fireEvent(event, player, EventPriority.NORMAL);
    }

    @Listener(order = Order.LATE)
    @Exclude(RespawnPlayerEvent.Recreate.class)
    public void lateEvent(MoveEntityEvent event, @Getter("entity") ServerPlayer player) {
        fireEvent(event, player, EventPriority.LOW);
    }

    @Listener(order = Order.LAST)
    @Exclude(RespawnPlayerEvent.Recreate.class)
    public void lastEvent(MoveEntityEvent event, @Getter("entity") ServerPlayer player) {
        fireEvent(event, player, EventPriority.LOWEST);
    }

    private void fireEvent(MoveEntityEvent event, ServerPlayer spongePlayer, EventPriority priority) {
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
        SoakManager.<WrapperManager>getManager().getServer().getPluginManager().callEvent(this.singleEventListener, bukkitEvent, priority);

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
