package net.xdpp.kaleidoscopetfctavern.mixin;

import com.github.ysbbbbbb.kaleidoscopetavern.block.plant.GrapevineTrellisBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * GrapevineTrellisBlock 混合类
 * <p>
 * 让葡萄藤藤架兼容 TFC 的泥土和草方块
 */
@Mixin(value = GrapevineTrellisBlock.class, remap = false)
public abstract class GrapevineTrellisBlockMixin {

    /**
     * 覆盖 belowSupportGrow 方法，兼容 TFC 的泥土和草方块
     *
     * @reason 需要兼容 TFC 的泥土和草方块，让葡萄藤能在 TFC 世界中正常生长
     * @author xdpp
     */
    @Overwrite
    public boolean belowSupportGrow(BlockState belowState) {
        GrapevineTrellisBlock self = (GrapevineTrellisBlock) (Object) this;
        if (belowState.is(self)) {
            return self.isMaxAge(belowState);
        } else {
            return belowState.is(BlockTags.DIRT) ||
                   isInTag(belowState, "tfc", "dirt") ||
                   isInTag(belowState, "tfc", "grass");
        }
    }

    /**
     * 检查方块是否在指定命名空间的标签中
     */
    private boolean isInTag(BlockState state, String namespace, String tagName) {
        var tag = net.minecraft.tags.TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(namespace, tagName)
        );
        return state.is(tag);
    }
}
