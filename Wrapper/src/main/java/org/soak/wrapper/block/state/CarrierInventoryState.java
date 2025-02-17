package org.soak.wrapper.block.state;

import com.destroystokyo.paper.loottable.LootableBlockInventory;
import io.papermc.paper.block.TileStateInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.map.item.inventory.SoakInventoryMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.inventory.SoakInventory;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.entity.carrier.CarrierBlockEntity;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.UUID;

public abstract class CarrierInventoryState extends AbstractTileState
        implements LootableBlockInventory, TileStateInventoryHolder {

    private Inventory snapshot;

    public CarrierInventoryState(@NotNull ServerLocation location, boolean isSnapshot) {
        super(location, isSnapshot);
    }

    public CarrierInventoryState(@Nullable ServerLocation location, @NotNull BlockState state, boolean isSnapshot) {
        super(location, state, isSnapshot);
    }

    @Override
    public boolean isRefillEnabled() {
        throw NotImplementedException.createByLazy(LootableBlockInventory.class, "isRefillEnabled");
    }

    @Override
    public boolean hasBeenFilled() {
        throw NotImplementedException.createByLazy(LootableBlockInventory.class, "getBeenFilled");
    }

    @Override
    public boolean canPlayerLoot(@NotNull UUID uuid) {
        throw NotImplementedException.createByLazy(LootableBlockInventory.class, "canPlayerLoot", UUID.class);
    }

    @Override
    public boolean hasPlayerLooted(@NotNull UUID uuid) {
        throw NotImplementedException.createByLazy(LootableBlockInventory.class, "hasPlayerLooted", UUID.class);
    }

    @Override
    public @Nullable Long getLastLooted(@NotNull UUID uuid) {
        throw NotImplementedException.createByLazy(LootableBlockInventory.class, "getLastLooted", UUID.class);
    }

    @Override
    public boolean setHasPlayerLooted(@NotNull UUID uuid, boolean b) {
        throw NotImplementedException.createByLazy(LootableBlockInventory.class,
                                                   "setHasPlayerLooted",
                                                   UUID.class,
                                                   boolean.class);
    }

    @Override
    public boolean hasPendingRefill() {
        throw NotImplementedException.createByLazy(LootableBlockInventory.class, "hasPendingRefill");
    }

    @Override
    public long getLastFilled() {
        throw NotImplementedException.createByLazy(LootableBlockInventory.class, "getLastFilled");
    }

    @Override
    public long getNextRefill() {
        throw NotImplementedException.createByLazy(LootableBlockInventory.class, "getNextRefill");
    }

    @Override
    public long setNextRefill(long l) {
        throw NotImplementedException.createByLazy(LootableBlockInventory.class, "setNextRefill", long.class);
    }

    @Override
    public void setLootTable(@Nullable LootTable lootTable) {
        throw NotImplementedException.createByLazy(LootableBlockInventory.class, "setLootTable", LootTable.class);
    }

    @Override
    public @Nullable LootTable getLootTable() {
        throw NotImplementedException.createByLazy(LootableBlockInventory.class, "getLootTable");
    }

    @Override
    public void setLootTable(@Nullable LootTable lootTable, long l) {
        throw NotImplementedException.createByLazy(LootableBlockInventory.class,
                                                   "setLootTable",
                                                   LootTable.class,
                                                   long.class);
    }

    @Override
    public void setSeed(long l) {
        throw NotImplementedException.createByLazy(LootableBlockInventory.class, "setSeed", long.class);
    }

    @Override
    public long getSeed() {
        throw NotImplementedException.createByLazy(LootableBlockInventory.class, "getSeed");
    }

    @Override
    public @NotNull Inventory getInventory() {
        var opBlockEntity = this.spongeEntity()
                .filter(entity -> entity instanceof CarrierBlockEntity)
                .map(entity -> (CarrierBlockEntity) entity);
        if (opBlockEntity.isEmpty()) {
            return getSnapshotInventory();
        }
        var bukkit = SoakInventoryMap.toBukkit(opBlockEntity.get());
        if (this.snapshot == null) {
            var spongeInventory = org.spongepowered.api.item.inventory.Inventory.builder()
                    .inventory(opBlockEntity.get().inventory())
                    .completeStructure()
                    .plugin(SoakManager.getManager().getOwnContainer())
                    .carrier(opBlockEntity.get())
                    .build();
            this.snapshot = SoakInventory.wrap(spongeInventory);
        }
        return bukkit;
    }

    protected abstract org.spongepowered.api.item.inventory.Inventory createMockedInventory();

    @Override
    public @NotNull Inventory getSnapshotInventory() {
        if (snapshot == null) {
            this.snapshot = SoakInventory.wrap(createMockedInventory());
        }
        return this.snapshot;
    }
}
