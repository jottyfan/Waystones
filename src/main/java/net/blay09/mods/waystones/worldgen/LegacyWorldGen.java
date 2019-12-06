package net.blay09.mods.waystones.worldgen;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class LegacyWorldGen implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() != 0) {
            return;
        }

        if (!(random.nextFloat() <= WaystoneConfig.worldGen.legacyChance / 10000f)) {
            return;
        }

        for (int i = 0; i < 5; i++) {
            int blockX = chunkX * 16 + random.nextInt(16);
            int blockZ = chunkZ * 16 + random.nextInt(16);
            BlockPos pos = new BlockPos(blockX, 0, blockZ);
            pos = world.getTopSolidOrLiquidBlock(pos);
            BlockPos posUp = pos.up();
            IBlockState prev = world.getBlockState(pos);
            IBlockState prevUp = world.getBlockState(posUp);
            EnumFacing facing = EnumFacing.values()[2 + random.nextInt(4)];
            if (prev.getBlock() != Blocks.WATER && prev.getBlock().isReplaceable(world, pos) && prevUp.getBlock().isAir(prevUp, world, pos)) {
                world.setBlockState(pos, Waystones.blockWaystone.getDefaultState()
                        .withProperty(WaystoneBlock.BASE, true)
                        .withProperty(WaystoneBlock.FACING, facing), 2);

                world.setBlockState(posUp, Waystones.blockWaystone.getDefaultState()
                        .withProperty(WaystoneBlock.BASE, false)
                        .withProperty(WaystoneBlock.FACING, facing), 2);

                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity instanceof WaystoneTileEntity) {
                    ((WaystoneTileEntity) tileEntity).setWaystoneName(NameGenerator.get(world).getName(world.getBiome(pos), random));
                    ((WaystoneTileEntity) tileEntity).setMossy(true);
                }

                break;
            }
        }
    }

}