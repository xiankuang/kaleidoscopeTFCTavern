package net.xdpp.kaleidoscopetfctavern.mixin;

import com.github.ysbbbbbb.kaleidoscopetavern.block.plant.TrellisBlock;
import net.xdpp.kaleidoscopetfctavern.block.properties.GrapevineType;
import net.xdpp.kaleidoscopetfctavern.init.ModBlocks;
import net.xdpp.kaleidoscopetfctavern.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * TrellisBlock 混合类
 * <p>
 * 让我们的葡萄藤也能放置到藤架上，同时兼容 TFC 的泥土和草方块
 */
@Mixin(value = TrellisBlock.class, remap = false)
public abstract class TrellisBlockMixin {

    /**
     * 注入到 use 方法前，处理我们的葡萄藤和 TFC 泥土/草方块
     */
    @Inject(method = "use(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    private void beforeUse(BlockState state, Level level, BlockPos pos, Player player,
                           InteractionHand hand, BlockHitResult hitResult,
                           CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemInHand = player.getItemInHand(hand);

        boolean isGrapevinePurple = itemInHand.is(ModItems.WILD_GRAPEVINE_PURPLE.get());
        boolean isGrapevineRed = itemInHand.is(ModItems.WILD_GRAPEVINE_RED.get());
        boolean isGrapevineWhite = itemInHand.is(ModItems.WILD_GRAPEVINE_WHITE.get());
        boolean isGrapevineGreen = itemInHand.is(ModItems.WILD_GRAPEVINE_GREEN.get());

        if (!isGrapevinePurple && !isGrapevineRed && !isGrapevineWhite && !isGrapevineGreen) {
            return;
        }

        var type = state.getValue(TrellisBlock.TYPE);

        if (type != com.github.ysbbbbbb.kaleidoscopetavern.block.properties.TrellisType.SINGLE) {
            return;
        }

        BlockState belowState = level.getBlockState(pos.below());

        boolean isValidSoil = belowState.is(BlockTags.DIRT) ||
                              isInTag(belowState, "tfc", "dirt") ||
                              isInTag(belowState, "tfc", "grass");

        if (isValidSoil) {
            GrapevineType grapeType;
            if (isGrapevinePurple) {
                grapeType = GrapevineType.PURPLE;
            } else if (isGrapevineRed) {
                grapeType = GrapevineType.RED;
            } else if (isGrapevineWhite) {
                grapeType = GrapevineType.WHITE;
            } else {
                grapeType = GrapevineType.GREEN;
            }
            BlockState plantedState = ModBlocks.TFC_GRAPEVINE_TRELLIS.get()
                    .defaultBlockState()
                    .setValue(TrellisBlock.WATERLOGGED, state.getValue(TrellisBlock.WATERLOGGED))
                    .setValue(net.xdpp.kaleidoscopetfctavern.block.plant.TFCGrapevineTrellisBlock.GRAPE_TYPE, grapeType);
            level.setBlockAndUpdate(pos, plantedState);
            level.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS);
            if (!player.isCreative()) {
                itemInHand.shrink(1);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    /**
     * 检查方块是否在指定命名空间的标签中
     */
    private boolean isInTag(BlockState state, String namespace, String tagName) {
        var tag = net.minecraft.tags.TagKey.create(
            net.minecraft.core.registries.Registries.BLOCK,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(namespace, tagName)
        );
        return state.is(tag);
    }
}
