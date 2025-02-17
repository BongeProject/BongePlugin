package org.soak.data.type;

import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;

import java.util.Optional;

public class BukkitByteDataType implements BukkitDataType<Byte> {

    @Override
    public Optional<Byte> get(DataView view, DataQuery from) {
        return view.getByte(from);
    }

    @Override
    public String typeName() {
        return "byte";
    }

    @Override
    public Class<?> typeClass() {
        return byte.class;
    }

    @Override
    public boolean isType(Class<?> classType) {
        return classType == byte.class || classType == Byte.class;
    }

    @Override
    public DataContainer set(DataContainer container, DataQuery from, Byte value) {
        return container.set(from, value);
    }

    @Override
    public PersistentDataType<?, Byte> toBukkit() {
        return PersistentDataType.BYTE;
    }
}
