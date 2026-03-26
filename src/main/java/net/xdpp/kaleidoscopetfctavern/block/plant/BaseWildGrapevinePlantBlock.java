package net.xdpp.kaleidoscopetfctavern.block.plant;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

/**
 * 野生葡萄藤主体方块基类
 * <p>
 * 基于 Kaleidoscope Tavern 的 WildGrapevinePlantBlock 修改
 * 支持动态设置头部方块，与头部方块配对使用
 * 实现了附着在树叶上的功能
 */
public class BaseWildGrapevinePlantBlock extends GrowingPlantBodyBlock {
    /**
     * 方块碰撞形状
     */
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 16, 15);
    
    /**
     * 方块属性
     */
    private static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .noCollission()
            .instabreak()
            .sound(SoundType.CAVE_VINES)
            .pushReaction(PushReaction.DESTROY);

    /**
     * 头部方块供应
     * <p>
     * 用于延迟获取对应的头部方块
     */
    private final Supplier<Block> headBlockSupplier;

    /**
     * 构造野生葡萄藤主体方块
     * <p>
     * 设置方块属性、生长方向和碰撞形状
     * 
     * @param headBlockSupplier 头部方块
     */
    public BaseWildGrapevinePlantBlock(Supplier<Block> headBlockSupplier) {
        super(PROPERTIES, Direction.DOWN, SHAPE, false);
        this.headBlockSupplier = headBlockSupplier;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos relative = pos.relative(this.growthDirection.getOpposite());
        BlockState relativeState = level.getBlockState(relative);
        return relativeState.is(this.getHeadBlock())
                || relativeState.is(this.getBodyBlock())
                || this.canAttachTo(relativeState)
                || relativeState.isFaceSturdy(level, relative, this.growthDirection);
    }

    @Override
    protected boolean canAttachTo(BlockState state) {
        return state.is(BlockTags.LEAVES);
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) headBlockSupplier.get();
    }
}
