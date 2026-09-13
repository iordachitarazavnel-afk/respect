package kat.respect.module.modules.client;

import kat.respect.Respect;
import kat.respect.event.events.PacketReceiveListener;
import kat.respect.gui.ClickGui;
import kat.respect.module.Category;
import kat.respect.module.Module;
import kat.respect.module.setting.BooleanSetting;
import kat.respect.module.setting.ModeSetting;
import kat.respect.module.setting.NumberSetting;
import net.minecraft.class_3944;
import net.minecraft.class_490;

public final class ClickGUI extends Module implements PacketReceiveListener {
   public static final NumberSetting scale = new NumberSetting("Scale", 0.5, 1.5, 0.75, 0.05);
   public static final BooleanSetting background = new BooleanSetting("Background", false).setDescription("Renders dim background overlay");
   public static final BooleanSetting customFont = new BooleanSetting("Custom Font", true);
   public static final BooleanSetting antiAliasing = new BooleanSetting("MSAA", true);
   public static final ModeSetting<ClickGUI.AnimationMode> animationMode = new ModeSetting<>(
      "Animations", ClickGUI.AnimationMode.Normal, ClickGUI.AnimationMode.class
   );
   private final BooleanSetting preventClose = new BooleanSetting("Prevent Close", true)
      .setDescription("For servers with freeze plugins that don't let you open the GUI");

   public ClickGUI() {
      super("Respect", "Settings for the client", 344, Category.CLIENT);
      this.addSettings(scale, background, this.preventClose, customFont);
   }

   @Override
   public void onEnable() {
      this.eventManager.add(PacketReceiveListener.class, this);
      Respect.INSTANCE.previousScreen = this.mc.field_1755;
      if (Respect.INSTANCE.clickGui != null) {
         this.mc.method_29970(Respect.INSTANCE.clickGui);
      } else if (this.mc.field_1755 instanceof class_490) {
         Respect.INSTANCE.guiInitialized = true;
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.eventManager.remove(PacketReceiveListener.class, this);
      if (this.mc.field_1755 instanceof ClickGui) {
         Respect.INSTANCE.clickGui.method_25419();
         this.mc.method_29970(Respect.INSTANCE.previousScreen);
         Respect.INSTANCE.clickGui.onGuiClose();
      } else if (this.mc.field_1755 instanceof class_490) {
         Respect.INSTANCE.guiInitialized = false;
      }

      super.onDisable();
   }

   @Override
   public void onPacketReceive(PacketReceiveListener.PacketReceiveEvent event) {
      if (Respect.INSTANCE.guiInitialized && event.packet instanceof class_3944 && this.preventClose.getValue()) {
         event.cancel();
      }
   }

   public enum AnimationMode {
      Normal,
      Positive,
      Off;
   }
}
