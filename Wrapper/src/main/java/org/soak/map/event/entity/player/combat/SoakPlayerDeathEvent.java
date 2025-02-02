package org.soak.map.event.entity.player.combat;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.map.item.SoakItemStackMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.damage.SoakDamageSource;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.stream.Collectors;

public class SoakPlayerDeathEvent extends SoakEvent<DropItemEvent.Destruct, PlayerDeathEvent> {
    private Component deathMessage = Component.empty();

    public SoakPlayerDeathEvent(Class<PlayerDeathEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<DropItemEvent.Destruct> spongeEventClass() {
        return DropItemEvent.Destruct.class;
    }

    @Override
    public void handle(DropItemEvent.Destruct event) throws Exception {
        var root = event.cause().root();
        if (!(root instanceof ServerPlayer spongePlayer)) {
            return;
        }
        var opSpongeDamageCause = event.cause().first(DamageSource.class);
        if (opSpongeDamageCause.isEmpty()) {
            //shouldnt be possible
            return;
        }
        var spongeDamageCause = opSpongeDamageCause.get();
        var bukkitDamageSource = new SoakDamageSource(spongeDamageCause, spongePlayer.world());

        var entity = SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongePlayer);
        var items = event.entities()
                .parallelStream()
                .map(itemEntity -> itemEntity.get(Keys.ITEM_STACK_SNAPSHOT)
                        .orElseThrow(() -> new RuntimeException("Item (" + itemEntity.type()
                                .key(RegistryTypes.ENTITY_TYPE) + ") does not contain an ItemStack")))
                .map(SoakItemStackMap::toBukkit)
                .collect(Collectors.toList());
        //TODO -> find exp
        var bukkitEvent = new PlayerDeathEvent(entity, bukkitDamageSource, items, 0, this.deathMessage);
        fireEvent(bukkitEvent);
        //TODO -> spawn the player back in if event is cancelled
        //TODO -> cancel/change the message
        //TODO -> set should drop experience

    }
}
