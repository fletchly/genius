package org.fletchly.genius;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.fletchly.genius.di.DaggerPluginComponent;
import org.fletchly.genius.di.PluginComponent;
import org.fletchly.genius.di.PluginModule;

import java.util.List;

public final class Genius extends JavaPlugin {
    private PluginComponent component;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        component = DaggerPluginComponent.builder()
                .pluginModule(new PluginModule(this))
                .build();

        registerCommands();

        this.getLogger().info(String.format("Successfully enabled Genius v%s!", this.getPluginMeta().getVersion()));
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Goodbye!");
    }

    private void registerCommands() {
        this.getLogger().info("Registering commands");
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(
                    component.askCommand().createCommandNode(),
                    "Ask genius a question",
                    List.of("g")
            );
        });
    }
}
