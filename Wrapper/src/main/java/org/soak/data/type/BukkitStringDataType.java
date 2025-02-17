package org.soak.data.type;

import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;

import java.util.Optional;

public class BukkitStringDataType implements BukkitDataType<String> {

    @Override
    public Optional<String> get(DataView view, DataQuery query) {
        return view.getString(query);
    }

    @Override
    public String typeName() {
        return "string";
    }

    @Override
    public Class<String> typeClass() {
        return String.class;
    }

    @Override
    public DataContainer set(DataContainer container, DataQuery from, String value) {
        return container.set(from, value);
    }

    @Override
    public PersistentDataType<?, String> toBukkit() {
        return PersistentDataType.STRING;
    }
}
