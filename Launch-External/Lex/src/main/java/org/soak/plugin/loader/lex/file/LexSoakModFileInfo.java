package org.soak.plugin.loader.lex.file;

import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;

import java.util.List;
import java.util.Map;

public class LexSoakModFileInfo implements IModFileInfo {

    private final LexSoakModInfo info;

    public LexSoakModFileInfo(LexSoakModInfo info) {
        this.info = info;
    }

    @Override
    public List<IModInfo> getMods() {
        return List.of(this.info);
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
        return "";
    }

    @Override
    public String moduleName() {
        return "";
    }

    @Override
    public String versionString() {
        return this.info.getVersion().toString();
    }

    @Override
    public List<String> usesServices() {
        return List.of();
    }

    @Override
    public IModFile getFile() {
        return null;
    }

    @Override
    public IConfigurable getConfig() {
        return null;
    }
}
