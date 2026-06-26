package org.awesome.fabricclient.client.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.awesome.fabricclient.client.annotations.ModuleInfo;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.BooleanSetting;
import org.awesome.fabricclient.client.module.settings.SliderSetting;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;
import org.awesome.fabricclient.client.utility.packets.PacketEvent;

@RegisterModule()
@ModuleInfo(name = "WTap", description = "Wtaps for you to deal more knockback", category = Category.COMBAT, active = true)
public class WTap extends Module {
    private final SliderSetting delay = addSetting(new SliderSetting("Pause", "How long to pause for before wtapping again", 50, 50, 500));
    private final SliderSetting stopDuration = addSetting(new SliderSetting("Stop Duration", "How long to stop for before releasing W key", 50, 50, 500));
    private final BooleanSetting waitForAttackCoolDown = addSetting(new BooleanSetting("Wait For Attack Cooldown", "Waits for the attack cooldown before wtapping", false));
    private boolean isWtapActive = false;

    public WTap() {
        super();
    }

    @Override
    public void onPacketSend(PacketEvent packetEvent) {
        if(this.isWtapActive) {
            return;
        }

        Player player = PlayerUtility.getPlayer();
        if(!player.isSprinting()) {
            return;
        }

        Packet<?> packet = packetEvent.getPacket();

        if(!(packet instanceof ServerboundAttackPacket)) {
            return;
        }

        ServerboundAttackPacket serverboundAttackPacket = (ServerboundAttackPacket) packet;
        if(serverboundAttackPacket.type() != GamePacketTypes.SERVERBOUND_ATTACK) {
            return;
        }

        int entityId = serverboundAttackPacket.entityId();
        Level level = player.level();
        Entity entity = level.getEntity(entityId);

        if(!(entity instanceof Player)) {
            return;
        }

        this.isWtapActive = true;
        int playersSwingTime = player.swingTime;
        if(waitForAttackCoolDown.getValue() && playersSwingTime > 0) {
            MinecraftUtility.runLater(this::wtap, playersSwingTime * 50, false);
            return;
        }

        MinecraftUtility.runLater(this::wtap, delay.getValue(), false); // delay
    }

    private void wtap() {
        Minecraft minecraft = MinecraftUtility.getMinecraftClient();
        Options options = minecraft.options;

        options.keyUp.setDown(false);
        MinecraftUtility.runLater(() -> {
            options.keyUp.setDown(true);
            isWtapActive = false;
        }, stopDuration.getValue(), false); // pause
    }
}