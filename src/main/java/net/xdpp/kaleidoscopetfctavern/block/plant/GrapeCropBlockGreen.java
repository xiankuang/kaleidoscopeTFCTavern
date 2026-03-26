package net.xdpp.kaleidoscopetfctavern.block.plant;

import com.github.ysbbbbbb.kaleidoscopetavern.block.plant.GrapevineTrellisBlock;
import net.xdpp.kaleidoscopetfctavern.init.ModBlocks;
import net.xdpp.kaleidoscopetfctavern.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolActions;

/**
 * 绿葡萄作物方块
 * <p>
 * 生长在TFC兼容葡萄藤藤架下方的绿葡萄作物
 * 支持剪刀收割，有5个生长阶段
 */
@SuppressWarnings("deprecation")
public class GrapeCropBlockGreen extends Block {
    /**
     * 生长阶段属性
     */
    public static final IntegerProperty AGE = BlockStateProperties.AGE_5;
    
    /**
     * 最大生长阶段
     */
    public static final int MAX_AGE = BlockStateProperties.MAX_AGE_5;

    /**
     * 构造绿葡萄作物方块
     * <p>
     * 设置方块属性并注册默认状态
     */
    public GrapeCropBlockGreen() {
        super(Properties.of()
                .strength(0.2f)
                .noCollission()
                .randomTicks());
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    /**
     * 获取克隆物品栈
     * <p>
     * 当玩家用鼠标中键点击方块时返回对应的绿葡萄物品
     * 
     * @param state 方块状态
     * @param target 点击目标
     * @param level 世界
     * @param pos 方块位置
     * @param player 玩家
     * @return 绿葡萄物品栈
     */
    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return ModItems.GRAPE_GREEN.get().getDefaultInstance();
    }

    /**
     * 随机刻执行
     * <p>
     * 检查方块是否能继续生长，如果不能则切换为枯萎状态
     * 检查光照是否足够，如果足够则尝试生长
     * 
     * @param state 方块状态
     * @param level 世界
     * @param pos 方块位置
     * @param random 随机源
     */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!this.canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }

        int age = state.getValue(AGE);
        if (age < MAX_AGE && level.getRawBrightness(pos.above(), 0) >= 9) {
            if (ForgeHooks.onCropsGrowPre(level, pos, state, true)) {
                this.growCrops(level, pos, state);
                ForgeHooks.onCropsGrowPost(level, pos, state);
            }
        }
    }

    /**
     * 生长作物
     * <p>
     * 将生长阶段增加1
     * 
     * @param level 世界
     * @param pos 方块位置
     * @param state 方块状态
     */
    public void growCrops(Level level, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        if (age < MAX_AGE) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 2);
        }
    }

    /**
     * 检查是否达到最大生长阶段
     * 
     * @param state 方块状态
     * @return 是否达到最大生长阶段
     */
    public boolean isMaxAge(BlockState state) {
        return state.getValue(AGE) >= MAX_AGE;
    }

    /**
     * 右键交互方块
     * <p>
     * 当使用剪刀且作物成熟时，收割绿葡萄并移除方块
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
        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.canPerformAction(ToolActions.SHEARS_HARVEST) && isMaxAge(state)) {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            Block.popResource(level, pos, new ItemStack(ModItems.GRAPE_GREEN.get(), 3));
            heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            player.playSound(SoundEvents.BEEHIVE_SHEAR);
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

    /**
     * 检查方块是否能继续存活
     * <p>
     * 需要上方是成熟的葡萄藤藤架（KT或TFC兼容的）
     * 
     * @param state 方块状态
     * @param level 世界
     * @param pos 方块位置
     * @return 是否能继续存活
     */
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        var aboveState = level.getBlockState(pos.above());
        if (aboveState.getBlock() instanceof GrapevineTrellisBlock trellis) {
            return trellis.isMaxAge(aboveState);
        }
        if (aboveState.getBlock() instanceof TFCGrapevineTrellisBlock ourTrellis) {
            return ourTrellis.isMaxAge(aboveState);
        }
        return false;
    }

    /**
     * 创建方块状态定义
     * <p>
     * 注册生长阶段属性
     * 
     * @param builder 方块状态定义构建器
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}
