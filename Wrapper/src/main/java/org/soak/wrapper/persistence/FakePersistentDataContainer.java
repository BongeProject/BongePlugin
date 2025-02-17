package org.soak.wrapper.persistence;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.soak.data.BukkitPersistentData;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakPersistentDataMap;
import org.soak.map.SoakResourceKeyMap;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class FakePersistentDataContainer implements PersistentDataContainer, SoakPersistentData {

    private final BukkitPersistentData data = new BukkitPersistentData();

    @Override
    public <P, C> void set(@NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<P, C> persistentDataType
            , @NotNull C c) {
        data.addValue(SoakResourceKeyMap.mapToSponge(namespacedKey),
                      SoakPersistentDataMap.toSoak(persistentDataType),
                      c);
    }

    @Override
    public void remove(@NotNull NamespacedKey namespacedKey) {
        data.removeValue(SoakResourceKeyMap.mapToSponge(namespacedKey));
    }

    @Override
    public void readFromBytes(byte @NotNull [] bytes, boolean b) throws IOException {
        throw NotImplementedException.createByLazy(PersistentDataContainer.class,
                                                   "readFromBytes",
                                                   byte.class,
                                                   boolean.class);
    }

    @Override
    public <P, C> boolean has(@NotNull NamespacedKey namespacedKey,
                              @NotNull PersistentDataType<P, C> persistentDataType) {
        return this.getOptional(namespacedKey, persistentDataType).isPresent();

    }

    @Override
    public boolean has(@NotNull NamespacedKey namespacedKey) {
        return data.getValue(SoakResourceKeyMap.mapToSponge(namespacedKey)).isPresent();

    }

    @Override
    public <P, C> @Nullable C get(@NotNull NamespacedKey namespacedKey,
                                  @NotNull PersistentDataType<P, C> persistentDataType) {
        return getOptional(namespacedKey, persistentDataType).orElse(null);
    }

    @Override
    public <P, C> @NotNull C getOrDefault(@NotNull NamespacedKey namespacedKey,
                                          @NotNull PersistentDataType<P, C> persistentDataType, @NotNull C c) {
        return getOptional(namespacedKey, persistentDataType).orElse(c);

    }

    @Override
    public @NotNull Set<NamespacedKey> getKeys() {
        throw NotImplementedException.createByLazy(PersistentDataContainer.class, "getKeys");
    }

    @Override
    public boolean isEmpty() {
        throw NotImplementedException.createByLazy(PersistentDataContainer.class, "isEmpty");
    }

    @Override
    public void copyTo(@NotNull PersistentDataContainer persistentDataContainer, boolean b) {
        throw NotImplementedException.createByLazy(PersistentDataContainer.class,
                                                   "copyTo",
                                                   PersistentDataContainer.class,
                                                   boolean.class);
    }

    @Override
    public @NotNull PersistentDataAdapterContext getAdapterContext() {
        return new SoakPersistentDataContext();
    }

    @Override
    @NotNull
    public byte[] serializeToBytes() throws IOException {
        throw NotImplementedException.createByLazy(PersistentDataContainer.class, "serializeToBytes");
    }

    private <T, Z> Optional<Z> getOptional(NamespacedKey bukkitKey, PersistentDataType<T, Z> bukkitType) {
        var key = SoakResourceKeyMap.mapToSponge(bukkitKey);
        var type = SoakPersistentDataMap.toSoak(bukkitType);
        return data.getValue(key, type);
    }

    @Override
    public BukkitPersistentData wrapper() {
        return this.data;
    }
}
