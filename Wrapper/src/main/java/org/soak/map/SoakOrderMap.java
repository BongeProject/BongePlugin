package org.soak.map;

import org.bukkit.event.EventPriority;
import org.spongepowered.api.event.Order;

public class SoakOrderMap {

    public static Order toSponge(EventPriority priority) {
        return switch (priority) {
            case LOWEST -> Order.LAST;
            case LOW -> Order.LATE;
            case NORMAL, MONITOR -> Order.DEFAULT;
            case HIGH -> Order.EARLY;
            case HIGHEST -> Order.FIRST;
        };
    }

}
