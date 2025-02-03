package org.soak.map.event.block.piston;

import org.bukkit.block.Block;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.SoakDirectionMap;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlockSnapshot;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.entity.Piston;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;

import java.util.stream.Collectors;

public class SoakPistonRetractEvent extends SoakEvent<NotifyNeighborBlockEvent, BlockPistonRetractEvent> {

    public SoakPistonRetractEvent(Class<BlockPistonRetractEvent> bukkitEvent, EventPriority priority, Plugin plugin,
                                  Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<NotifyNeighborBlockEvent> spongeEventClass() {
        return NotifyNeighborBlockEvent.class;
    }

    @Override
    public void handle(NotifyNeighborBlockEvent event) {
        var opPiston = event.cause().first(Piston.class);
        if (opPiston.isEmpty()) {
            return;
        }
        var piston = opPiston.get();
        //The piston here is the head position. When retracting this may show the block that retracted rather than
        // the piston
        //the pistons body is stored in the piston. Ill contact Sponge for a none implementation specific way to get
        // this
        BlockState state;
        try {
            var classType = Class.forName("net.minecraft.world.level.block.entity.BlockEntity");
            var blockStateField = classType.getDeclaredField("blockState");
            blockStateField.setAccessible(true);
            state = (BlockState) blockStateField.get(piston);
        } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        var isExtended = piston.extending().get();
        if (isExtended) {
            return;
        }
        var pistonDirection = state.get(Keys.DIRECTION).orElseThrow(() -> new RuntimeException("Cannot get direction"));
        var pistonLocation = piston.serverLocation().relativeToBlock(pistonDirection.opposite());
        var pistonBody = BlockSnapshot.builder()
                .blockState(state)
                .world(pistonLocation.world().properties())
                .position(pistonLocation.blockPosition())
                .build();

        var bukkitPiston = new SoakBlockSnapshot(pistonBody);
        var bukkitDirection = SoakDirectionMap.toBukkit(pistonDirection);
        var movedBlocks = event.tickets()
                .stream()
                .map(ticket -> (Block) new SoakBlockSnapshot(ticket.target()))
                .collect(Collectors.toList());

        var bukkitEvent = new BlockPistonRetractEvent(bukkitPiston, movedBlocks, bukkitDirection);
        fireEvent(bukkitEvent);
    }
}
