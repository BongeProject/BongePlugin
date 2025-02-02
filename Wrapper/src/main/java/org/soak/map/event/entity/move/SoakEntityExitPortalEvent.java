package org.soak.map.event.entity.move;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.SoakLocationMap;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.utils.TagHelper;
import org.soak.wrapper.entity.AbstractEntity;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.tag.BlockTypeTags;

public class SoakEntityExitPortalEvent extends SoakEvent<MoveEntityEvent, EntityPortalExitEvent> {

    public SoakEntityExitPortalEvent(Class<EntityPortalExitEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<MoveEntityEvent> spongeEventClass() {
        return MoveEntityEvent.class;
    }

    @Override
    public void handle(MoveEntityEvent event) {
        var spongeLocation = event.entity().world().location(event.destinationPosition());
        var type = spongeLocation.blockType();
        if (TagHelper.getBlockTypes(BlockTypeTags.PORTALS).anyMatch(blockType -> blockType.equals(type))) {
            return;
        }

        var originalSpongeLocation = event.entity().world().location(event.originalPosition());
        var originalType = originalSpongeLocation.blockType();
        if (TagHelper.getBlockTypes(BlockTypeTags.PORTALS).noneMatch(blockType -> blockType.equals(originalType))) {
            return;
        }
        var spongeLocationTo = event.entity().world().location(event.destinationPosition());

        var bukkitEntity = AbstractEntity.wrap(event.entity());
        var bukkitLocationFrom = SoakLocationMap.toBukkit(originalSpongeLocation);
        var bukkitLocationTo = SoakLocationMap.toBukkit(spongeLocationTo);

        var bukkitPositionFrom = bukkitLocationFrom.toVector(); //i feel like this is wrong
        var bukkitPositionTo = bukkitLocationTo.toVector();


        var bukkitEvent = new EntityPortalExitEvent(bukkitEntity, bukkitLocationFrom, bukkitLocationTo, bukkitPositionFrom, bukkitPositionTo);
        fireEvent(bukkitEvent);

        //TODO change position after event
    }
}
