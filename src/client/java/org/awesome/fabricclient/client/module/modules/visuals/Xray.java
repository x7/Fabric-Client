package org.awesome.fabricclient.client.module.modules.visuals;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import org.awesome.fabricclient.client.annotations.ModuleInfo;
import org.awesome.fabricclient.client.annotations.RegisterModule;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.SliderSetting;
import org.awesome.fabricclient.client.utility.PlayerUtility;

import java.util.Map;

@RegisterModule()
@ModuleInfo(name = "Xray", description = "Xray for ores", category = Category.VISUALS, active = false)
public class Xray extends Module {
    private final SliderSetting maxChunks = addSetting(new SliderSetting("Max Chunks", "How many chunks to search for", 12, 1, 12));

    public Xray() {
        super();
    }

    @Override
    public void onEnable() {
        Player player = PlayerUtility.getPlayer();
        if(player == null) {
            return;
        }

        Level playerWorld = player.level();
        ChunkSource chunkSource = playerWorld.getChunkSource();
        int loadedChunksCount = chunkSource.getLoadedChunksCount();

        if(loadedChunksCount == 0) {
            return;
        }

        // chunks are 16 x 16
        int minWorldY = playerWorld.getMinY();
        int maxWorldY = playerWorld.getMaxY();

        System.out.println(loadedChunksCount);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}