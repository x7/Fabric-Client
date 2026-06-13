package org.awesome.fabricclient.client.module.modules.utility;

import org.awesome.fabricclient.client.annotations.ModuleInfo;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;

@RegisterModule()
@ModuleInfo(name = "No Click Delay", description = "Removes the vanilla click delay", category = Category.UTILITY, active = true)
public class NoClickDelay extends Module {
    public NoClickDelay() {
        super();
    }
}
