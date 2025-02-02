package org.soak.map.event.world;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.map.event.SoakEvent;
import org.soak.wrapper.world.chunk.AbstractSoakChunk;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.world.chunk.ChunkEvent;

public class SoakChunkGenerateEvent extends SoakEvent<ChunkEvent.Generated, ChunkPopulateEvent> {

    public SoakChunkGenerateEvent(Class<ChunkPopulateEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<ChunkEvent.Generated> spongeEventClass() {
        return ChunkEvent.Generated.class;
    }

    @Override
    public void handle(ChunkEvent.Generated event) throws Exception {
        var opWorld = Sponge.server().worldManager().world(event.worldKey());
        if(opWorld.isEmpty()){
            return;
        }
        var spongeChunk = opWorld.get().chunk(event.chunkPosition());
        var bukkitChunk = new AbstractSoakChunk(spongeChunk);

        var bukkitEvent = new ChunkPopulateEvent(bukkitChunk);
        fireEvent(bukkitEvent);

        //TODO setting
    }
}
