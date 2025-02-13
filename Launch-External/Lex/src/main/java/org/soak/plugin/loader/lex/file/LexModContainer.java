package org.soak.plugin.loader.lex.file;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.forgespi.language.IModInfo;
import org.soak.plugin.SoakPluginContainer;

public class LexModContainer extends ModContainer {

    private final SoakPluginContainer container;

    public LexModContainer(SoakPluginContainer container) {
        super(new LexSoakModInfo(container));
        this.contextExtension = () -> null;
        this.modLoadingStage = ModLoadingStage.COMPLETE;
        this.container = container;
    }

    @Override
    public boolean matches(Object o) {
        return false;
    }

    @Override
    public Object getMod() {
        return this.container.getTrueContainer().instance();
    }
}
