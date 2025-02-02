package org.soak.map.event;


import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.soak.map.event.block.*;
import org.soak.map.event.block.piston.SoakPistonExtendEvent;
import org.soak.map.event.block.piston.SoakPistonRetractEvent;
import org.soak.map.event.block.portal.SoakEndPortalCreateEvent;
import org.soak.map.event.block.portal.SoakNetherPortalCreateEvent;
import org.soak.map.event.command.SoakPreCommandEvent;
import org.soak.map.event.command.SoakServerCommandEvent;
import org.soak.map.event.entity.SoakCreatureSpawnEvent;
import org.soak.map.event.entity.SoakEntityDeathEvent;
import org.soak.map.event.entity.SoakEntityExplosionEvent;
import org.soak.map.event.entity.SoakEntityInteractWithBlockEvent;
import org.soak.map.event.entity.move.*;
import org.soak.map.event.entity.player.chat.SoakAsyncChatEvent;
import org.soak.map.event.entity.player.chat.SoakAsyncPlayerChatEvent;
import org.soak.map.event.entity.player.combat.SoakPlayerDeathEvent;
import org.soak.map.event.entity.player.combat.SoakPlayerRespawnEvent;
import org.soak.map.event.entity.player.connection.SoakPlayerJoinEvent;
import org.soak.map.event.entity.player.connection.SoakPlayerKickEvent;
import org.soak.map.event.entity.player.connection.SoakPlayerQuitEvent;
import org.soak.map.event.entity.player.connection.SoakPreJoinEvent;
import org.soak.map.event.entity.player.data.*;
import org.soak.map.event.entity.player.interact.SoakPlayerInteractAirEvent;
import org.soak.map.event.entity.player.interact.SoakPlayerInteractBlockEvent;
import org.soak.map.event.entity.player.interact.SoakPlayerInteractEntityEvent;
import org.soak.map.event.entity.player.interact.move.SoakPlayerChangedWorldEvent;
import org.soak.map.event.entity.player.interact.move.SoakPlayerMoveEvent;
import org.soak.map.event.entity.player.interact.move.SoakPlayerRotateEvent;
import org.soak.map.event.entity.player.interact.move.SoakPlayerTeleportEvent;
import org.soak.map.event.inventory.action.SoakEnchantEvent;
import org.soak.map.event.inventory.action.SoakInventoryCloseEvent;
import org.soak.map.event.inventory.action.click.SoakInventoryClickEvent;
import org.soak.map.event.inventory.action.craft.SoakCraftItemEvent;
import org.soak.map.event.inventory.action.furnance.SoakBurnItemEvent;
import org.soak.map.event.inventory.action.furnance.SoakSmeltItemEvent;
import org.soak.map.event.inventory.hand.action.SoakItemUseEvent;
import org.soak.map.event.server.SoakServerListPingEvent;
import org.soak.map.event.world.SoakChunkGenerateEvent;
import org.soak.map.event.world.SoakWorldLoadEvent;
import org.soak.map.event.world.SoakWorldUnloadEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

public class EventClassMapping {

    public interface EventCreator<BE extends Event> {

        SoakEvent<?, BE> create(Class<BE> bukkitClass, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin, boolean ignoreCancelled);
    }

    //not good practise ... but quick to type, as it only happens onload it should be fine performance wise
    static class ReflectionEventCreator<BE extends Event> implements EventCreator<BE> {

        private final Class<? extends SoakEvent<?, BE>> creatingClass;

        public ReflectionEventCreator(Class<? extends SoakEvent<?, BE>> creatingClass) {
            this.creatingClass = creatingClass;
        }

