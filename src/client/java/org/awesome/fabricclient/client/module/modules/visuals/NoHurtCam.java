package org.awesome.fabricclient.client.module.modules.visuals;

import org.awesome.fabricclient.client.annotations.ModuleInfo;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;

@RegisterModule()
@ModuleInfo(name = "No Hurt Cam", description = "Remove the camera shaking when you take damage", category = Category.VISUALS, active = true)
public class NoHurtCam extends Module {
    public NoHurtCam() {
        super();
    }
}
