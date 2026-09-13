package kat.respect.mixin;

import kat.respect.event.EventManager;
import kat.respect.event.events.ButtonListener;
import kat.respect.event.events.MouseMoveListener;
import kat.respect.event.events.MouseUpdateListener;
import net.minecraft.class_310;
import net.minecraft.class_312;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_312.class)
public abstract class MouseMixin {
   @Shadow
   @Final
   private class_310 field_1779;
   @Unique
   private double respect$lastMouseX;
   @Unique
   private double respect$lastMouseY;
   @Unique
   private boolean respect$initialized;
   @Unique
   private int[] respect$buttonStates = new int[8];

   @Shadow
   public abstract double method_1603();

   @Shadow
   public abstract double method_1604();

   @Inject(method = "method_55793", at = @At("TAIL"))
   private void onMouseUpdate(CallbackInfo ci) {
      if (this.respect$buttonStates == null) {
         this.respect$buttonStates = new int[8];
      }

      EventManager.fire(new MouseUpdateListener.MouseUpdateEvent());
      long window = this.field_1779.method_22683().method_4490();
      double x = this.method_1603();
      double y = this.method_1604();
      if (!this.respect$initialized) {
         this.respect$initialized = true;
         this.respect$lastMouseX = x;
         this.respect$lastMouseY = y;

         for (int button = 0; button < this.respect$buttonStates.length; button++) {
            this.respect$buttonStates[button] = GLFW.glfwGetMouseButton(window, button);
         }
      } else {
         if (x != this.respect$lastMouseX || y != this.respect$lastMouseY) {
            this.respect$lastMouseX = x;
            this.respect$lastMouseY = y;
            EventManager.fire(new MouseMoveListener.MouseMoveEvent(window, x, y));
         }

         for (int button = 0; button < this.respect$buttonStates.length; button++) {
            int state = GLFW.glfwGetMouseButton(window, button);
            if (state != this.respect$buttonStates[button]) {
               this.respect$buttonStates[button] = state;
               EventManager.fire(new ButtonListener.ButtonEvent(button, window, state));
            }
         }
      }
   }
}
