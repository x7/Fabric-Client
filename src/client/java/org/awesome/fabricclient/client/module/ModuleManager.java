package org.awesome.fabricclient.client.module;

import org.awesome.fabricclient.client.module.modules.combat.KillAura;
import org.awesome.fabricclient.client.module.modules.movement.NoJumpDelay;
import org.awesome.fabricclient.client.module.modules.movement.Sprint;
import org.awesome.fabricclient.client.module.modules.visuals.GUI;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    private static ModuleManager instance;
    private final List<Module> modules = new ArrayList<>();

    private ModuleManager() {
        register(new KillAura());
        register(new Sprint());
        register(new NoJumpDelay());
        register(new GUI());
    }

    public static ModuleManager getInstance() {
        if (instance == null) instance = new ModuleManager();
        return instance;
    }

    private void register(Module module) {
        modules.add(module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public Module getModule(Class<?> clazz) {
        List<Module> modules = getModules();
        Module returnModule = null;

        for(Module module : modules) {
            if(module.getClass() == clazz) {
                returnModule = module;
                break;
            }
        }

        return returnModule;
    }

    public List<Module> getModulesByCategory(Module.Category category) {
        return modules.stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());
    }
}
