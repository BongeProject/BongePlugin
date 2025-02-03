package org.soak.utils.log;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomLoggerFormat extends Formatter {

    public static final Map<String, String> LEVEL_COLOURS;

    static {
        LEVEL_COLOURS = Map.of(System.Logger.Level.ERROR.getName(),
                               AnsiEscapeCodes.RED,
                               System.Logger.Level.WARNING.getName(),
                               AnsiEscapeCodes.YELLOW,
                               System.Logger.Level.DEBUG.getName(),
                               AnsiEscapeCodes.CYAN,
                               System.Logger.Level.INFO.getName(), AnsiEscapeCodes.GREEN);
    }

    private String formatInt(int value) {
        if (value >= 10) {
            return String.valueOf(value);
        }
        return "0" + value;
    }


    @Override
    public String format(LogRecord record) {
        var time = LocalTime.ofInstant(record.getInstant(), ZoneId.systemDefault());

        String levelColour = LEVEL_COLOURS.getOrDefault(record.getLevel().getName(), AnsiEscapeCodes.RESET);

        String timeString =
                "[" + formatInt(time.getHour()) + ":" + formatInt(time.getMinute()) + ":" + formatInt(time.getSecond()) + "]";
        String threadString = "[" + Thread.currentThread().getName() + "/" + levelColour + record.getLevel()
                .getName() + AnsiEscapeCodes.RESET + "]";
        String loggerName = "[" + record.getLoggerName() + "]";
        String messageColour = (record.getLevel().getName().equalsIgnoreCase("info") ? "" : levelColour);
        String message = (formatMessage(record.getMessage(), record.getParameters())) + "\n";

        return AnsiEscapeCodes.RESET + timeString + " " + threadString + " " + loggerName + ": " + messageColour + message + AnsiEscapeCodes.RESET;
    }

    @SuppressWarnings("deprecation")
    private String formatMessage(String message, @Nullable Object... arguments) {
        message = message.replaceAll(ChatColor.AQUA.toString(), AnsiEscapeCodes.CYAN);
        message = message.replaceAll(ChatColor.BLACK.toString(), AnsiEscapeCodes.BLACK);
        message = message.replaceAll(ChatColor.BLUE.toString(), AnsiEscapeCodes.BLUE);
        message = message.replaceAll(ChatColor.DARK_AQUA.toString(), AnsiEscapeCodes.CYAN);
        message = message.replaceAll(ChatColor.DARK_BLUE.toString(), AnsiEscapeCodes.BLUE);
        message = message.replaceAll(ChatColor.DARK_GRAY.toString(), AnsiEscapeCodes.WHITE);
        message = message.replaceAll(ChatColor.DARK_GREEN.toString(), AnsiEscapeCodes.GREEN);
        message = message.replaceAll(ChatColor.DARK_PURPLE.toString(), AnsiEscapeCodes.PURPLE);
        message = message.replaceAll(ChatColor.DARK_RED.toString(), AnsiEscapeCodes.RED);
        message = message.replaceAll(ChatColor.GOLD.toString(), AnsiEscapeCodes.YELLOW);
        message = message.replaceAll(ChatColor.GRAY.toString(), AnsiEscapeCodes.WHITE);
        message = message.replaceAll(ChatColor.GREEN.toString(), AnsiEscapeCodes.GREEN);
        message = message.replaceAll(ChatColor.LIGHT_PURPLE.toString(), AnsiEscapeCodes.PURPLE);
        message = message.replaceAll(ChatColor.RED.toString(), AnsiEscapeCodes.RED);
        message = message.replaceAll(ChatColor.WHITE.toString(), AnsiEscapeCodes.WHITE);
        message = message.replaceAll(ChatColor.YELLOW.toString(), AnsiEscapeCodes.YELLOW);
        message = message.replaceAll(ChatColor.RESET.toString(), AnsiEscapeCodes.WHITE);
        if (arguments == null) {
            return message;
        }
        for (int i = 0; i < arguments.length; i++) {
            var argument = arguments[i];
            message = message.replace("{" + i + "}", stringMapping(Objects.requireNonNull(argument)));
        }
        return message;
    }

    private String stringMapping(Object object) {
        return object.toString();
    }
}
