package net.xdpp.kaleidoscopetfctavern.block.plant;

import com.github.ysbbbbbb.kaleidoscopetavern.block.properties.TrellisType;
import com.github.ysbbbbbb.kaleidoscopetavern.block.plant.ITrellis;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Season;
import net.dries007.tfc.util.climate.Climate;
import net.xdpp.kaleidoscopetfctavern.block.properties.GrapevineType;
import net.xdpp.kaleidoscopetfctavern.init.ModBlocks;
import net.xdpp.kaleidoscopetfctavern.init.ModItems;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolActions;

import static com.github.ysbbbbbb.kaleidoscopetavern.block.plant.ITrellis.axisHasTrellis;
import static com.github.ysbbbbbb.kaleidoscopetavern.block.plant.ITrellis.updateType;

@SuppressWarnings("deprecation")
public class TFCGrapevineTrellisBlock extends Block implements SimpleWaterloggedBlock, ITrellis, net.minecraft.world.level.block.BonemealableBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    public static final int MAX_AGE = BlockStateProperties.MAX_AGE_3;
    public static final EnumProperty<GrapevineType> GRAPE_TYPE = EnumProperty.create("grape_type", GrapevineType.class);
    public static final Direction[] CHECK_DIRECTION = new Direction[]{Direction.UP, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH};

    private final float growPerTickProbability;

    public TFCGrapevineTrellisBlock() {
        super(Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.GUITAR)
                .strength(0.8F)
                .sound(SoundType.WOOD)
                .randomTicks()
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY)
                .ignitedByLava());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(TYPE, TrellisType.SINGLE)
                .setValue(AGE, 0)
                .setValue(GRAPE_TYPE, GrapevineType.PURPLE)
                .setValue(WATERLOGGED, false));
        this.growPerTickProbability = 0.25F;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (!itemInHand.canPerformAction(ToolActions.SHEARS_HARVEST)) {
            return super.use(state, level, pos, player, hand, hitResult);
        }
        BlockState newState = com.github.ysbbbbbb.kaleidoscopetavern.init.ModBlocks.TRELLIS.get()
                .defaultBlockState()
                .setValue(TYPE, state.getValue(TYPE))
                .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
        level.setBlockAndUpdate(pos, newState);
        var grapeType = state.getValue(GRAPE_TYPE);
        ItemStack dropItem;
        switch (grapeType) {
            case PURPLE:
                dropItem = ModItems.WILD_GRAPEVINE_PURPLE.get().getDefaultInstance();
                break;
            case RED:
                dropItem = ModItems.WILD_GRAPEVINE_RED.get().getDefaultInstance();
                break;
            case WHITE:
                dropItem = ModItems.WILD_GRAPEVINE_WHITE.get().getDefaultInstance();
                break;
            case GREEN:
                dropItem = ModItems.WILD_GRAPEVINE_GREEN.get().getDefaultInstance();
                break;
            default:
                dropItem = ModItems.WILD_GRAPEVINE_PURPLE.get().getDefaultInstance();
                break;
        }
        Block.popResource(level, pos, dropItem);
        itemInHand.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        player.playSound(SoundEvents.BEEHIVE_SHEAR);
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        boolean xHas = axisHasTrellis(level, pos, Direction.Axis.X);
        boolean yHas = axisHasTrellis(level, pos, Direction.Axis.Y);
        boolean zHas = axisHasTrellis(level, pos, Direction.Axis.Z);
        var trellisType = updateType(state.getValue(TYPE), xHas, yHas, zHas);

        state = state.setValue(TYPE, trellisType);
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean sameType(BlockState state) {
        return state.is(com.github.ysbbbbbb.kaleidoscopetavern.init.ModBlocks.TRELLIS.get()) 
                || state.is(com.github.ysbbbbbb.kaleidoscopetavern.init.ModBlocks.GRAPEVINE_TRELLIS.get())
                || state.is(this);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (ForgeHooks.onCropsGrowPre(level, pos, state, random.nextDouble() < this.growPerTickProbability)) {
            this.doGrow(level, pos, state);
        }
    }

    public boolean isMaxAge(BlockState state) {
        return state.getValue(AGE) >= MAX_AGE;
    }

    public boolean belowSupportGrow(BlockState belowState) {
        if (belowState.is(this)) {
            return isMaxAge(belowState);
        } else {
            return belowState.is(BlockTags.DIRT) ||
                   isInTag(belowState, "tfc", "dirt") ||
                   isInTag(belowState, "tfc", "grass");
        }
    }

    private boolean isInTag(BlockState state, String namespace, String tagName) {
        var tag = net.minecraft.tags.TagKey.create(
            net.minecraft.core.registries.Registries.BLOCK,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(namespace, tagName)
        );
        return state.is(tag);
    }

    public boolean canGrowInto(BlockState checkState) {
        return checkState.is(com.github.ysbbbbbb.kaleidoscopetavern.init.ModBlocks.TRELLIS.get());
    }

    public BlockState getGrowIntoState(Direction direction, BlockState checkState, GrapevineType grapeType) {
        var type = checkState.getOptionalValue(TYPE).orElse(TrellisType.SINGLE);
        boolean waterlogged = checkState.getOptionalValue(WATERLOGGED).orElse(false);
        int age = direction == Direction.UP ? 0 : MAX_AGE;
        return this.defaultBlockState()
                .setValue(TYPE, type)
                .setValue(AGE, age)
                .setValue(GRAPE_TYPE, grapeType)
                .setValue(WATERLOGGED, waterlogged);
    }

    public boolean canGrowGrape(LevelReader level, BlockPos pos) {
        if (pos.getY() < level.getMinBuildHeight() + 1) {
            return false;
        }
        return level.getBlockState(pos.below()).isAir();
    }

    public boolean canGrow(LevelReader level, BlockPos pos, BlockState state) {
        if (state.getValue(TYPE) == TrellisType.SINGLE) {
            BlockState belowState = level.getBlockState(pos.below());
            if (!belowSupportGrow(belowState)) {
                return false;
            }
            if (!isMaxAge(state)) {
                return true;
            }
        }

        if (isMaxAge(state)) {
            for (Direction direction : CHECK_DIRECTION) {
                BlockPos checkPos = pos.relative(direction);
                BlockState checkState = level.getBlockState(checkPos);
                if (this.canGrowInto(checkState)) {
                    return true;
                }
            }

            return canGrowGrape(level, pos);
        } else {
            return true;
        }
    }

    public void doGrow(Level level, BlockPos pos, BlockState state) {
        var grapeType = state.getValue(GRAPE_TYPE);
        
        if (state.getValue(TYPE) == TrellisType.SINGLE) {
            BlockState belowState = level.getBlockState(pos.below());
            if (!belowSupportGrow(belowState)) {
                return;
            }
            if (!isMaxAge(state)) {
                level.setBlockAndUpdate(pos, state.cycle(AGE));
                ForgeHooks.onCropsGrowPost(level, pos, state);
                return;
            }
        }

        if (isMaxAge(state)) {
            for (Direction direction : CHECK_DIRECTION) {
                BlockPos checkPos = pos.relative(direction);
                BlockState checkState = level.getBlockState(checkPos);
                if (this.canGrowInto(checkState)) {
                    BlockState growIntoState = this.getGrowIntoState(direction, checkState, grapeType);
                    level.setBlockAndUpdate(checkPos, growIntoState);
                    ForgeHooks.onCropsGrowPost(level, checkPos, checkState);
                    return;
                }
            }

            if (canGrowGrape(level, pos) && checkClimateConditions(level, pos, grapeType)) {
                Block cropBlock;
                switch (grapeType) {
                    case PURPLE:
                        cropBlock = ModBlocks.GRAPE_CROP_PURPLE.get();
                        break;
                    case RED:
                        cropBlock = ModBlocks.GRAPE_CROP_RED.get();
                        break;
                    case WHITE:
                        cropBlock = ModBlocks.GRAPE_CROP_WHITE.get();
                        break;
                    case GREEN:
                        cropBlock = ModBlocks.GRAPE_CROP_GREEN.get();
                        break;
                    default:
                        cropBlock = ModBlocks.GRAPE_CROP_PURPLE.get();
                        break;
                }
                level.setBlockAndUpdate(pos.below(), cropBlock.defaultBlockState());
                ForgeHooks.onCropsGrowPost(level, pos.below(), state);
            }
        } else {
            level.setBlockAndUpdate(pos, state.setValue(AGE, MAX_AGE));
            ForgeHooks.onCropsGrowPost(level, pos, state);
        }
    }
    // 作物成果检查
    private boolean checkClimateConditions(Level level, BlockPos pos, GrapevineType grapeType) {
        float temperature = Climate.getTemperature(level, pos);
        float rainfall = Climate.getRainfall(level, pos);
        Season season = Calendars.get(level).getCalendarMonthOfYear().getSeason();

        switch (grapeType) {
            case PURPLE:
                return temperature >= 15.0f && temperature <= 28.0f
                        && rainfall >= 330.0f && rainfall <= 450.0f
                        && season == Season.FALL;
            case RED:
                return temperature >= 20.0f && temperature <= 32.0f
                        && rainfall >= 210.0f && rainfall <= 300.0f
                        && season == Season.SUMMER;
            case WHITE:
                return temperature >= 12.0f && temperature <= 26.0f
                        && rainfall >= 420.0f && rainfall <= 500.0f
                        && season == Season.FALL;
            case GREEN:
                return temperature >= 20.0f && temperature <= 30.0f
                        && rainfall >= 150.0f && rainfall <= 300.0f
                        && season == Season.SUMMER;
            default:
                return false;
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return this.canGrow(level, pos, state);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        this.doGrow(level, pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE, AGE, GRAPE_TYPE, WATERLOGGED);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return collisionShape(state.getValue(TYPE));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return selectShape(state.getValue(TYPE));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        var grapeType = state.getValue(GRAPE_TYPE);
        ItemStack cloneItem;
        switch (grapeType) {
            case PURPLE:
                cloneItem = ModItems.WILD_GRAPEVINE_PURPLE.get().getDefaultInstance();
                break;
            case RED:
                cloneItem = ModItems.WILD_GRAPEVINE_RED.get().getDefaultInstance();
                break;
            case WHITE:
                cloneItem = ModItems.WILD_GRAPEVINE_WHITE.get().getDefaultInstance();
                break;
            case GREEN:
                cloneItem = ModItems.WILD_GRAPEVINE_GREEN.get().getDefaultInstance();
                break;
            default:
                cloneItem = ModItems.WILD_GRAPEVINE_PURPLE.get().getDefaultInstance();
                break;
        }
        return cloneItem;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
