package org.soak.map.event.entity.move;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.SoakLocationMap;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.entity.AbstractEntity;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.ChangeEntityWorldEvent;

public class SoakPortalTeleportEntityEvent extends SoakEvent<ChangeEntityWorldEvent.Reposition, EntityPortalEvent> {

    public SoakPortalTeleportEntityEvent(Class<EntityPortalEvent> bukkitEvent, EventPriority priority, Plugin plugin,
                                         Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ChangeEntityWorldEvent.Reposition> spongeEventClass() {
        return ChangeEntityWorldEvent.Reposition.class;
    }

    @Override
    public void handle(ChangeEntityWorldEvent.Reposition event) throws Exception {
        var spongeToLocation = event.entity().world().location(event.destinationPosition());
        var spongeFromLocation = event.entity().world().location(event.originalPosition());

        var bukkitEntity = AbstractEntity.wrap(event.entity());
        var bukkitFromLocation = SoakLocationMap.toBukkit(spongeFromLocation);
        var bukkitToLocation = SoakLocationMap.toBukkit(spongeToLocation);

        var bukkitEvent = new EntityPortalEvent(bukkitEntity, bukkitFromLocation, bukkitToLocation);
        fireEvent(bukkitEvent);

        if (bukkitEvent.getTo() != null && !bukkitEvent.getTo().equals(bukkitToLocation)) {
            spongeToLocation = SoakLocationMap.toSponge(bukkitEvent.getTo());
            event.setDestinationPosition(spongeToLocation.position());
        }
        if (bukkitEvent.isCancelled() && !bukkitEvent.getFrom().equals(bukkitFromLocation)) {
            spongeFromLocation = SoakLocationMap.toSponge(bukkitEvent.getFrom());
            event.setDestinationPosition(spongeFromLocation.position());
        } else if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
