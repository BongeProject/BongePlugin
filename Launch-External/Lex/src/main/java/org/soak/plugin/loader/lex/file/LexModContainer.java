package org.soak.plugin.loader.lex.file;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.forgespi.language.IModInfo;

public class LexModContainer extends ModContainer {

    public LexModContainer(LexSoakModInfo info) {
        super(info);
    }

    @Override
    public boolean matches(Object o) {
        return false;
    }

    @Override
    public Object getMod() {
        return ((LexSoakModInfo) this.modInfo).container().getTrueContainer().instance();
    }
}
