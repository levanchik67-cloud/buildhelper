package com.buildhelper;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import java.util.*;

public class SchematicReader {
    public static List<BuildProcess.SchematicBlock> getBlocksToPlace(MinecraftClient client) {
        List<BuildProcess.SchematicBlock> blocks = new ArrayList<>();
        try {
            List<SchematicPlacement> placements = DataManager.getInstance()
                .getSchematicPlacementManager().getAllSchematicsPlacements();
            if (placements.isEmpty()) return blocks;
            for (SchematicPlacement placement : placements) {
                LitematicaSchematic schematic = placement.getSchematic();
                if (schematic == null) continue;
                var regions = placement.getSubRegions();
                for (var entry : regions.entrySet()) {
                    var subRegion = entry.getValue();
                    BlockPos origin = subRegion.getPos();
                    var sWorld = schematic.getSchematicWorld();
                    for (BlockPos lp : BlockPos.iterate(BlockPos.ORIGIN,
                        new BlockPos(schematic.getWidth()-1, schematic.getHeight()-1, schematic.getLength()-1))) {
                        BlockState state = sWorld.getBlockState(lp);
                        if (state.isAir()) continue;
                        BlockPos worldPos = origin.add(lp);
                        if (client.world.getBlockState(worldPos).equals(state)) continue;
                        blocks.add(new BuildProcess.SchematicBlock(worldPos, state));
                    }
                }
            }
        } catch (Exception e) {
            BuildHelperMod.LOGGER.error("Error reading schematic", e);
        }
        return blocks;
    }
}
