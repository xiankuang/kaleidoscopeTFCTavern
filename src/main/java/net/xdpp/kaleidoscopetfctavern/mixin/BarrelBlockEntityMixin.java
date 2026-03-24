package net.xdpp.kaleidoscopetfctavern.mixin;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.capabilities.InventoryFluidTank;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(value = BarrelBlockEntity.BarrelInventory.class, remap = false)
public abstract class BarrelBlockEntityMixin {
    
    @Inject(method = "<init>(Lnet/dries007/tfc/common/blockentities/BarrelInventoryCallback;)V", at = @At("RETURN"), remap = false)
    private void afterInit(CallbackInfo ci) {
        try {
            Object self = this;
            java.lang.reflect.Field tankField = self.getClass().getDeclaredField("tank");
            tankField.setAccessible(true);
            
            InventoryFluidTank originalTank = (InventoryFluidTank) tankField.get(self);
            
            Predicate<FluidStack> originalValidator = null;
            for (java.lang.reflect.Field field : originalTank.getClass().getSuperclass().getDeclaredFields()) {
                if (Predicate.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    originalValidator = (Predicate<FluidStack>) field.get(originalTank);
                    break;
                }
            }
            
            final Predicate<FluidStack> finalOriginalValidator = originalValidator;
            
            InventoryFluidTank newTank = new InventoryFluidTank(
                Helpers.getValueOrDefault(TFCConfig.SERVER.barrelCapacity),
                (FluidStack stack) -> {
                    if (finalOriginalValidator != null && finalOriginalValidator.test(stack)) {
                        return true;
                    }
                    var fluidId = ForgeRegistries.FLUIDS.getKey(stack.getFluid());
                    return fluidId != null && fluidId.getNamespace().equals("kaleidoscopetfctavern");
                },
                (net.dries007.tfc.common.capabilities.FluidTankCallback) self
            );
            
            tankField.set(self, newTank);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
