package net.xdpp.kaleidoscopetfctavern.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.xdpp.kaleidoscopetfctavern.init.ModBlocks;

import java.util.ArrayList;
import java.util.List;

public class GrapevineLocatorItem extends Item {
    private static final int SEARCH_RADIUS = 64;

    public GrapevineLocatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            BlockPos playerPos = player.blockPosition();
            BlockPos nearestPos = null;
            double nearestDistance = Double.MAX_VALUE;
            Block nearestBlock = null;

            List<Block> grapevineBlocks = new ArrayList<>();
            grapevineBlocks.add(ModBlocks.WILD_GRAPEVINE_PURPLE.get());
            grapevineBlocks.add(ModBlocks.WILD_GRAPEVINE_RED.get());
            grapevineBlocks.add(ModBlocks.WILD_GRAPEVINE_WHITE.get());
            grapevineBlocks.add(ModBlocks.WILD_GRAPEVINE_GREEN.get());
            grapevineBlocks.add(ModBlocks.TFC_GRAPEVINE_TRELLIS.get());

            for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
                for (int y = -SEARCH_RADIUS; y <= SEARCH_RADIUS; y++) {
                    for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
                        BlockPos pos = playerPos.offset(x, y, z);
                        BlockState state = level.getBlockState(pos);
                        
                        for (Block block : grapevineBlocks) {
                            if (state.is(block)) {
                                double distance = pos.distSqr(playerPos);
                                if (distance < nearestDistance) {
                                    nearestDistance = distance;
                                    nearestPos = pos;
                                    nearestBlock = block;
                                }
                            }
                        }
                    }
                }
            }

            if (nearestPos != null) {
                int distance = (int) Math.sqrt(nearestDistance);
                String blockName;
                if (nearestBlock == ModBlocks.WILD_GRAPEVINE_PURPLE.get()) {
                    blockName = "野生紫葡萄藤";
                } else if (nearestBlock == ModBlocks.WILD_GRAPEVINE_RED.get()) {
                    blockName = "野生红葡萄藤";
                } else if (nearestBlock == ModBlocks.WILD_GRAPEVINE_WHITE.get()) {
                    blockName = "野生白葡萄藤";
                } else if (nearestBlock == ModBlocks.WILD_GRAPEVINE_GREEN.get()) {
                    blockName = "野生绿葡萄藤";
                } else {
                    blockName = "藤架葡萄藤";
                }
                
                player.sendSystemMessage(Component.literal("找到最近的" + blockName + "！")
                        .withStyle(ChatFormatting.GREEN));
                player.sendSystemMessage(Component.literal("坐标: X: " + nearestPos.getX() + ", Y: " + nearestPos.getY() + ", Z: " + nearestPos.getZ())
                        .withStyle(ChatFormatting.YELLOW));
                player.sendSystemMessage(Component.literal("距离: " + distance + " 格")
                        .withStyle(ChatFormatting.AQUA));
            } else {
                player.sendSystemMessage(Component.literal("在" + SEARCH_RADIUS + "格范围内未找到葡萄藤")
                        .withStyle(ChatFormatting.RED));
            }
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
