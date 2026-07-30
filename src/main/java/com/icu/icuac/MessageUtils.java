package com.icu.icuac;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class MessageUtils {
    public static final String DEFAULT_PREFIX = "&#00D2FF&l[&#3A7BD5&lICUAC&#00D2FF&l] &8&l┃ &r";
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&').hexColors().build();
    private static final Pattern COLOR_CODES = Pattern.compile("(?i)&#[0-9a-f]{6}|&[0-9a-fk-or]");
    private static final int[] GRADIENT_COLORS = {0x00D2FF, 0x3A7BD5, 0xF2C94C};
    private static final Pattern DIVIDER_LINE = Pattern.compile("[-=+|┃✧\\s]+");

    private MessageUtils() {}

    public static String format(ICUAC plugin, String message, String... replacements) {
        if (message == null || message.isEmpty()) return "";
        String prefix = plugin.getLanguageManager().getMessageString("prefix", DEFAULT_PREFIX);
        if (prefix == null || prefix.isBlank()) prefix = DEFAULT_PREFIX;
        String formatted = replace(message.replace("{prefix}", prefix), replacements);
        return LegacyComponentSerializer.legacySection().serialize(deserialize(formatted, true));
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
        boolean playerVisible = sender instanceof Player;
        for (String line : message.split("\\n", -1)) {
            sender.sendMessage(deserialize(replace(line, replacements), playerVisible));
        }
    }

    private static Component deserialize(String message, boolean playerVisible) {
        Component component = SERIALIZER.deserialize(message);
        Component boldComponent = isDivider(message) ? component : forceBold(component);
        return playerVisible ? applyGradient(boldComponent) : boldComponent;
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

    private static Component applyGradient(Component component) {
        int characterCount = countVisibleCharacters(component);
        if (characterCount == 0) return component;
        return applyGradient(component, new int[]{0}, characterCount);
    }

    private static Component applyGradient(Component component, int[] characterIndex, int characterCount) {
        List<Component> children = new ArrayList<>();
        Component result = component;

        if (component instanceof TextComponent textComponent && !textComponent.content().isEmpty()) {
            textComponent.content().codePoints().forEach(codePoint -> {
                Component character = Component.text(new String(Character.toChars(codePoint)))
                        .style(textComponent.style());
                if (!Character.isWhitespace(codePoint)) {
                    character = character.color(gradientColor(characterIndex[0]++, characterCount));
                }
                children.add(character);
            });
            result = textComponent.content("");
        }

        for (Component child : component.children()) {
            children.add(applyGradient(child, characterIndex, characterCount));
        }
        return result.children(children);
    }

    private static int countVisibleCharacters(Component component) {
        int count = 0;
        if (component instanceof TextComponent textComponent) {
            count += (int) textComponent.content().codePoints()
                    .filter(codePoint -> !Character.isWhitespace(codePoint))
                    .count();
        }
        for (Component child : component.children()) {
            count += countVisibleCharacters(child);
        }
        return count;
    }

    private static TextColor gradientColor(int index, int characterCount) {
        if (characterCount <= 1) return TextColor.color(GRADIENT_COLORS[0]);

        double position = (double) index / (characterCount - 1);
        double scaledPosition = position * (GRADIENT_COLORS.length - 1);
        int segment = Math.min((int) scaledPosition, GRADIENT_COLORS.length - 2);
        double segmentProgress = scaledPosition - segment;
        int start = GRADIENT_COLORS[segment];
        int end = GRADIENT_COLORS[segment + 1];

        int red = interpolate((start >> 16) & 0xFF, (end >> 16) & 0xFF, segmentProgress);
        int green = interpolate((start >> 8) & 0xFF, (end >> 8) & 0xFF, segmentProgress);
        int blue = interpolate(start & 0xFF, end & 0xFF, segmentProgress);
        return TextColor.color(red, green, blue);
    }

    private static int interpolate(int start, int end, double progress) {
        return (int) Math.round(start + (end - start) * progress);
    }

    private static String replace(String message, String... replacements) {
        String result = message;
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            result = result.replace("{" + replacements[i] + "}", replacements[i + 1]);
        }
        return result;
    }
}
