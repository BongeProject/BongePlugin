package org.soak.wrapper;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.plugin.SoakPluginManager;

import java.io.IOException;
import java.io.InputStreamReader;

public class SoakSpigotServer extends Server.Spigot {

    private YamlConfiguration load(String configName) {
        var is = SoakManager.getManager()
                .getOwnContainer()
                .openResource("spigot.yml")
                .orElseThrow(() -> new RuntimeException("Cannot find spigot.yml resource"));
        var isr = new InputStreamReader(is);
        return YamlConfiguration.loadConfiguration(isr);
    }

    @Override
    public @NotNull YamlConfiguration getConfig() {
        return load("bukkit.yml");
    }


    @Override
    public @NotNull YamlConfiguration getSpigotConfig() {
        return load("spigot.yml");
    }

    @Override
    public void restart() {
        SoakManager.getManager()
                .getLogger()
                .error("A plugin has required to reboot the server. Manual reboot required currently");
    }
}
