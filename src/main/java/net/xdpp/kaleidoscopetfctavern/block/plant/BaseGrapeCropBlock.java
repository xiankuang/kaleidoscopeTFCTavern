package net.xdpp.kaleidoscopetfctavern.block.plant;

import com.github.ysbbbbbb.kaleidoscopetavern.block.properties.TrellisType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolActions;
import net.xdpp.kaleidoscopetfctavern.config.KTConfig;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * 葡萄作物方块基类
 * <p>
 * 基于 Kaleidoscope Tavern 的 GrapeCropBlock 修改
 * 支持动态设置葡萄物品
 */
@SuppressWarnings("deprecation")
public class BaseGrapeCropBlock extends Block {
    /**
     * 生长阶段属性
     */
    public static final IntegerProperty AGE = BlockStateProperties.AGE_5;
    
    /**
     * 最大生长阶段
     */
    public static final int MAX_AGE = BlockStateProperties.MAX_AGE_5;
    
    /**
     * 方块碰撞形状
     */
    public static final VoxelShape SHAPE = Block.box(2, 6, 2, 14, 16, 14);

    /**
     * 葡萄物品供应商
     * <p>
     * 用于延迟获取对应的葡萄物品
     */
    private final Supplier<Item> grapeItemSupplier;

    /**
     * 构造葡萄作物方块
     * <p>
     * 设置方块属性、注册默认状态
     * 初始化葡萄结果部分
     * 
     * @param grapeItemSupplier 葡萄结果部分
     */
    public BaseGrapeCropBlock(Supplier<Item> grapeItemSupplier) {
        super(Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .offsetType(BlockBehaviour.OffsetType.XYZ)
                .pushReaction(PushReaction.DESTROY));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0));
        this.grapeItemSupplier = grapeItemSupplier;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.canPerformAction(ToolActions.SHEARS_HARVEST) && isMaxAge(state)) {
            if (!level.isClientSide) {
                LootParams.Builder lootParamsBuilder = new LootParams.Builder((ServerLevel) level)
                        .withParameter(LootContextParams.ORIGIN, pos.getCenter())
                        .withParameter(LootContextParams.TOOL, heldItem)
                        .withParameter(LootContextParams.THIS_ENTITY, player)
                        .withParameter(LootContextParams.BLOCK_STATE, state);
                
                List<ItemStack> drops = getDrops(state, lootParamsBuilder);
                for (ItemStack drop : drops) {
                    Block.popResource(level, pos, drop);
                }
            }
            
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            player.playSound(SoundEvents.BEEHIVE_SHEAR);
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return super.isRandomlyTicking(state) && state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (ForgeHooks.onCropsGrowPre(level, pos, state, random.nextDouble() < KTConfig.GRAPE_CROP_GROWTH_CHANCE.get())) {
            level.setBlockAndUpdate(pos, state.cycle(AGE));
            ForgeHooks.onCropsGrowPost(level, pos, state);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.canSurvive(level, pos)) {
            return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        var aboveState = level.getBlockState(pos.above());
        if (aboveState.getBlock() instanceof com.github.ysbbbbbb.kaleidoscopetavern.block.plant.GrapevineTrellisBlock trellis) {
            return trellis.isMaxAge(aboveState);
        }
        return false;
    }

    public boolean isMaxAge(BlockState state) {
        return state.getValue(AGE) >= MAX_AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder lootParamsBuilder) {
        if (isMaxAge(state)) {
            return super.getDrops(state, lootParamsBuilder);
        }
        return Collections.emptyList();
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return grapeItemSupplier.get().getDefaultInstance();
    }
}
