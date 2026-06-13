package org.awesome.fabricclient.client.module.modules.visuals;

import org.awesome.fabricclient.client.annotations.ModuleInfo;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;

@RegisterModule()
@ModuleInfo(name = "GUI", description = "The client GUI", category = Category.VISUALS, active = true)
public class GUI extends Module {
    public GUI() {
        super();
    }
}
