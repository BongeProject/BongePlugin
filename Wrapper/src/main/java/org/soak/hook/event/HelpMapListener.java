package org.soak.hook.event;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.help.HelpTopic;
import org.soak.WrapperManager;
import org.soak.map.SoakSubjectMap;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.ExecuteCommandEvent;
import org.spongepowered.api.service.permission.Subject;

import java.util.Comparator;

public class HelpMapListener {

    @Listener(order = Order.LAST)
    public void showExtraHelpTopics(ExecuteCommandEvent.Pre event) {
        var commandLower = event.command().toLowerCase();
        if (!commandLower.equals("help")) {
            return;
        }
        if (!event.result().map(CommandResult::isSuccess).orElse(true)) {
            return;
        }

        int page = 1;

        try {
            var pageNumberString = event.arguments();
            if (pageNumberString.contains(" ")) {
                pageNumberString = pageNumberString.substring(0, pageNumberString.indexOf(' '));
            }
            page = Integer.parseInt(pageNumberString);
        } catch (NumberFormatException ignored) {
        }


        if (page != 1) {
            return;
        }

        var commandSender = SoakSubjectMap.mapToBukkit((Subject) event.commandCause().root());
        var helpMap = SoakManager.<WrapperManager>getManager().getServer().getHelpMap();
        var audience = event.commandCause().audience();
        helpMap.getHelpTopics()
                .stream()
                .filter(topic -> topic.canSee(commandSender))
                .sorted(Comparator.comparing(HelpTopic::getName))
                .forEach(topic -> {
                    Component message = Component.text(topic.getName() + ": ").color(TextColor.color(100, 100, 0));
                    message = message.append(Component.text(topic.getShortText())
                                                     .color(TextColor.color(200, 200, 200)));
                    audience.sendMessage(message);
                });


    }
}
