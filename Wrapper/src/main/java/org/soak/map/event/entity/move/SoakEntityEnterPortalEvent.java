package org.soak.map.event.entity.move;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
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

public class SoakEntityEnterPortalEvent extends SoakEvent<MoveEntityEvent, EntityPortalEnterEvent> {

    public SoakEntityEnterPortalEvent(Class<EntityPortalEnterEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    public void handle(MoveEntityEvent event) throws Exception {
        var spongeLocation = event.entity().world().location(event.destinationPosition());
        var type = spongeLocation.blockType();
        if (TagHelper.getBlockTypes(BlockTypeTags.PORTALS).noneMatch(blockType -> blockType.equals(type))) {
            return;
        }

        var originalSpongeLocation = event.entity().world().location(event.originalPosition());
        var originalType = originalSpongeLocation.blockType();
        if (TagHelper.getBlockTypes(BlockTypeTags.PORTALS).anyMatch(blockType -> blockType.equals(originalType))) {
            return;
        }

        var bukkitEntity = AbstractEntity.wrap(event.entity());
        var bukkitLocation = SoakLocationMap.toBukkit(spongeLocation);

        //TODO portal type
        var bukkitEvent = new EntityPortalEnterEvent(bukkitEntity, bukkitLocation);
        fireEvent(bukkitEvent);
    }
}
