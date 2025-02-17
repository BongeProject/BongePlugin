package org.soak.map.event.block;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.SoakActionMap;
import org.soak.map.event.SoakEvent;
import org.soak.map.item.SoakItemStackMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlock;
import org.soak.wrapper.block.SoakBlockSnapshot;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.ContextValue;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStackLike;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.blockray.RayTrace;

public class SoakBlockPlaceEvent extends SoakEvent<ChangeBlockEvent.All, BlockPlaceEvent> {

    public SoakBlockPlaceEvent(Class<BlockPlaceEvent> bukkitEvent, EventPriority priority, Plugin plugin,
                               Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ChangeBlockEvent.All> spongeEventClass() {
        return ChangeBlockEvent.All.class;
    }

    @Override
    public void handle(ChangeBlockEvent.All event) throws Exception {
        var opSpongePlayer = event.cause().first(ServerPlayer.class);
        if (opSpongePlayer.isEmpty()) {
            return;
        }
        var opUsedItem = event.context().get(EventContextKeys.USED_ITEM);
        if (opUsedItem.isEmpty()) {
            return;
        }
        var spongePlayer = opSpongePlayer.get();
        var usedItemSnapshot = opUsedItem.get();
        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongePlayer);
        var opRaytrace = RayTrace.block()
                .direction(spongePlayer)
                .sourceEyePosition(spongePlayer)
                .continueWhileBlock(RayTrace.onlyAir())
                .limit(7)
                .execute();
        if (opRaytrace.isEmpty()) {
            return;
        }
        var placedAgainst = new SoakBlock(opRaytrace.get().selectedObject().serverLocation());
        if (opRaytrace.isEmpty()) {
            return;
        }
        var usedItem = SoakItemStackMap.toBukkit(usedItemSnapshot);
        var canBuild = true; //always true?
        var usedHand = event.context()
                .get(EventContextKeys.USED_HAND)
                .map(SoakActionMap::toBukkit)
                .orElseThrow(() -> new RuntimeException("Cannot get the used hand to place the block"));

        event.transactions(Operations.PLACE.get()).forEach(transaction -> {
            var originalBlock = new SoakBlockSnapshot(transaction.original());
            var newBlock = new SoakBlockSnapshot(transaction.custom().orElseGet(transaction::finalReplacement));

            var bukkitEvent = new BlockPlaceEvent(originalBlock,
                                                  newBlock.getState(),
                                                  placedAgainst,
                                                  usedItem,
                                                  player,
                                                  canBuild,
                                                  usedHand);
            fireEvent(bukkitEvent);
            if (bukkitEvent.isCancelled()) {
                transaction.invalidate();
            }
            //TODO change can build?
        });
    }
}
