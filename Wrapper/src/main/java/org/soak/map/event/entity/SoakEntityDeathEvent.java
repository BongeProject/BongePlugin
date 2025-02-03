package org.soak.map.event.entity;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.map.item.SoakItemStackMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.damage.SoakDamageSource;
import org.soak.wrapper.entity.AbstractEntity;
import org.soak.wrapper.entity.living.AbstractLivingEntity;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.server.ServerWorld;

import java.util.stream.Collectors;

public class SoakEntityDeathEvent extends SoakEvent<DropItemEvent.Destruct, EntityDeathEvent> {

    public SoakEntityDeathEvent(Class<EntityDeathEvent> bukkitEvent, EventPriority priority, Plugin plugin,
                                Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<DropItemEvent.Destruct> spongeEventClass() {
        return DropItemEvent.Destruct.class;
    }

    @Override
    public void handle(DropItemEvent.Destruct event) throws Exception {
        var opBukkitEntity = event.cause()
                .first(Living.class)
                .filter(living -> !(living instanceof ServerPlayer))
                .map(AbstractEntity::wrap);
        if (opBukkitEntity.isEmpty()) {
            return;
        }
        var entity = opBukkitEntity.get();
        var opSpongeDamageCause = event.cause().first(DamageSource.class);
        if (opSpongeDamageCause.isEmpty()) {
            return;
        }
        var bukkitDamageCause = new SoakDamageSource(opSpongeDamageCause.get(),
                                                     (ServerWorld) entity.spongeEntity().world());
        var items = event.entities()
                .parallelStream()
                .map(itemEntity -> itemEntity.get(Keys.ITEM_STACK_SNAPSHOT)
                        .orElseThrow(() -> new RuntimeException("Item (" + itemEntity.type()
                                .key(RegistryTypes.ENTITY_TYPE) + ") does not contain an ItemStack")))
                .map(SoakItemStackMap::toBukkit)
                .collect(Collectors.toList());
        //TODO -> find exp
        var bukkitEvent = new EntityDeathEvent(entity, bukkitDamageCause, items);
        fireEvent(bukkitEvent);
        //TODO -> spawn the entity back in if event is cancelled
        //TODO -> cancel death sounds .... that sounds like a hassle

    }
}
