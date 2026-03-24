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
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolActions;

@SuppressWarnings("deprecation")
public class GrapeCropBlockRed extends Block implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    public static final int MAX_AGE = BlockStateProperties.MAX_AGE_3;

    public GrapeCropBlockRed() {
        super(Properties.of()
                .strength(0.2f)
                .noCollission()
                .randomTicks());
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return ModItems.GRAPE_RED.get().getDefaultInstance();
    }

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

    public void growCrops(Level level, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        if (age < MAX_AGE) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 2);
        }
    }

    public boolean isMaxAge(BlockState state) {
        return state.getValue(AGE) >= MAX_AGE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemInHand = player.getItemInHand(hand);
        int age = state.getValue(AGE);
        boolean maxAge = age == MAX_AGE;
        if (!maxAge && itemInHand.is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        }

        if (maxAge) {
            popResource(level, pos, ModItems.GRAPE_RED.get().getDefaultInstance());
            level.playSound(null, pos, SoundEvents.CROP_PLANTED, player.getSoundSource(), 1.0F, 1.0F);
            level.setBlock(pos, state.setValue(AGE, 0), 2);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        this.growCrops(level, pos, state);
    }
}
