package org.awesome.fabricclient.client.module.modules.combat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.awesome.fabricclient.client.annotations.ModuleInfo;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.BooleanSetting;
import org.awesome.fabricclient.client.module.settings.SliderSetting;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;

import java.util.Set;

@RegisterModule()
@ModuleInfo(name = "Auto Weapon", description = "Automatically equips your sword when hovering over an entity", category = Category.COMBAT, active = true)
public class AutoWeapon extends Module {
    private final SliderSetting delay = addSetting(new SliderSetting("Activation Delay", "How long to wait before switching to your sword", 50, 0, 250));
    private final BooleanSetting holdingLeftClick = addSetting(new BooleanSetting("Holding Left Click", "Only enables while holding down left click", false));
    private final Set<Item> swords = Set.of(
            Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD,
            Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.COPPER_SWORD,
            Items.NETHERITE_SWORD
    );

    public AutoWeapon() {
        super();
    }

    @Override
    public void onTickStart() {
        if(holdingLeftClick.getValue() && !MinecraftUtility.isLeftClickDown()) {
            return;
        }

        Player player = PlayerUtility.getPlayer();
        Entity entity = PlayerUtility.getEntityPlayerLookingAt();

        if(!(entity instanceof Player)) {
            return;
        }

        Inventory inventory = player.getInventory();

        int slotToChangeTo = -1;
        for(int i = 0; i < 9; i++) {
            SlotAccess slotAccess = inventory.getSlot(i);
            if(slotAccess == null) {
                continue;
            }

            ItemStack itemStack = slotAccess.get();
            if(!swords.contains(itemStack.getItem())) {
                continue;
            }

            slotToChangeTo = i;
            break;
        }

        if(slotToChangeTo == -1) {
            return;
        }

        int finalSlotToChangeTo = slotToChangeTo;
        MinecraftUtility.runLater(() -> {
            inventory.setSelectedSlot(finalSlotToChangeTo);
        }, delay.getValue(), true);
    }
}