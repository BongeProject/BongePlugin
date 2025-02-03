package org.soak.plugin.loader.common;

import io.papermc.paper.plugin.configuration.PluginMeta;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.bukkit.plugin.Plugin;
import org.soak.exception.NotImplementedException;
import org.soak.plugin.SoakPlugin;
import org.soak.utils.StringHelper;
import org.spongepowered.plugin.metadata.Container;
import org.spongepowered.plugin.metadata.PluginMetadata;
import org.spongepowered.plugin.metadata.model.PluginBranding;
import org.spongepowered.plugin.metadata.model.PluginContributor;
import org.spongepowered.plugin.metadata.model.PluginDependency;
import org.spongepowered.plugin.metadata.model.PluginLinks;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SoakPluginMetadata implements PluginMetadata {

    private final ArtifactVersion artifactVersion;
    private final Plugin plugin;

    private SoakPluginMetadata(Plugin plugin, ArtifactVersion artifactVersion) {
        this.plugin = plugin;
        this.artifactVersion = artifactVersion;
    }

    public static SoakPluginMetadata fromPlugin(Plugin plugin) {
        ArtifactVersion version = new SoakPluginVersion(plugin.getPluginMeta().getVersion());
        return new SoakPluginMetadata(plugin, version);
    }

    private <C> C meta(Function<PluginMeta, C> function) {
        return function.apply(this.plugin.getPluginMeta());
    }

    @Override
    public Container container() {
        throw NotImplementedException.createByLazy(PluginMetadata.class, "container");
    }

    @Override
    public String id() {
        return StringHelper.toId(this.plugin.getName());
    }

    @Override
    public String entrypoint() {
        return meta(PluginMeta::getMainClass);
    }

    @Override
    public Optional<String> name() {
        return Optional.of(this.plugin.getName());
    }

    @Override
    public Optional<String> description() {
        String description = meta(PluginMeta::getDescription);
        return Optional.ofNullable(description);
    }

    @Override
    public ArtifactVersion version() {
        return this.artifactVersion;
    }

    @Override
    public PluginBranding branding() {
        return new PluginBranding() {

            @Override
            public Optional<String> icon() {
                return Optional.empty();
            }

            @Override
            public Optional<String> logo() {
                return Optional.empty();
            }
        };
    }

    @Override
    public PluginLinks links() {
        return new PluginLinks() {
            @Override
            public Optional<URL> homepage() {
                return Optional.ofNullable(meta(PluginMeta::getWebsite)).flatMap(web -> {
                    try {
                        return Optional.of(new URI(web).toURL());
                    } catch (MalformedURLException | URISyntaxException e) {
                        return Optional.empty();
                    }
                });
            }

            @Override
            public Optional<URL> source() {
                return Optional.empty();
            }

            @Override
            public Optional<URL> issues() {
                return Optional.empty();
            }
        };
    }

    @Override
    public List<PluginContributor> contributors() {
        return meta(PluginMeta::getContributors).stream().map(name -> new PluginContributor() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public Optional<String> description() {
                return Optional.empty();
            }
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<PluginDependency> dependency(String id) {
        return dependencies().stream().filter(pd -> pd.id().equals(id)).findFirst();
    }

    @Override
    public Set<PluginDependency> dependencies() {
        Set<PluginDependency> hardDepends = meta(PluginMeta::getPluginDependencies).stream()
                .map(name -> new BukkitPluginDependency(name, true, false))
                .collect(Collectors.toSet());
        Set<PluginDependency> softDepends = meta(PluginMeta::getPluginSoftDependencies).stream()
                .map(name -> new BukkitPluginDependency(name, false, false))
                .collect(Collectors.toSet());
        Set<PluginDependency> beforeDepends = meta(PluginMeta::getLoadBeforePlugins).stream()
                .map(name -> new BukkitPluginDependency(name, false, true))
                .collect(Collectors.toSet());

        Set<PluginDependency> ret = new HashSet<>();
        ret.addAll(hardDepends);
        ret.addAll(softDepends);
        ret.addAll(beforeDepends);

        ret.add(new BukkitPluginDependency(SoakPlugin.plugin().container().metadata().id(), true, false));
        return ret;
    }

    @Override
    public <T> Optional<T> property(String key) {
        throw NotImplementedException.createByLazy(PluginMetadata.class, "property", String.class);
    }

    @Override
    public Map<String, Object> properties() {
        throw NotImplementedException.createByLazy(PluginMetadata.class, "properties");
    }

    private record BukkitPluginDependency(String name, boolean hardDependency, boolean loadBefore)
            implements PluginDependency {

        @Override
        public String id() {
            return StringHelper.toId(name);
        }

        @Override
        public VersionRange version() {
            return VersionRange.createFromVersion("0.0.0");
        }

        @Override
        public LoadOrder loadOrder() {
            return this.loadBefore ? LoadOrder.BEFORE : LoadOrder.AFTER;
        }

        @Override
        public boolean optional() {
            return !this.hardDependency;
        }

    }
}
