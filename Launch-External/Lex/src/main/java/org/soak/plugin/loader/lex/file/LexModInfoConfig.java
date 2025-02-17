package org.soak.plugin.loader.lex.file;

import net.minecraftforge.forgespi.language.IConfigurable;
import org.soak.plugin.SoakManager;
import org.soak.plugin.SoakPluginContainer;
import org.soak.plugin.loader.lex.LexSoakModConfig;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LexModInfoConfig implements IConfigurable {

    private SoakPluginContainer container;

    @Override
    public <T> Optional<T> getConfigElement(String... strings) {
        if (match(strings, "license")) {
            return (Optional<T>) Optional.of("All Rights Reserved");
        }
        if (match(strings, "modLoader")) {
            return (Optional<T>) Optional.of("soak");
        }
        if (match(strings, "loaderVersion")) {
            return (Optional<T>) Optional.of(SoakManager.getManager().getVersion().toString());
        }
        return Optional.empty();
    }

    @Override
    public List<? extends IConfigurable> getConfigList(String... strings) {
        if (match(strings, "mods")) {
            return Collections.singletonList(new LexSoakModConfig(this.container));
        }
        return List.of();
    }

    private boolean match(String[] node, String... compare) {
        if (node.length != compare.length) {
            return false;
        }
        for (var index = 0; index < node.length; index++) {
            if (!node[index].equals(compare[index])) {
                return false;
            }
        }
        return true;
    }
}
