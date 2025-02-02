package org.soak.map.event.entity.player.interact.move;

import org.bukkit.Server;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.entity.RotateEntityEvent;
import org.spongepowered.math.vector.Vector3d;

public class SoakPlayerRotateEvent extends SoakEvent<RotateEntityEvent, PlayerMoveEvent> {

    public SoakPlayerRotateEvent(Class<PlayerMoveEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<RotateEntityEvent> spongeEventClass() {
        return RotateEntityEvent.class;
    }

    @Override
    public void handle(RotateEntityEvent event) throws Exception {
        var entity = event.entity();
        if (!(entity instanceof ServerPlayer spongePlayer)) {
            return;
        }
        var bukkitPlayer = SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongePlayer);
        var originalPosition = bukkitPlayer.getLocation();
        var originalRotation = event.fromRotation();
        originalPosition.setPitch((float) originalRotation.x());
        originalPosition.setYaw((float) originalRotation.y());

        var newPosition = originalPosition.clone();
        var newRotation = event.fromRotation();
        newPosition.setPitch((float) newRotation.x());
        newPosition.setYaw((float) newRotation.y());


        var bukkitEvent = new PlayerMoveEvent(bukkitPlayer, originalPosition, newPosition);
        fireEvent(bukkitEvent);
        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        var to = bukkitEvent.getTo();
        var pitch = to.getPitch();
        var yaw = to.getYaw();
        var eventRotation = new Vector3d(pitch, yaw, newRotation.z());
        if (!eventRotation.equals(newRotation)) {
            event.setToRotation(new Vector3d(to.getPitch(), to.getYaw(), newRotation.z()));
        }

        //change .... getFrom????
        //guess of implementation ->
        //when the event is cancelled the player will teleport back to this specified position


        //but why???? surly setting to "to" position is easier than cancelling the event and setting "from"????
    }
}
