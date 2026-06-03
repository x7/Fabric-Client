package org.awesome.fabricclient.client.module.modules.utility;

import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.packets.PacketManager;

@RegisterModule(name = "Fake Lag", description = "Fake Lag", category = Category.UTILITY, active = false)
public class FakeLag extends Module {
    public FakeLag() {
        super();
    }

    @Override
    public void onEnable() {
        PacketManager.addIncomingListener("fake_lag_inbound", packetEvent -> {
            MinecraftUtility.getMinecraftClient().execute(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
