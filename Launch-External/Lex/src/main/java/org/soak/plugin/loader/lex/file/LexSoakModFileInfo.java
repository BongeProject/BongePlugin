package org.soak.plugin.loader.lex.file;

import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LexSoakModFileInfo implements IModFileInfo {

    public static final LexSoakModFileInfo MOD_INFO = new LexSoakModFileInfo();

    private final List<IModInfo> soakPluginsCache = new LinkedList<>();

    public void addMod(LexSoakModInfo info) {
        this.soakPluginsCache.add(info);
    }

    @Override
    public List<IModInfo> getMods() {
        return Collections.unmodifiableList(this.soakPluginsCache);
    }

    @Override
    public List<LanguageSpec> requiredLanguageLoaders() {
        return List.of();
    }

    @Override
    public boolean showAsResourcePack() {
        return false;
    }

    @Override
    public Map<String, Object> getFileProperties() {
        return Map.of();
    }

    @Override
    public String getLicense() {
        return "MIT";
    }

    @Override
    public String moduleName() {
        return "soak";
    }

    @Override
    public String versionString() {
        return "1.0.0";
    }

    @Override
    public List<String> usesServices() {
        return List.of();
    }

    @Override
    public IModFile getFile() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public IConfigurable getConfig() {
        throw new RuntimeException("not implemented yet");

    }
}
