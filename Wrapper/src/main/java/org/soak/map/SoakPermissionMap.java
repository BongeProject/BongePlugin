package org.soak.map;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.util.TriState;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.plugin.PluginContainer;

import java.util.*;

public class SoakPermissionMap {

    public static Permission toBukkit(PermissionDescription description) {
        PermissionDefault defaultPermission = switch (description.defaultValue()) {
            case TRUE -> PermissionDefault.OP;
            case FALSE -> PermissionDefault.FALSE;
            case UNDEFINED -> PermissionDefault.NOT_OP;
        };
        return new Permission(description.id(),
                              description.description().map(SoakMessageMap::mapToBukkit).orElse(""),
                              defaultPermission);
    }

    @NotNull
    @CheckReturnValue
    public static Collection<PermissionDescription> fromBukkit(@NotNull Permission perm,
                                                               @Nullable PluginContainer plugin) {
        Map<String, Boolean> bukkitPerms = Map.of(perm.getName(), fromBukkit(perm.getDefault()).asBoolean());
        if (perm.getName().contains("*")) {
            bukkitPerms = perm.getChildren();
        }
        var permissionService = Sponge.server().serviceProvider().permissionService();

        Collection<PermissionDescription> alreadyRegistered = bukkitPerms.keySet()
                .stream()
                .map(id -> permissionService.descriptions()
                        .stream()
                        .filter(desc -> desc.id().equals(perm.getName()))
                        .findAny())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(p -> (PermissionDescription) p)
                .toList();
        if (plugin == null) {
            return alreadyRegistered;
        }

        var newRegisters = bukkitPerms.entrySet()
                .stream()
                .filter(entry -> alreadyRegistered.stream().noneMatch(desc -> desc.id().equals(entry.getKey())))
                .map(entry -> {
                    return permissionService.newDescriptionBuilder(plugin)
                            .id(entry.getKey())
                            .description(SoakMessageMap.toComponent(perm.getDescription()))
                            .defaultValue(Tristate.fromBoolean(entry.getValue()))
                            .register();
                })
                .toList();

        Collection<PermissionDescription> result = new LinkedList<>(alreadyRegistered);
        result.addAll(newRegisters);
        return result;

    }

    public static Tristate fromBukkit(PermissionDefault permissionDefault) {
        return switch (permissionDefault) {
            case TRUE, OP -> Tristate.TRUE;
            case FALSE, NOT_OP -> Tristate.FALSE;
        };
    }
}
