package org.soak.map.event.block;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.map.SoakVectorMap;
import org.soak.map.event.SoakEvent;
import org.soak.map.item.SoakItemStackMap;
import org.soak.wrapper.block.SoakBlock;
import org.spongepowered.api.block.entity.carrier.CarrierBlockEntity;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.util.Optional;

public class SoakBlockDispenseEvent extends SoakEvent<DropItemEvent.Dispense, BlockDispenseEvent> {

    public SoakBlockDispenseEvent(Class<BlockDispenseEvent> bukkitEvent, EventPriority priority, Plugin plugin,
                                  Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<DropItemEvent.Dispense> spongeEventClass() {
        return DropItemEvent.Dispense.class;
    }

    @Override
    public void handle(DropItemEvent.Dispense event) throws Exception {
        var opInventory = event.cause()
                .first(CarriedInventory.class)
                .map(inv -> (CarriedInventory<? extends Carrier>) inv);
        if (opInventory.isEmpty()) {
            return;
        }
        Optional<CarrierBlockEntity> opBlock = opInventory.get()
                .carrier()
                .filter(carrier -> carrier instanceof CarrierBlockEntity)
                .map(carrier -> (CarrierBlockEntity) carrier);
        if (opBlock.isEmpty()) {
            return;
        }

        var bukkitBlock = new SoakBlock(opBlock.get().serverLocation());
        event.entities()
                .stream()
                .filter(entity -> entity instanceof Item)
                .map(entity -> (Item) entity)
                .forEach(item -> {
                    var itemStack = SoakItemStackMap.toBukkit(item.get(Keys.ITEM_STACK_SNAPSHOT)
                                                                      .orElseThrow(() -> new IllegalStateException(
                                                                              "Entity item does not have item")));
                    var velocity = SoakVectorMap.toBukkit(item.velocity().get());
                    var bukkitEvent = new BlockDispenseEvent(bukkitBlock, itemStack, velocity);
                    fireEvent(bukkitEvent);
                    if (bukkitEvent.isCancelled()) {
                        event.setCancelled(true);
                    }
                });
    }
}
