package org.awesome.fabricclient.client.module.modules.utility;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaffDetector extends Module {
    private final List<String> hypixelStaff = Arrays.asList(
            "2008Choco", "Aceify", "AdamWho", "Aerh", "Ahmad", "Alespresso", "Alexmaster", "AmyTheMudkip",
            "Amylatte", "BlocksKey", "Bloozing", "BugDetector", "Camiilla", "Catstaboli", "Cerus_",
            "Changitesz", "Chi", "Citria", "Cocolastico53", "DEADORKAI", "DeluxeRose", "DistrictGecko",
            "Donpireso", "Externalizable", "Fadest", "Gainful", "Gerbor12", "Heatran", "Hulk_", "Hypixel",
            "Jayavarmen", "Judg5", "Kazius1", "LadyBleu", "LeGrillant", "MCVisuals", "MistressEldrid",
            "MotorGorilla", "Nausicaah", "NinjaCharlieT", "Nitroholic_", "Plancke", "PublicQualityAcc",
            "Quack", "RapidTheNerd", "Relenter", "Revengeee", "Rezzus", "Rhune", "Rozsa", "SaltyLia",
            "Sanity", "Sethkins", "Simon", "Smoarzified", "SourMatt", "Sylent_", "TacNayn", "TakesEffort",
            "Taytale", "TheBirmanator", "TheMGRF", "Thernus", "TimeDeo", "ToxBowDie", "Velighted", "Veleha",
            "ZeaBot", "_Digit14_", "_PolynaLove_", "_fudgiethewhale", "bassamm", "bugfroggy", "carstairs95",
            "aeyitscoco", "fr3qu3ncy_", "iCeeker", "inventivetalent", "kissmuffliato", "mrkeith", "rosmeme",
            "skyerzz", "sycophantasy", "vinny8ball666", "xHascox"
    );

    public StaffDetector() {
        super("Staff Detector", "Staff Detector", Category.UTILITY);
    }

    // TODO: Make this and test on hypixel
    @Override
    public void onEnable() {
        Player player = PlayerUtility.getPlayer();
        PlayerUtility.sendPlayerChat("test");
    }
}
