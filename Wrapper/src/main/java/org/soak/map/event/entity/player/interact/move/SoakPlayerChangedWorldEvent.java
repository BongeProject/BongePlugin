package org.soak.map.event.entity.player.interact.move;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.WrapperManager;
import org.soak.map.event.SoakEvent;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;

public class SoakPlayerChangedWorldEvent extends SoakEvent<RespawnPlayerEvent.SelectWorld, PlayerChangedWorldEvent> {

    public SoakPlayerChangedWorldEvent(Class<PlayerChangedWorldEvent> bukkitEvent, EventPriority priority, Plugin plugin, Listener listener, EventExecutor executor, boolean ignoreCancelled) {
        super(bukkitEvent, priority, plugin, listener, executor, ignoreCancelled);
    }

    @Override
    protected Class<RespawnPlayerEvent.SelectWorld> spongeEventClass() {
        return RespawnPlayerEvent.SelectWorld.class;
    }

    @Override
    public void handle(RespawnPlayerEvent.SelectWorld event) throws Exception {
        var spongePlayer = event.entity();
        var spongeFromWorld = spongePlayer.world();

        var bukkitPlayer = SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongePlayer);
        var bukkitWorld = SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongeFromWorld);
        PlayerChangedWorldEvent bukkitEvent = new PlayerChangedWorldEvent(bukkitPlayer, bukkitWorld);

        var soakContainer = SoakManager.getManager().getSoakContainer(this.plugin());
        Sponge.server().scheduler().executor(soakContainer.getTrueContainer()).execute(() -> fireEvent(bukkitEvent));
    }
}
