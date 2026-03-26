package net.xdpp.kaleidoscopetfctavern.mixin;

import com.github.ysbbbbbb.kaleidoscopetavern.crafting.recipe.BarrelRecipe;
import com.github.ysbbbbbb.kaleidoscopetavern.compat.jei.category.BarrelRecipeCategory;
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
 * BarrelRecipeCategory 混合类
 * <p>
 * 修改KT酒桶配方在JEI中的显示方式，将流体从桶图标改为直接显示流体图标和毫升数
 */
@Mixin(value = BarrelRecipeCategory.class, remap = false)
public abstract class BarrelRecipeCategoryMixin {

    /**
     * 注入到setRecipe方法开始处，先清空builder的槽位
     */
    @Inject(method = "setRecipe", at = @At("HEAD"), remap = false, cancellable = true)
    public void onSetRecipeHead(IRecipeLayoutBuilder builder, BarrelRecipe recipe, IFocusGroup focuses, CallbackInfo ci) {
        int offsetX = 0;
        for (Ingredient input : recipe.getIngredients()) {
            List<ItemStack> list = Arrays.stream(input.getItems())
                    .map(s -> s.copyWithCount(16))
                    .toList();
            builder.addSlot(RecipeIngredientRole.INPUT, 30 + offsetX, 9)
                    .addIngredients(VanillaTypes.ITEM_STACK, list);
            offsetX += 18;
        }

        FluidStack fluidStack = new FluidStack(recipe.fluid(), 4000);
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 9)
                .addIngredient(ForgeTypes.FLUID_STACK, fluidStack)
                .setFluidRenderer(4000, false, 16, 16);

        builder.addSlot(RecipeIngredientRole.CATALYST, 84, 117)
                .addIngredients(recipe.carrier());

        ItemStack outputStack = recipe.result().copyWithCount(16);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 152, 86)
                .addItemStack(outputStack);

        ci.cancel();
    }
}
