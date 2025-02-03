package org.soak.wrapper.block.state;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.block.Dispenser;
import org.bukkit.loot.LootTable;
import org.bukkit.projectiles.BlockProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.plugin.SoakManager;
import org.soak.utils.DataOverride;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.Objects;

public class DispenserBlockState extends CarrierInventoryState implements Dispenser {

    private DataOverride<Component> customName = new DataOverride<>(() -> spongeEntity().flatMap(entity -> entity.get(Keys.CUSTOM_NAME)).orElse(null));
    private DataOverride<String> lockedToken = new DataOverride<>(() -> spongeEntity().flatMap(entity -> entity.get(Keys.LOCK_TOKEN)).orElse(null));

    public DispenserBlockState(@NotNull ServerLocation location, boolean isSnapshot) {
        super(location, isSnapshot);
    }

    public DispenserBlockState(@Nullable ServerLocation location, @NotNull BlockState state, boolean isSnapshot) {
        super(location, state, isSnapshot);
    }

    private DispenserBlockState(@Nullable ServerLocation location, @NotNull BlockState state, boolean isSnapshot, DataOverride<Component> customName, DataOverride<String> lockedToken) {
        this(location, state, isSnapshot);
        this.lockedToken = lockedToken;
        this.customName = customName;
    }

    @Override
    protected Inventory createMockedInventory() {
        return Inventory.builder().slots(9).completeStructure().plugin(SoakManager.getManager()).build();
    }

    @Override
    public @Nullable BlockProjectileSource getBlockProjectileSource() {
        throw NotImplementedException.createByLazy(Dispenser.class, "getBlockProjectileSource");
    }

    @Override
    public boolean dispense() {
        var opBlockEntity = this.spongeEntity().filter(entity -> entity instanceof org.spongepowered.api.block.entity.carrier.Dispenser).map(entity -> (Dispenser) entity);
        if (opBlockEntity.isEmpty()) {
            return false;
        }
        var dispenser = opBlockEntity.get();
        return false;
    }


    @Override
    public @Nullable Component customName() {
        return this.customName.get();
    }

    @Override
    public void customName(@Nullable Component component) {
        this.customName.set(component);
    }

    @Override
    public @Nullable String getCustomName() {
        var name = customName();
        if (name == null) {
            return null;
        }
        return LegacyComponentSerializer.legacySection().serialize(name);
    }

    @Override
    public void setCustomName(@Nullable String s) {
        if (s == null) {
            customName = null;
            return;
        }
        customName(LegacyComponentSerializer.legacySection().deserialize(s));
    }

    @Override
    public boolean isLocked() {
        return this.lockedToken.get() == null;
    }

    @Override
    public @NotNull String getLock() {
        return Objects.requireNonNullElse(this.lockedToken.get(), "");
    }

    @Override
    public void setLock(@Nullable String s) {
        this.lockedToken.set(s);
    }

    @Override
    protected AbstractBlockState createCopy(@Nullable ServerLocation location, @NotNull BlockState state) {
        return new DispenserBlockState(location, state, this.isSnapshot(), this.customName, this.lockedToken);
    }

    @Override
    protected void onPostApply(@NotNull ServerLocation location) {
        customName.applyTo((customName) -> location.offer(Keys.CUSTOM_NAME, customName));
        lockedToken.applyTo(token -> location.offer(Keys.LOCK_TOKEN, token));
    }
}
