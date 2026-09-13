package kat.respect.mixin;

import kat.respect.Respect;
import kat.respect.imixin.IKeyBinding;
import net.minecraft.class_304;
import net.minecraft.class_3675;
import net.minecraft.class_3675.class_306;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(class_304.class)
public abstract class KeyBindingMixin implements IKeyBinding {
   @Shadow
   private class_306 field_1655;

   @Override
   public boolean isActuallyPressed() {
      int code = this.field_1655.method_1444();
      return class_3675.method_15987(Respect.mc.method_22683(), code);
   }

   @Override
   public void resetPressed() {
      this.method_23481(this.isActuallyPressed());
   }

   @Shadow
   public abstract void method_23481(boolean var1);
}
