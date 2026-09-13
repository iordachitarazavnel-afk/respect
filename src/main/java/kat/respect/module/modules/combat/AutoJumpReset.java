package kat.respect.module.modules.combat;

import kat.respect.event.events.TickListener;
import kat.respect.module.Category;
import kat.respect.module.Module;
import kat.respect.module.setting.NumberSetting;
import kat.respect.utils.MathUtils;

public final class AutoJumpReset extends Module implements TickListener {
   private final NumberSetting chance = new NumberSetting("Chance", 0.0, 100.0, 100.0, 1.0);

   public AutoJumpReset() {
      super("Auto Jump Reset", "Automatically jumps for you when you get hit so you take less knockback (not good for crystal pvp)", -1, Category.COMBAT);
      this.addSettings(this.chance);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(TickListener.class, this);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(TickListener.class, this);
      super.onDisable();
   }

   @Override
   public void onTick() {
      if (MathUtils.randomInt(1, 100) <= this.chance.getValueInt()) {
         if (this.mc.field_1755 != null) {
            return;
         }

         if (this.mc.field_1724.method_6115()) {
            return;
         }

         if (this.mc.field_1724.field_6235 == 0) {
            return;
         }

         if (this.mc.field_1724.field_6235 == this.mc.field_1724.field_6254) {
            return;
         }

         if (!this.mc.field_1724.method_24828()) {
            return;
         }

         if (this.mc.field_1724.field_6235 == 9 && MathUtils.randomInt(1, 100) <= this.chance.getValueInt()) {
            this.mc.field_1724.method_6043();
         }
      }
   }
}
