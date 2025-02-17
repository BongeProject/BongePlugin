package org.soak.fix.forge;


import org.soak.plugin.SoakManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.jar.JarFile;

public class ForgeFixCommons {

    public static boolean isRequired() {
        try {
            Class<?> modContainer;
            try {
                modContainer = Class.forName("FMLModContainer");
            } catch (ClassNotFoundException ex) {
                return false;
            }
            if (!modContainer.isInstance(SoakManager.getManager().getOwnContainer())) {
                return false;
            }
            Class.forName("org.apache.commons.lang.Validate");
            return false;
        } catch (ClassNotFoundException e) {
            return true;
        }
    }

    public static void installApacheCommons() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, IOException {
        var classLoader = ForgeFixCommons.class.getClassLoader();

        var targetPackageFilterField = classLoader.getClass().getDeclaredField("targetPackageFilter");
        targetPackageFilterField.trySetAccessible();
        var oldValue = (Predicate) targetPackageFilterField.get(classLoader);
        targetPackageFilterField.set(classLoader, (Predicate) o -> {
            if (o instanceof String classname) {
                if (classname.startsWith("org.apache.commons")) {
                    return true;
                }
            }
            return oldValue.test(o);
        });
        var loadClassMethod = classLoader.getClass().getMethod("loadClass", String.class);
        loadClassMethod.setAccessible(true);

        var pluginContainer = SoakManager.getManager().getOwnContainer();
        var modInfo = pluginContainer.getClass().getMethod("getModInfo").invoke(pluginContainer);
        var owningFile = modInfo.getClass().getMethod("getOwningFile").invoke(modInfo);
        var fileInfo = owningFile.getClass().getMethod("getFile").invoke(owningFile);
        var filePath = (Path) fileInfo.getClass().getMethod("getFilePath").invoke(fileInfo);
        var soakFile = filePath.toFile();

        var soakJar = new JarFile(soakFile);
        var iter = soakJar.stream().filter(entry -> {
            if (entry.isDirectory()) {
                return false;
            }
            var name = entry.getName();
            if (!name.endsWith(".class")) {
                return false;
            }
            if (!name.startsWith("org/apache/commons")) {
                return false;
            }
            return true;
        }).iterator();

        while (iter.hasNext()) {
            var next = iter.next();
            var name = next.getName();
            name = name.replaceAll("/", ".").substring(0, name.length() - 6);

            try {
                loadClassMethod.invoke(classLoader, name);
            } catch (InvocationTargetException e) {
                var target = e.getTargetException();
                if (target instanceof ClassNotFoundException) {
                    SoakManager.getManager().getLogger().error("Could not load: " + target.getMessage());
                    continue;
                }
                throw e;
            }
        }

        soakJar.close();
    }
}
