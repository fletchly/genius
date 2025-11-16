package org.fletchly.genius.command;

import net.kyori.adventure.text.Component;
import org.fletchly.genius.Genius;
import org.fletchly.genius.util.ConfigurationManager;

import static net.kyori.adventure.text.Component.text;

public class GeniusCommand {
    private static final Genius genius = Genius.getInstance();
    private static final Component displayName = text()
            .content("[")
            .append(text(genius
                    .getConfig()
                    .getString(ConfigurationManager.Paths.GENIUS_AGENT_NAME))
            )
            .append(text("] "))
            .build();
}
