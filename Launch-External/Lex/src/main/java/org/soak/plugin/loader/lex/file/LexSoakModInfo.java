package org.soak.plugin.loader.lex.file;

import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.ForgeFeature;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.soak.plugin.SoakPluginContainer;
import org.spongepowered.plugin.metadata.PluginMetadata;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LexSoakModInfo implements IModInfo {

    private final SoakPluginContainer file;

    public LexSoakModInfo(SoakPluginContainer file) {
        this.file = file;
    }

    private PluginMetadata meta() {
        return file.metadata();
    }

    public SoakPluginContainer container() {
        return this.file;
    }

    @Override
    public IModFileInfo getOwningFile() {
        return new LexSoakModFileInfo(this);
    }

    @Override
    public String getModId() {
        return meta().id();
    }

    @Override
    public String getDisplayName() {
        return meta().name().orElse(meta().id());
    }

    @Override
    public String getDescription() {
        return meta().description().orElse("");
    }

    @Override
    public ArtifactVersion getVersion() {
        return meta().version();
    }

    @Override
    public List<? extends ModVersion> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public List<? extends ForgeFeature.Bound> getForgeFeatures() {
        return Collections.emptyList();
    }

    @Override
    public String getNamespace() {
        return this.meta().id();
    }

    @Override
    public Map<String, Object> getModProperties() {
        return Collections.emptyMap();
    }

    @Override
    public Optional<URL> getUpdateURL() {
        return Optional.empty();
    }

    @Override
    public Optional<URL> getModURL() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getLogoFile() {
        return Optional.empty();
    }

    @Override
    public boolean getLogoBlur() {
        return false;
    }

    @Override
    public IConfigurable getConfig() {
        return new LexModInfoConfig();
    }
}
