package com.icu.icuac;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.regex.Pattern;

public final class MessageUtils {
    public static final String DEFAULT_PREFIX = "&#00D2FF&l[&#3A7BD5&lICUAC&#00D2FF&l] &8&l┃ &r";
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&').hexColors().build();
    private static final Pattern COLOR_CODES = Pattern.compile("(?i)&#[0-9a-f]{6}|&[0-9a-fk-or]");
    private static final Pattern DIVIDER_LINE = Pattern.compile("[-=+|┃✧\\s]+");

    private MessageUtils() {}

    public static String format(ICUAC plugin, String message, String... replacements) {
        if (message == null || message.isEmpty()) return "";
        String prefix = plugin.getLanguageManager().getMessageString("prefix", DEFAULT_PREFIX);
        if (prefix == null || prefix.isBlank()) prefix = DEFAULT_PREFIX;
        String formatted = replace(message.replace("{prefix}", prefix), replacements);
        return LegacyComponentSerializer.legacySection().serialize(deserialize(formatted));
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
            sender.sendMessage(deserialize(replace(line, replacements)));
        }
    }

    private static Component deserialize(String message) {
        Component component = SERIALIZER.deserialize(message);
        return isDivider(message) ? component : forceBold(component);
    }

    private static Component forceBold(Component component) {
        List<Component> children = component.children().stream()
                .map(MessageUtils::forceBold)
                .toList();
        return component.children(children).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE);
    }

    private static boolean isDivider(String message) {
        String plain = COLOR_CODES.matcher(message).replaceAll("").trim();
        return !plain.isEmpty() && DIVIDER_LINE.matcher(plain).matches();
    }

    private static String replace(String message, String... replacements) {
        String result = message;
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            result = result.replace("{" + replacements[i] + "}", replacements[i + 1]);
        }
        return result;
    }
}
