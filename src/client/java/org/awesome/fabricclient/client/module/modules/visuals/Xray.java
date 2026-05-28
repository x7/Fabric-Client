package org.awesome.fabricclient.client.module.modules.visuals;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.awesome.fabricclient.client.module.Category;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.settings.SliderSetting;
import org.awesome.fabricclient.client.utility.MinecraftUtility;
import org.awesome.fabricclient.client.utility.PlayerUtility;

import java.util.Map;

public class Xray extends Module {
    private final SliderSetting maxChunks = addSetting(new SliderSetting("Max Chunks", "How many chunks to search for", 12, 1, 12));

    public Xray() {
        super("Xray", "Xray for Ores", Category.VISUALS);
    }

    @Override
    public void onEnable() {
        Player player = PlayerUtility.getPlayer();
        if(player == null) {
            return;
        }

        Level playerWorld = player.level();
        LevelChunk currentChunk = playerWorld.getChunkAt(player.blockPosition());

        if(currentChunk.isEmpty()) {
            return;
        }

        ChunkPos chunkPos = currentChunk.getPos();
        int startChunkX = chunkPos.getMinBlockX();
        int startChunkZ = chunkPos.getMinBlockZ();
        int lowestWorldY = playerWorld.getMinY();
        int highestWorldY = playerWorld.getMaxY();

        new Thread(() -> {
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            for(int x = startChunkX; x < startChunkX + 16; x++) {
                for(int z = startChunkZ; z < startChunkZ + 16; z++) {
                    for(int y = lowestWorldY; y < highestWorldY; y++) {
                        mutableBlockPos.set(x, y, z);
                        BlockState blockEntity = playerWorld.getBlockState(mutableBlockPos);
                        System.out.println(blockEntity.getBlock());
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}