package org.fletchly.genius.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.fletchly.genius.chat.ChatManager;
import org.fletchly.genius.util.ConfigurationManager;

import javax.inject.Inject;

public class AskCommand {
    private final ConfigurationManager configurationManager;
    private final ChatManager chatManager;

    @Inject
    public AskCommand(ConfigurationManager configurationManager, ChatManager chatManager) {
        this.configurationManager = configurationManager;
        this.chatManager = chatManager;
    }

    public LiteralCommandNode<CommandSourceStack> createCommandNode() {
        return null;
    }
}
