package com.nekomaster1000.infernalexp.blocks;

import com.nekomaster1000.infernalexp.init.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

import static net.minecraft.block.CactusBlock.AGE;
import static net.minecraft.block.LecternBlock.COLLISION_SHAPE;

public class DullthornsBlock extends BushBlock {
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);

    public DullthornsBlock(Properties properties) {
        super(properties);
            this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
}

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return
                state.isIn(ModBlocks.GLOWDUST_SAND.get()) || state.isIn(Blocks.SAND) || state.isIn(Blocks.RED_SAND)
                        || state.isIn(Blocks.GRASS) || state.isIn(Blocks.GRASS_BLOCK) ||
                        state.isIn(Blocks.DIRT) || state.isIn(Blocks.COARSE_DIRT) || state.isIn(Blocks.FARMLAND) ||
                        state.isIn(Blocks.PODZOL) || state.isIn(Blocks.MYCELIUM) ||
                        state.isIn(Blocks.CRIMSON_NYLIUM) || state.isIn(Blocks.WARPED_NYLIUM) ||
                        state.isIn(Blocks.SOUL_SAND) || state.isIn(Blocks.SOUL_SOIL) ||
                        state.isIn(Blocks.GLOWSTONE) || state.isIn(ModBlocks.DIMSTONE.get()) ||
                        state.isIn(ModBlocks.DULLSTONE.get())
                ;
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent growing cactus from loading unloaded chunks with block update
        if (!state.isValidPosition(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }

    }

    /**
     * Performs a random tick on a block.
     */
    //I have no idea what overriding does LOL - Neko
    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        BlockPos blockpos = pos.up();
        if (worldIn.isAirBlock(blockpos)) {
            int i;
            for(i = 1; worldIn.getBlockState(pos.down(i)).isIn(this); ++i) {
            }

            if (i < 3) {
                int j = state.get(AGE);
                if(net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, blockpos, state, true)) {
                    if (j == 15) {
                        worldIn.setBlockState(blockpos, this.getDefaultState());
                        BlockState blockstate = state.with(AGE, Integer.valueOf(0));
                        worldIn.setBlockState(pos, blockstate, 4);
                        blockstate.neighborChanged(worldIn, blockpos, this, pos, false);
                    } else {
                        worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(j + 1)), 4);
                    }
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Vector3d vector3d = state.getOffset(worldIn, pos);
        return SHAPE.withOffset(vector3d.x, vector3d.y, vector3d.z);
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }


}
