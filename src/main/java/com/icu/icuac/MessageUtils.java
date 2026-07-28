package com.icu.icuac;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class MessageUtils {
    public static final String DEFAULT_PREFIX = "&#00D2FF&l[&#3A7BD5&lICUAC&#00D2FF&l] &8&l┃ &r";
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&').hexColors().build();

    private MessageUtils() {}

    public static String format(ICUAC plugin, String message, String... replacements) {
        if (message == null || message.isEmpty()) return "";
        String prefix = plugin.getLanguageManager().getMessageString("prefix", DEFAULT_PREFIX);
        if (prefix == null || prefix.isBlank()) prefix = DEFAULT_PREFIX;
        String formatted = replace(message.replace("{prefix}", prefix), replacements);
        return LegacyComponentSerializer.legacySection().serialize(SERIALIZER.deserialize(formatted));
    }

    public static void send(CommandSender sender, ICUAC plugin, String path, String... replacements) {
        Object value = plugin.getLanguageManager().getMessage(path);
        String prefix = plugin.getLanguageManager().getMessageString("prefix", DEFAULT_PREFIX);
        if (prefix == null || prefix.isBlank()) prefix = DEFAULT_PREFIX;
        if (value instanceof String message) {
            sendRaw(sender, message.replace("{prefix}", prefix), replacements);
        } else if (value instanceof List<?> lines) {
            for (Object line : lines) {
                if (line instanceof String message) {
                    sendRaw(sender, message.replace("{prefix}", prefix), replacements);
                }
            }
        }
    }

    public static void sendRaw(CommandSender sender, String message, String... replacements) {
        if (message == null || message.isEmpty()) return;
        for (String line : message.split("\\n", -1)) {
            sender.sendMessage(SERIALIZER.deserialize(replace(line, replacements)));
        }
    }

    private static String replace(String message, String... replacements) {
        String result = message;
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            result = result.replace("{" + replacements[i] + "}", replacements[i + 1]);
        }
        return result;
    }
}
