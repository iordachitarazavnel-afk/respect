package kat.respect.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import kat.respect.Respect;
import kat.respect.gui.nanovg.CategoryWindow;
import kat.respect.gui.nanovg.SearchBar;
import kat.respect.module.Category;
import kat.respect.module.modules.client.ClickGUI;
import kat.respect.util.render.nanovg.NanoVGContext;
import kat.respect.util.render.nanovg.NanoVGFrameManager;
import kat.respect.util.render.nanovg.NanoVGRenderer;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.lwjgl.glfw.GLFW;

public final class ClickGui extends class_437 {
   public final List<CategoryWindow> windows = new ArrayList<>();
   public final SearchBar searchBar = new SearchBar();
   public Color currentColor;

   public ClickGui() {
      super(class_2561.method_43473());
      this.initWindows();
   }

   private void initWindows() {
      this.windows.clear();
      float offsetX = 25.0F;
      float startY = 20.0F;
      float spacing = 168.0F;

      for (Category category : Category.values()) {
         this.windows.add(new CategoryWindow(offsetX, startY, category));
         offsetX += spacing;
      }

      this.windows.add(new CategoryWindow(offsetX, startY, "Menu", "assets/respect/icons/brush.png", CategoryWindow.WindowType.MENU));
      offsetX += spacing;
      this.windows.add(new CategoryWindow(offsetX, startY, "CONFIG", "assets/respect/icons/file-braces-corner.png", CategoryWindow.WindowType.CONFIGS));
   }

   public Color getAccentColor() {
      for (CategoryWindow window : this.windows) {
         if (window.type == CategoryWindow.WindowType.MENU) {
            return window.colorPicker.getColor();
         }
      }

      return new Color(124, 58, 237);
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
   }

   public void renderNanoVG() {
      if (Respect.mc.field_1755 == this) {
         class_310 mc = class_310.method_1551();
         if (mc != null && mc.method_22683() != null) {
            long handle = mc.method_22683().method_4490();
            int fbW = mc.method_22683().method_4480();
            int fbH = mc.method_22683().method_4507();
            if (handle != 0L && fbW > 0 && fbH > 0) {
               NanoVGFrameManager.beginFrame();
               long vg = NanoVGContext.getHandle();
               if (vg != 0L) {
                  double[] xpos = new double[1];
                  double[] ypos = new double[1];
                  GLFW.glfwGetCursorPos(handle, xpos, ypos);
                  double userScale = ClickGUI.scale.getValue();
                  if (userScale <= 0.1 || userScale > 2.0) {
                     userScale = 0.75;
                  }

                  double scaleFactor = mc.method_22683().method_4495() * userScale;
                  int mouseX = (int)(xpos[0] / scaleFactor);
                  int mouseY = (int)(ypos[0] / scaleFactor);
                  float screenW = (float)(mc.method_22683().method_4486() / userScale);
                  float screenH = (float)(mc.method_22683().method_4502() / userScale);
                  Color accentColor = this.getAccentColor();
                  String searchQuery = this.searchBar.getQuery();
                  if (ClickGUI.background.getValue()) {
                     NanoVGRenderer.drawRect(0.0F, 0.0F, screenW, screenH, new Color(0, 0, 0, 140));
                  }

                  for (CategoryWindow window : this.windows) {
                     window.render(vg, mouseX, mouseY, accentColor, searchQuery);
                  }

                  this.searchBar.render(vg, screenW, screenH, mouseX, mouseY, accentColor);
                  NanoVGFrameManager.endFrame();
               }
            }
         }
      }
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      double userScale = ClickGUI.scale.getValue();
      if (userScale <= 0.1) {
         userScale = 0.75;
      }

      double mouseX = click.comp_4798() / userScale;
      double mouseY = click.comp_4799() / userScale;
      int button = click.method_74245();
      float screenW = (float)(Respect.mc.method_22683().method_4486() / userScale);
      float screenH = (float)(Respect.mc.method_22683().method_4502() / userScale);
      if (this.searchBar.mouseClicked(mouseX, mouseY, button, screenW, screenH)) {
         return true;
      }

      for (int i = this.windows.size() - 1; i >= 0; i--) {
         CategoryWindow window = this.windows.get(i);
         if (window.mouseClicked(mouseX, mouseY, button, this.searchBar.getQuery())) {
            this.windows.remove(i);
            this.windows.add(window);
            return true;
         }
      }

      return super.method_25402(click, doubled);
   }

   public boolean method_25406(class_11909 click) {
      int button = click.method_74245();

      for (CategoryWindow window : this.windows) {
         window.mouseReleased(button);
      }

      return super.method_25406(click);
   }

   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      for (CategoryWindow window : this.windows) {
         window.mouseScrolled(mouseX, mouseY, verticalAmount);
      }

      return super.method_25401(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   public boolean method_25404(class_11908 keyInput) {
      int keyCode = keyInput.comp_4795();
      if (this.searchBar.keyPressed(keyCode)) {
         return true;
      }

      for (CategoryWindow window : this.windows) {
         if (window.keyPressed(keyCode)) {
            return true;
         }
      }

      return super.method_25404(keyInput);
   }

   public boolean method_25400(class_11905 charInput) {
      if (this.searchBar.charTyped((char)charInput.comp_4793())) {
         return true;
      }

      for (CategoryWindow window : this.windows) {
         if (window.charTyped((char)charInput.comp_4793())) {
            return true;
         }
      }

      return super.method_25400(charInput);
   }

   public boolean method_25421() {
      return false;
   }

   public void method_25419() {
      Respect.INSTANCE.getModuleManager().getModule(ClickGUI.class).setEnabledStatus(false);
      this.onGuiClose();
   }

   public void onGuiClose() {
      Respect.mc.method_29970(Respect.INSTANCE.previousScreen);
      this.searchBar.setFocused(false);
   }
}
