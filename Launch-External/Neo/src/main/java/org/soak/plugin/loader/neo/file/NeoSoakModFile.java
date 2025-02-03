package org.soak.plugin.loader.neo.file;

import net.neoforged.fml.loading.moddiscovery.ModFile;
import net.neoforged.neoforgespi.locating.ModFileDiscoveryAttributes;
import cpw.mods.jarhandling.SecureJar;
import org.soak.plugin.SoakPluginContainer;

import java.nio.file.Path;
import java.util.Collection;

public class NeoSoakModFile extends ModFile {

    public NeoSoakModFile(Path soakPlugin, Collection<SoakPluginContainer> container) {
        super(SecureJar.from(soakPlugin),
              iModFile -> new NeoSoakModFileInfo((NeoSoakModFile) iModFile, container),
              ModFileDiscoveryAttributes.DEFAULT);
    }
}
