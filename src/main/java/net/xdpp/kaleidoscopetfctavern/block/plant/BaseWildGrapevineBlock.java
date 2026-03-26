package net.xdpp.kaleidoscopetfctavern.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolActions;

import java.util.function.Supplier;

/**
 * 野生葡萄藤头部方块基类
 * <p>
 * 基于 Kaleidoscope Tavern 的 WildGrapevineBlock 修改
 * 支持动态设置主体方块，可用于创建多种不同的葡萄藤类型
 */
@SuppressWarnings("deprecation")
public class BaseWildGrapevineBlock extends GrowingPlantHeadBlock {
    /**
     * 修剪状态属性
     * <p>
     * 标识葡萄藤是否被剪刀修剪过
     */
    public static BooleanProperty SHEARED = BooleanProperty.create("sheared");

    /**
     * 方块碰撞形状
     */
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 16, 15);
    
    /**
     * 方块属性
     */
    private static final BlockBehaviour.Properties PROPERTIES = Properties.of()
            .mapColor(MapColor.PLANT)
            .randomTicks()
            .noCollission()
            .instabreak()
            .sound(SoundType.CAVE_VINES)
            .pushReaction(PushReaction.DESTROY);

    /**
     * 主体方块供应商
     * <p>
     * 用于延迟获取对应的主体方块
     */
    private final Supplier<Block> bodyBlockSupplier;

    /**
     * 构造野生葡萄藤头部方块
     * <p>
     * 设置方块属性、生长方向和碰撞形状
     * 注册默认的方块状态
     * 
     * @param bodyBlockSupplier 主体方块
     */
    public BaseWildGrapevineBlock(Supplier<Block> bodyBlockSupplier) {
        super(PROPERTIES, Direction.DOWN, SHAPE, false, 0.15);
        this.bodyBlockSupplier = bodyBlockSupplier;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(SHEARED, false));
    }

    /**
     * 右键交互方块
     * <p>
     * 直接调用父类方法
     * 
     * @param state 方块状态
     * @param level 世界
     * @param pos 方块位置
     * @param player 玩家
     * @param hand 交互的手
     * @param hitResult 交互结果
     * @return 交互结果
     */
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hitResult) {
       return super.use(state, level, pos, player, hand, hitResult);
    }

    /**
     * 创建方块状态定义
     * <p>
     * 继承父类的状态定义，并添加修剪状态属性
     * 
     * @param builder 方块状态定义构建器
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHEARED);
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
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource randomSource) {
        return 0;
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.isAir();
    }

    @Override
    protected Block getBodyBlock() {
        return bodyBlockSupplier.get();
    }
}
