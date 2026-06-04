package org.awesome.fabricclient.client.module.modules.combat;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.BooleanSetting;
import org.awesome.fabricclient.client.module.settings.ModeSelectSetting;
import org.awesome.fabricclient.client.module.settings.SliderSetting;
import org.awesome.fabricclient.client.utility.PlayerUtility;
import org.awesome.fabricclient.client.utility.Utility;
import org.awesome.fabricclient.client.utility.packets.PacketEvent;

@RegisterModule(name = "Velocity", description = "Reduces the amount of knockback you take", category = Category.COMBAT, active = true)
public class Velocity extends Module {
    private final ModeSelectSetting modes = addSetting(new ModeSelectSetting("Mode", "Mode of velocity", "Normal", "Normal", "Reverse", "Jump"));
    private final SliderSetting horizontal = addSetting(new SliderSetting("Horizontal", "Horizontal Velocity", 100, 0, 100));
    private final SliderSetting vertical = addSetting(new SliderSetting("Vertical", "Vertical Velocity", 100, 0, 100));
    private final SliderSetting chance = addSetting(new SliderSetting("Chance", "Chance of the velocity happening", 100, 0, 100));
    private final BooleanSetting lookingAtPlayer = addSetting(new BooleanSetting("Looking At Player", "Only activate if looking at a player", false));

    public Velocity() {
        super();
    }

    @Override
    public void onPacketReceive(PacketEvent packetEvent) {
        Player player = PlayerUtility.getPlayer();

        Packet<?> packet = packetEvent.getPacket();
        if(!(packet instanceof ClientboundSetEntityMotionPacket)) {
            return;
        }

        int playerId = player.getId();
        String velocityMode = modes.getValue();

        Entity entity = PlayerUtility.getEntityPlayerLookingAt();
        if(lookingAtPlayer.getValue() && !(entity instanceof Player)) {
            return;
        }

        if(!(packetEvent.getPacket() instanceof ClientboundSetEntityMotionPacket)) {
            return;
        }

        ClientboundSetEntityMotionPacket clientboundSetEntityMotionPacket = (ClientboundSetEntityMotionPacket) packetEvent.getPacket();
        int entityId = clientboundSetEntityMotionPacket.id();

        if(entityId != playerId) {
            return;
        }

        Vec3 playersVelocity = clientboundSetEntityMotionPacket.movement();

        if(chance.getValue() < 100) {
            boolean shouldActivate = Utility.shouldActivateChance(chance.getMin(), chance.getMax(), chance.getValue());
            if(!shouldActivate) {
                return;
            }
        }

        if(velocityMode.equalsIgnoreCase("normal")) {
            // These are MAYBE wrong. Not sure come back to this
            double newX = playersVelocity.x() * (horizontal.getValue() / 100.0);
            double newY = playersVelocity.y() * (vertical.getValue() / 100.0);
            double newZ = playersVelocity.x() * (horizontal.getValue() / 100.0);
            Vec3 newVelocity = new Vec3(newX, newY, newZ);

            ClientboundSetEntityMotionPacket clientboundSetEntityMotionPacket1 = new ClientboundSetEntityMotionPacket(PlayerUtility.getPlayer().getId(), newVelocity);
            packetEvent.overridePacket(clientboundSetEntityMotionPacket1);
            return;
        }

        // Just reverse from negative to positive and positive to negative?
        if(velocityMode.equalsIgnoreCase("reverse")) {
            Vec3 reverseVelocity = new Vec3(-playersVelocity.x, playersVelocity.y, -playersVelocity.z);
            ClientboundSetEntityMotionPacket newVelocityPacket = new ClientboundSetEntityMotionPacket(playerId, reverseVelocity);
            packetEvent.overridePacket(newVelocityPacket);
            return;
        }

        // NOT COMPLETE
        if(velocityMode.equalsIgnoreCase("jump")) {
            // if the velocity packet is x and z is 0 don't do anything as it's likely a non entity damage or unable to preform a full jump
            if(playersVelocity.x() == 0.0 && playersVelocity.z() == 0.0) {
                return;
            }

            // you wanna perform the jump 1 tick after the hurt time
            if(player.hurtTime != 9) {
                return;
            }

            player.jumpFromGround();
        }
    }
}