        @Override
        public SoakEvent<?, BE> create(Class<BE> bukkitClass, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin, boolean ignoreCancelled) {
            try {
                return (SoakEvent<?, BE>) creatingClass.getConstructors()[0].newInstance(bukkitClass, priority, plugin, listener, executor, ignoreCancelled);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <BE extends Event> Collection<EventCreator<BE>> soakEventClass(Class<BE> bukkitClass) {
        String name = bukkitClass.getName();
        if (name.equals(PluginEnableEvent.class.getName()) || name.equals(PluginDisableEvent.class.getName())) {
            //these are fired within Soak itself
            return Collections.emptyList();
        }
        if (name.equals(SignChangeEvent.class.getName())) {
            return reflection(SoakSignChangeEvent.class);
        }
        if (name.equals(VehicleMoveEvent.class.getName())) {
            return reflection(SoakVehicleMoveEvent.class, SoakVehicleRotateEvent.class);
        }
        if (name.equals(PlayerMoveEvent.class.getName())) {
            return reflection(SoakPlayerMoveEvent.class, SoakPlayerRotateEvent.class);
        }
        if (name.equals(PlayerTeleportEvent.class.getName())) {
            return reflection(SoakPlayerTeleportEvent.class);
        }
        if (name.equals(InventoryCloseEvent.class.getName())) {
            return reflection(SoakInventoryCloseEvent.class);
        }
        if (name.equals(PlayerInteractEvent.class.getName())) {
            return reflection(SoakPlayerInteractBlockEvent.class, SoakPlayerInteractAirEvent.class);
        }
        if (name.equals(PlayerInteractEntityEvent.class.getName())) {
            return reflection(SoakPlayerInteractEntityEvent.class);
        }
        if (name.equals(CraftItemEvent.class.getName())) {
            return reflection(SoakCraftItemEvent.class);
        }
        if (name.equals(WorldUnloadEvent.class.getName())) {
            return reflection(SoakWorldUnloadEvent.class);
        }
        if (name.equals(WorldLoadEvent.class.getName())) {
            return reflection(SoakWorldLoadEvent.class);
        }
        if (name.equals(PlayerKickEvent.class.getName())) {
            return reflection(SoakPlayerKickEvent.class);
        }
        if (name.equals(PlayerJoinEvent.class.getName())) {
            return reflection(SoakPlayerJoinEvent.class);
        }
        if (name.equals(AsyncPlayerPreLoginEvent.class.getName())) {
            return reflection(SoakPreJoinEvent.class);
        }
        if (name.equals(PlayerQuitEvent.class.getName())) {
            return reflection(SoakPlayerQuitEvent.class);
        }
        if (name.equals(EntityDeathEvent.class.getName())) {
            return reflection(SoakEntityDeathEvent.class);
        }
        if (name.equals(PlayerDeathEvent.class.getName())) {
            return reflection(SoakPlayerDeathEvent.class);
        }
        if (name.equals(PlayerCommandPreprocessEvent.class.getName())) {
            return reflection(SoakPreCommandEvent.class);
        }
        if (name.equals(PlayerToggleSprintEvent.class.getName())) {
            return reflection(SoakToggleSprintEvent.class);
        }
        if (name.equals(PlayerToggleSneakEvent.class.getName())) {
            return reflection(SoakToggleSneakEvent.class);
        }
        if (name.equals(EntityToggleGlideEvent.class.getName())) {
            return reflection(SoakToggleGlideEvent.class);
        }
        if (name.equals(FoodLevelChangeEvent.class.getName())) {
            return reflection(SoakFoodLevelChangeEvent.class);
        }
        if (name.equals(PlayerExpChangeEvent.class.getName())) {
            return reflection(SoakExpChangeEvent.class);
        }
        if (name.equals(PlayerRespawnEvent.class.getName())) {
            return reflection(SoakPlayerRespawnEvent.class);
        }
        if (name.equals(EnchantItemEvent.class.getName())) {
            return reflection(SoakEnchantEvent.class);
        }
        if (name.equals(FurnaceBurnEvent.class.getName())) {
            return reflection(SoakBurnItemEvent.class);
        }
        if (name.equals(FurnaceSmeltEvent.class.getName())) {
            return reflection(SoakSmeltItemEvent.class);
        }
        if (name.equals(PlayerItemConsumeEvent.class.getName())) {
            return reflection(SoakItemUseEvent.class);
        }
        if (name.equals(InventoryClickEvent.class.getName())) {
            return reflection(SoakInventoryClickEvent.class);
        }
        if (name.equals(BlockBreakEvent.class.getName())) {
            return reflection(SoakBlockBreakEvent.class);
        }
        if (name.equals(BlockPlaceEvent.class.getName())) {
            return reflection(SoakBlockPlaceEvent.class);
        }
        if (name.equals(BlockPhysicsEvent.class.getName())) {
            return reflection(SoakBlockPhysicsEvent.class);
        }
        if (name.equals(BlockFromToEvent.class.getName())) {
            return reflection(SoakBlockFlowExpandEvent.class);
        }
        if (name.equals(EntityBlockFormEvent.class.getName())) {
            return reflection(SoakBlockPlaceByEntityEvent.class);
        }
        if (name.equals(EntityExplodeEvent.class.getName())) {
            return reflection(SoakEntityExplosionEvent.class);
        }
        if (name.equals(BlockPistonExtendEvent.class.getName())) {
            return reflection(SoakPistonExtendEvent.class);
        }
        if (name.equals(BlockPistonRetractEvent.class.getName())) {
            return reflection(SoakPistonRetractEvent.class);
        }
        if (name.equals(PlayerChangedWorldEvent.class.getName())) {
            return reflection(SoakPlayerChangedWorldEvent.class);
        }
        if (name.equals(EntityPortalEnterEvent.class.getName())) {
            return reflection(SoakEntityEnterPortalEvent.class);
        }
        if (name.equals(EntityPortalExitEvent.class.getName())) {
            return reflection(SoakEntityExitPortalEvent.class);
        }
        if (name.equals(EntityPortalEvent.class.getName())) {
            return reflection(SoakPortalTeleportEntityEvent.class);
        }
        if (name.equals(CreatureSpawnEvent.class.getName())) {
            return reflection(SoakCreatureSpawnEvent.class);
        }
        if (name.equals(PortalCreateEvent.class.getName())) {
            return reflection(SoakNetherPortalCreateEvent.class, SoakEndPortalCreateEvent.class);
        }
        if (name.equals(ServerListPingEvent.class.getName())) {
            return reflection(SoakServerListPingEvent.class);
        }
        if (name.equals(ServerCommandEvent.class.getName())) {
            return reflection(SoakServerCommandEvent.class);
        }
        if (name.equals(AsyncPlayerChatEvent.class.getName())) {
            return reflection(SoakAsyncPlayerChatEvent.class);
        }
        if (name.equals(AsyncChatEvent.class.getName())) {
            return reflection(SoakAsyncChatEvent.class);
        }
        if (name.equals(BlockBurnEvent.class.getName())) {
            return reflection(SoakBlockBurnEvent.class);
        }
        if (name.equals(LeavesDecayEvent.class.getName())) {
            return reflection(SoakLeavesDecayEvent.class);
        }
        if (name.equals(EntityInteractEvent.class.getName())) {
            return reflection(SoakEntityInteractWithBlockEvent.class);
        }
        if(name.equals(ChunkPopulateEvent.class.getName())){
            return reflection(SoakChunkGenerateEvent.class);
        }
        throw new RuntimeException("No mapping found for Bukkit Event: " + bukkitClass.getName());
    }

    private static <E extends Event, BE extends Event> Collection<EventCreator<E>> reflection(Class<? extends SoakEvent<?, BE>>... values) {
        return Stream.of(values).map(clazz -> (EventCreator<E>) new ReflectionEventCreator<>(clazz)).toList();
    }
}
