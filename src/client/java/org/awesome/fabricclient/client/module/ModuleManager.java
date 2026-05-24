package org.awesome.fabricclient.client.module;

import org.awesome.fabricclient.client.module.modules.combat.LeftClicker;
import org.awesome.fabricclient.client.module.modules.combat.RightClicker;
import org.awesome.fabricclient.client.module.modules.movement.NoJumpDelay;
import org.awesome.fabricclient.client.module.modules.movement.Sprint;
import org.awesome.fabricclient.client.module.modules.utility.*;
import org.awesome.fabricclient.client.module.modules.visuals.GUI;
import org.awesome.fabricclient.client.module.settings.Setting;

import java.util.*;

public class ModuleManager {
    private static ModuleManager instance;
    private final Map<String, Module> modules = new HashMap<>();
    private final Map<Module, List<Setting<?>>> settings = new HashMap<>();

    // TODO: Auto register any modules
    private ModuleManager() {
        register(new LeftClicker());
        register(new RightClicker());
        register(new Sprint());
        register(new NoJumpDelay());
        register(new NoAttackCooldown());
        register(new Debug());
        register(new GUI());
        register(new NoClickDelay());
        register(new NoPlaceDelay());
        register(new NoHurtCam());
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
        List<Module> moduleList = new ArrayList<>();

        modules.forEach((string, module) -> {
            if(module.getCategory() == category) {
                moduleList.add(module);
            }
        });

        return moduleList;
    }
}
