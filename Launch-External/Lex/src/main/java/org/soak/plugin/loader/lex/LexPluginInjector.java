package org.soak.plugin.loader.lex;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.soak.plugin.SoakPluginContainer;
import org.soak.plugin.loader.lex.file.LexModContainer;
import org.soak.plugin.loader.lex.file.LexSoakModFileInfo;
import org.soak.plugin.loader.lex.file.LexSoakModInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class LexPluginInjector {

    public static void injectPluginToPlatform(Collection<SoakPluginContainer> containers)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {

        //Mod Container
        try {
            var modContainers = containers.stream().map(LexModContainer::new).toList();
            var modFileInfo = LexSoakModFileInfo.MOD_INFO;
            var pluginFileScanData = new ModFileScanData();
            pluginFileScanData.addModFileInfo(modFileInfo);
            var modListAccessor = ModList.get();
            var modsField = ModList.class.getDeclaredField("mods");
            modsField.setAccessible(true);
            Collection<ModContainer> currentMods = (Collection<ModContainer>) modsField.get(modListAccessor);
            var newMods = new ArrayList<>(currentMods);
            newMods.addAll(modContainers);
            modsField.set(modListAccessor, newMods);
        } catch (Throwable e) {
            throw e;
        }
    }

}
