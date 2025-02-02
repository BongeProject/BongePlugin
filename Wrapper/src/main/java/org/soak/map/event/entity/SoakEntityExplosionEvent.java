package org.soak.map.event.entity;

import org.bukkit.ExplosionResult;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlock;
import org.soak.wrapper.entity.AbstractEntity;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.world.ExplosionEvent;

import java.util.stream.Collectors;

public class SoakEntityExplosionEvent extends SoakEvent<ExplosionEvent.Detonate, EntityExplodeEvent> {

    public SoakEntityExplosionEvent(Class<EntityExplodeEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ExplosionEvent.Detonate> spongeEventClass() {
        return ExplosionEvent.Detonate.class;
    }

    private ExplosionResult result(ExplosionEvent.Detonate event) {
        if (!event.explosion().shouldBreakBlocks()) {
            return ExplosionResult.KEEP;
        }
        if (event.explosion().randomness() == 0) {
            return ExplosionResult.DESTROY;
        }
        return ExplosionResult.DESTROY_WITH_DECAY;
    }

    @Override
    public void handle(ExplosionEvent.Detonate event) throws Exception {
        var opSource = event.explosion().sourceExplosive();
        if (opSource.isEmpty()) {
            return;
        }
        var bukkitEntity = AbstractEntity.wrap(opSource.get());
        var spongeLocation = event.explosion().serverLocation();
        var bukkitWorld = SoakManager.<WrapperManager>getManager().getMemoryStore().get(event.world());
        var bukkitLocation = new Location(bukkitWorld, spongeLocation.x(), spongeLocation.y(), spongeLocation.z());
        var blocks = event.affectedLocations().stream().map(loc -> (Block) new SoakBlock(loc)).collect(Collectors.toList());
        var result = result(event);

        var bukkitEvent = new EntityExplodeEvent(bukkitEntity, bukkitLocation, blocks, 0, result);
        fireEvent(bukkitEvent);
    }
}
