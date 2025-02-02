package org.soak.map.event.entity.player.combat;

import org.bukkit.Location;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.world.SoakWorld;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;
import org.spongepowered.math.vector.Vector3d;

public class SoakPlayerRespawnEvent extends SoakEvent<RespawnPlayerEvent.Recreate, PlayerRespawnEvent> {

    public SoakPlayerRespawnEvent(Class<PlayerRespawnEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<RespawnPlayerEvent.Recreate> spongeEventClass() {
        return RespawnPlayerEvent.Recreate.class;
    }

    @Override
    public void handle(RespawnPlayerEvent.Recreate event) throws Exception {
        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get(event.entity());
        var newWorld = SoakManager.<WrapperManager>getManager().getMemoryStore().get(event.destinationWorld());
        var newLocation = new Location(newWorld,
                event.destinationPosition().x(),
                event.destinationPosition().y(),
                event.destinationPosition().z());

        var bukkitEvent = new PlayerRespawnEvent(player, newLocation, event.isBedSpawn());
        fireEvent(bukkitEvent);
        var newSetLocation = bukkitEvent.getRespawnLocation();
        if (!newLocation.equals(newSetLocation)) {
            if (newLocation.getWorld().equals(newSetLocation.getWorld())) {
                event.setDestinationPosition(new Vector3d(newSetLocation.getX(),
                        newSetLocation.getY(),
                        newSetLocation.getZ()));
                return;
            }
            event.setCancelled(true);
            var spongeNewWorld = ((SoakWorld) newSetLocation.getWorld()).sponge();
            var spongeNewLocation = spongeNewWorld.location(newSetLocation.getX(),
                    newSetLocation.getY(),
                    newSetLocation.getZ());
            //this may need a schedule on it, depending on if its a death respawn
            event.entity().setLocation(spongeNewLocation);
        }
    }
}
