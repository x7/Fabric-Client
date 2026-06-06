package org.awesome.fabricclient.client.module;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.modules.combat.LeftClicker;
import org.awesome.fabricclient.client.module.modules.combat.RightClicker;
import org.awesome.fabricclient.client.module.modules.combat.Velocity;
import org.awesome.fabricclient.client.module.modules.movement.NoJumpDelay;
import org.awesome.fabricclient.client.module.modules.movement.Sprint;
import org.awesome.fabricclient.client.module.modules.utility.*;
import org.awesome.fabricclient.client.module.modules.visuals.GUI;
import org.awesome.fabricclient.client.module.modules.visuals.Xray;
import org.awesome.fabricclient.client.module.settings.Setting;

import java.util.*;

public class ModuleManager {
    private static ModuleManager instance;
    private final Map<String, Module> modules = new HashMap<>();
    private final Map<Category, List<Module>> categoryModules = new HashMap<>();

    private ModuleManager() {
        try(ScanResult scanResult = new ClassGraph().enableAnnotationInfo().acceptPackages("org.awesome.fabricclient").scan()) {
            var moduleClasses = scanResult.getClassesWithAnnotation(RegisterModule.class.getName());

            for(var classInfo : moduleClasses) {
                Class<?> clazz = classInfo.loadClass();
                try {
                    Module module = (Module) clazz.getDeclaredConstructor().newInstance();

                    if(!module.isActive()) {
                        continue;
                    }

                    register(module);
                } catch (Exception error) {
                    System.out.println("Failed to register " + moduleClasses.getClass().getSimpleName() + " as a module");
                }
            }
        }
    }

    public static ModuleManager getInstance() {
        if(instance == null) {
            instance = new ModuleManager();
        }

        return instance;
    }

    private void register(Module module) {
        if(modules.containsValue(module)) {
            System.out.println(module.getName() + " already exist as a module");
            return;
        }

        modules.put(module.getName(), module);
    }

    public Map<String, Module> getModules() {
        return modules;
    }

    public Module getModule(String moduleName) {
        if(!modules.containsKey(moduleName)) {
            return null;
        }

        return modules.get(moduleName);
    }

    public List<Module> getModulesByCategory(Category category) {
        if(categoryModules.containsKey(category)) {
            return categoryModules.get(category);
        }

        List<Module> moduleList = new ArrayList<>();

        modules.forEach((string, module) -> {
            if(module.getCategory() == category) {
                moduleList.add(module);
            }
        });

        categoryModules.put(category, moduleList);
        return moduleList;
    }
}