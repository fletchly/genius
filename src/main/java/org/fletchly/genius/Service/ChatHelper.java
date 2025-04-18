package org.fletchly.genius.Service;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;

public class ChatHelper
{
    public static List<TextComponent> buildBotMessage(String botName, String response) {
        List<TextComponent> components = new ArrayList<>();

        int maxLength = 256;

        if (response.length() <= maxLength) {
            TextComponent component = Component.text()
                    .content(String.format("[%s]: ", botName))
                    .color(NamedTextColor.GOLD)
                    .append(Component.text(response, NamedTextColor.WHITE))
                    .build();
            components.add(component);
        } else {
            List<String> chunks = new ArrayList<>();
            StringBuilder currentChunk = new StringBuilder();

            for (String word : response.split(" ")) {
                if (currentChunk.length() + word.length() + 1 > maxLength) {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                }
                currentChunk.append(word).append(" ");
            }
            if (!currentChunk.isEmpty()) {
                chunks.add(currentChunk.toString().trim());
            }

            for (int i = 0; i < chunks.size(); i++) {
                boolean isFirst = (i == 0);
                String chunk = chunks.get(i);

                TextComponent component = isFirst
                        ? Component.text()
                        .content(String.format("[%s]: ", botName))
                        .color(NamedTextColor.GOLD)
                        .append(Component.text(chunk, NamedTextColor.WHITE))
                        .build()
                        : Component.text()
                        .content(String.format("(part %d): ", i + 1))
                        .color(NamedTextColor.GRAY)
                        .append(Component.text(chunk, NamedTextColor.WHITE))
                        .build();

                components.add(component);
            }
        }
        return components;
    }
}
