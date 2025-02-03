package org.soak.map;

import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class SoakMessageMap {

    public static String mapToBukkit(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public static Component toComponent(String message) {
        if (message == null) {
            return Component.empty();
        }
        return LegacyComponentSerializer.legacySection().deserialize(message);
    }

    @Deprecated
    public static Component toComponent(BaseComponent... components) {
        var jsonText = ComponentSerializer.toString(components);
        return GsonComponentSerializer.gson().deserialize(jsonText);
    }

    @Deprecated
    public static MessageType toComponent(ChatMessageType type) {
        return switch (type) {
            case CHAT -> MessageType.CHAT;
            case SYSTEM -> MessageType.SYSTEM;
            case ACTION_BAR -> MessageType.SYSTEM; //TODO
            default -> throw new RuntimeException("No mapping for " + type.name());
        };
    }
}
