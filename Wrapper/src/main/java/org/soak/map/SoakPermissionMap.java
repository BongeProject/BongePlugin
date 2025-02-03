package org.soak.map;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.spongepowered.api.service.permission.PermissionDescription;

public class SoakPermissionMap {

    public static Permission toBukkit(PermissionDescription description) {
        PermissionDefault defaultPermission = switch (description.defaultValue()) {
            case TRUE -> PermissionDefault.OP;
            case FALSE -> PermissionDefault.FALSE;
            case UNDEFINED -> PermissionDefault.NOT_OP;
        };
        return new Permission(description.id(), description.description().map(SoakMessageMap::mapToBukkit).orElse(""), defaultPermission);
    }
}
