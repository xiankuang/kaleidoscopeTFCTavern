package net.xdpp.kaleidoscopetfctavern.mixin;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IPressingTub;
import com.github.ysbbbbbb.kaleidoscopetavern.compat.jei.category.PressingTubCategory;
import com.github.ysbbbbbb.kaleidoscopetavern.crafting.recipe.PressingTubRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

/**
 * PressingTubCategory 混合类
 * <p>
 * 修改KT果盆配方在JEI中的显示方式，将流体从桶图标改为直接显示流体图标和毫升数
 */
@Mixin(value = PressingTubCategory.class, remap = false)
public abstract class PressingTubCategoryMixin {

    /**
     * 注入到setRecipe方法开始处，修改流体显示方式
     * <p>
     * 将输出槽从桶图标改为直接显示流体图标和毫升数
     * 
     * @param builder 配方布局构建器
     * @param recipe 果盆配方
     * @param focuses 焦点组
     * @param ci 回调信息
     */
    @Inject(method = "setRecipe", at = @At("HEAD"), remap = false, cancellable = true)
    public void onSetRecipeHead(IRecipeLayoutBuilder builder, PressingTubRecipe recipe, IFocusGroup focuses, CallbackInfo ci) {
        int needPressCount = IPressingTub.MAX_FLUID_AMOUNT / recipe.getFluidAmount();
        if (needPressCount * recipe.getFluidAmount() < IPressingTub.MAX_FLUID_AMOUNT) {
            needPressCount++;
        }

        for (Ingredient input : recipe.getIngredients()) {
            int finalNeedPressCount = needPressCount;
            List<ItemStack> list = Arrays.stream(input.getItems())
                    .map(s -> s.copyWithCount(finalNeedPressCount))
                    .toList();
            builder.addSlot(RecipeIngredientRole.INPUT, 32, 13)
                    .addIngredients(VanillaTypes.ITEM_STACK, list);
        }

        FluidStack fluidStack = new FluidStack(recipe.getFluid(), IPressingTub.MAX_FLUID_AMOUNT);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 128, 18)
                .addIngredient(ForgeTypes.FLUID_STACK, fluidStack)
                .setFluidRenderer(IPressingTub.MAX_FLUID_AMOUNT, false, 16, 16);

        ci.cancel();
    }
}
