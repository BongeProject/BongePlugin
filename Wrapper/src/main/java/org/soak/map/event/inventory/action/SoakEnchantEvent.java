package org.soak.map.event.inventory.action;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.map.item.SoakEnchantmentTypeMap;
import org.soak.map.item.SoakItemStackMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlock;
import org.soak.wrapper.inventory.view.AbstractInventoryView;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.EnchantItemEvent;
import org.spongepowered.api.item.enchantment.Enchantment;

import java.util.stream.Collectors;

public class SoakEnchantEvent extends SoakEvent<EnchantItemEvent.CalculateEnchantment, org.bukkit.event.enchantment.EnchantItemEvent> {

    public SoakEnchantEvent(Class<org.bukkit.event.enchantment.EnchantItemEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    public void handle(EnchantItemEvent.CalculateEnchantment event) throws Exception {
        var opSpongePlayer = event.cause().first(ServerPlayer.class);
        if (opSpongePlayer.isEmpty()) {
            return;
        }
        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get(opSpongePlayer.get());
        var view = AbstractInventoryView.wrap(event.container());
        SoakBlock enchantmentTable = event.context()
                .get(EventContextKeys.BLOCK_EVENT_PROCESS)
                .flatMap(lb -> lb.location().onServer())
                .map(SoakBlock::new)
                .orElseThrow(() -> new RuntimeException("Could not get the enchantment block"));
        var item = SoakItemStackMap.toBukkit(event.item());
        var enchantments = event.enchantments()
                .parallelStream()
                .collect(Collectors.toMap(spongeEnch -> SoakEnchantmentTypeMap.toBukkit(spongeEnch.type()),
                        Enchantment::level));

        var bukkitEvent = new org.bukkit.event.enchantment.EnchantItemEvent(player,
                view,
                enchantmentTable,
                item,
                event.levelRequirement(),
                enchantments,
                org.bukkit.enchantments.Enchantment.WIND_BURST, //TODO correct this
                1, //TODO correct this
                event.option());
        fireEvent(bukkitEvent);
        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
