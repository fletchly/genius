package org.fletchly.genius.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;

public class ChatHelper
{
    public static List<TextComponent> buildMessage(String botName, String message)
    {
        // Create a list to hold the components
        List<TextComponent> components = new ArrayList<>();

        // Define the maximum length for a single message
        // Minecraft chat message length is 256 characters
        int maxLength = 256;

        // Check if the response is shorter than the max length
        // If it is, create a single TextComponent
        if (message.length() <= (maxLength - (botName.length() + 4))) {
            TextComponent component = Component.text()
                    .content(String.format("[%s]: ", botName))
                    .color(NamedTextColor.GOLD)
                    .append(Component.text(message, NamedTextColor.WHITE))
                    .build();
            components.add(component);
        } else {
            // If the response is longer, split it into chunks
            List<String> chunks = new ArrayList<>();
            StringBuilder currentChunk = new StringBuilder();

            for (String word : message.split(" ")) {
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
                        .content(chunk)
                        .color(NamedTextColor.WHITE)
                        .build();
                components.add(component);
            }
        }
        return components;
    }
}
