package net.xdpp.kaleidoscopetfctavern.mixin;

import net.dries007.tfc.common.capabilities.ItemStackFluidHandler;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(value = ItemStackFluidHandler.class, remap = false)
public abstract class ItemStackFluidHandlerMixin {
    
    @Shadow
    @Final
    private Predicate<Fluid> allowedFluids;

    @Inject(method = "isFluidValid", at = @At("HEAD"), cancellable = true, remap = false)
    private void isFluidValid(int tank, FluidStack stack, CallbackInfoReturnable<Boolean> cir) {
        Fluid fluid = stack.getFluid();
        
        if (allowedFluids.test(fluid)) {
            cir.setReturnValue(true);
            return;
        }
        
        var fluidId = ForgeRegistries.FLUIDS.getKey(fluid);
        if (fluidId != null && fluidId.getNamespace().equals("kaleidoscopetfctavern")) {
            cir.setReturnValue(true);
            return;
        }
        
        cir.setReturnValue(false);
    }
}
