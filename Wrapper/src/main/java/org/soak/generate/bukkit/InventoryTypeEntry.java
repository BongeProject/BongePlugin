package org.soak.generate.bukkit;

import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ContainerType;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class InventoryTypeEntry {

    private final @NotNull String enumId;
    private @Nullable Predicate<Carrier> fromCarrier;
    private final @NotNull Predicate<Container> fromContainer;
    private @Nullable Supplier<Component> defaultName;

    public InventoryTypeEntry(@NotNull String enumId, @NotNull Predicate<Container> fromContainer) {
        this.enumId = enumId;
        this.fromContainer = fromContainer;
    }

    public @NotNull Component defaultName() {
        if (defaultName == null) {
            throw new RuntimeException("Default name called but no default name set on " + enumId);
        }
        return defaultName.get();
    }

    public InventoryTypeEntry setDefaultName(@NotNull Supplier<Component> defaultName) {
        this.defaultName = defaultName;
        return this;
    }

    public @NotNull Predicate<Container> fromContainer() {
        return fromContainer;
    }

    public @NotNull Predicate<Carrier> fromCarrier() {
        if (fromCarrier == null) {
            return carrier -> fromContainer().test((Container) carrier.inventory());
        }
        return fromCarrier;
    }

    public InventoryTypeEntry setFromCarrier(@Nullable Predicate<Carrier> fromCarrier) {
        this.fromCarrier = fromCarrier;
        return this;
    }

    public @NotNull String enumId() {
        return enumId;
    }

    public boolean is(Container container) {
        return fromContainer().test(container);
    }

    public boolean is(Carrier carrier) {
        return fromCarrier().test(carrier);
    }

    public <T extends Enum<T>> Enum<T> toType() {
        return (T) InventoryTypeList.values()
                .stream()
                .filter(enu -> enu.name().equals(this.enumId))
                .findFirst()
                .orElseThrow();
    }

    public static InventoryTypeEntry fromContainerType(String name, ContainerType type) {
        return new InventoryTypeEntry(name,
                                      container -> container.type()
                                              .map(t -> t.equals(type))
                                              .orElse(false)).setDefaultName(() -> Component.text(name.toLowerCase()
                                                                                                          .replaceAll(
                                                                                                                  "_",
                                                                                                                  " ")));
    }
}
