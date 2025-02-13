package org.soak.plugin.loader.lex;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import org.soak.plugin.SoakPluginContainer;
import org.soak.plugin.loader.lex.file.LexModContainer;
import org.soak.plugin.loader.lex.file.LexSoakModInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class LexPluginInjector {

    public static void injectPluginToPlatform(Collection<SoakPluginContainer> containers)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {

        var modInfos = containers.stream().map(LexSoakModInfo::new).toList();
        var modContainers = modInfos.stream().map(LexModContainer::new).toList();
        var modList = ModList.get();

        //Mod Container
        var currentModContainerList = (List<ModContainer>) modList.getClass().getField("mods").get(modList);
        Collection<ModContainer> modifiedModContainerList = new ArrayList<>(currentModContainerList);
        modifiedModContainerList.addAll(modContainers);
        modList.getClass().getField("mods").set(modList, modifiedModContainerList);

        //Sorted Mod Infos
        var currentModInfoList = modList.getMods();
        List<IModInfo> modifiedModInfoList = new ArrayList<>(currentModInfoList);
        modifiedModInfoList.addAll(modInfos);
        modifiedModInfoList.sort(Comparator.comparing(IModInfo::getModId));
        modList.getClass().getField("mods").set(modList, modifiedModInfoList);
    }

}
