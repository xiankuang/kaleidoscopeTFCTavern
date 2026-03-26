package net.xdpp.kaleidoscopetfctavern;

import com.github.ysbbbbbb.kaleidoscopetavern.block.plant.TrellisBlock;
import net.xdpp.kaleidoscopetfctavern.init.ModBlocks;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.xdpp.kaleidoscopetfctavern.block.properties.GrapevineType;
import net.xdpp.kaleidoscopetfctavern.init.ModItems;

/**
 * KT事件处理器
 * <p>
 * 替代TrellisBlockMixin，使用事件监听器实现葡萄藤放置功能
 * 兼容TFC的泥土和草方块作为土壤
 */
@Mod.EventBusSubscriber(modid = Kaleidoscopetfctavern.MODID)
public class KTEventHandler {

    /**
     * 处理右键点击方块事件
     * <p>
     * 检测是否点击藤架且手持葡萄藤，如果是则尝试种植葡萄藤
     * 支持普通泥土、TFC泥土和TFC草方块作为土壤
     * 
     * @param event 右键点击方块事件
     */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        BlockHitResult hitResult = event.getHitVec();
        ItemStack itemInHand = player.getItemInHand(hand);

        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof TrellisBlock)) {
            return;
        }

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
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    /**
     * 检查方块是否在指定命名空间的标签中
     * 
     * @param state 方块状态
     * @param namespace 命名空间
     * @param tagName 标签名
     * @return 是否在标签中
     */
    private static boolean isInTag(BlockState state, String namespace, String tagName) {
        var tag = net.minecraft.tags.TagKey.create(
            net.minecraft.core.registries.Registries.BLOCK,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(namespace, tagName)
        );
        return state.is(tag);
    }
}
