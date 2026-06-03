package org.awesome.fabricclient.client.module.modules.utility;

import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;

@RegisterModule(name = "No Block Delay", description = "Removes the vanilla block delay", category = Category.UTILITY, active = true)
public class NoBlockDelay extends Module {
    public NoBlockDelay() {
        super();
    }
}
