package kat.respect.mixin;

import kat.respect.Respect;
import kat.respect.module.modules.render.NoBounce;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_1799.class)
public class ItemStackMixin {
   @Inject(method = "method_7965", at = @At("HEAD"), cancellable = true)
   private void removeBounceAnimation(CallbackInfoReturnable<Integer> cir) {
      if (Respect.mc.field_1724 != null) {
         NoBounce noBounce = Respect.INSTANCE.getModuleManager().getModule(NoBounce.class);
         if (Respect.INSTANCE != null && Respect.mc.field_1724 != null && noBounce.isEnabled()) {
            class_1799 mainHandStack = Respect.mc.field_1724.method_6047();
            if (mainHandStack.method_31574(class_1802.field_8301)) {
               cir.setReturnValue(0);
            }
         }
      }
   }
}
