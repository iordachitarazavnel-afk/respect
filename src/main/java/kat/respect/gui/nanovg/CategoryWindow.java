package kat.respect.gui.nanovg;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kat.respect.Respect;
import kat.respect.module.Category;
import kat.respect.module.Module;
import kat.respect.util.render.nanovg.NanoVGImage;
import kat.respect.util.render.nanovg.NanoVGRenderer;
import org.lwjgl.nanovg.NanoVG;

public class CategoryWindow {
   public final CategoryWindow.WindowType type;
   public final Category category;
   public final String title;
   public final String iconPath;
   public float x;
   public float y;
   public float width = 155.0F;
   public float maxBodyHeight = 340.0F;
   public boolean dragging = false;
   private float dragX = 0.0F;
   private float dragY = 0.0F;
   public float scrollOffset = 0.0F;
   public final List<ModuleRow> moduleRows = new ArrayList<>();
   public final ColorPicker colorPicker = new ColorPicker(new Color(124, 58, 237));
   public boolean listeningMenuKey = false;
   public String configInputText = "";
   public boolean configInputFocused = false;
   private static String descriptionMode = "Mouse";
   private static final Map<String, Integer> iconCache = new HashMap<>();

   public CategoryWindow(float x, float y, Category category) {
      this.type = CategoryWindow.WindowType.CATEGORY;
      this.category = category;
      this.title = category.name.toString();
      this.iconPath = getCategoryIcon(category);
      this.x = x;
      this.y = y;

      for (Module module : Respect.INSTANCE.getModuleManager().getModules()) {
         if (module.getCategory() == category) {
            this.moduleRows.add(new ModuleRow(module));
         }
      }
   }

   public CategoryWindow(float x, float y, String title, String iconPath, CategoryWindow.WindowType type) {
      this.type = type;
      this.category = null;
      this.title = title;
      this.iconPath = iconPath;
      this.x = x;
      this.y = y;
   }

   private static String getCategoryIcon(Category cat) {
      if (cat == Category.COMBAT) {
         return "assets/respect/icons/sword.png";
      } else if (cat == Category.MISC) {
         return "assets/respect/icons/hammer.png";
      } else if (cat == Category.RENDER) {
         return "assets/respect/icons/eye.png";
      } else {
         return cat == Category.CLIENT ? "assets/respect/icons/lock-keyhole.png" : "assets/respect/icons/sword.png";
      }
   }

   private static int getOrLoadIcon(String path) {
      if (!iconCache.containsKey(path)) {
         int id = NanoVGImage.loadImage(path);
         iconCache.put(path, id);
      }

      return iconCache.get(path);
   }

   public float getHeaderHeight() {
      return 26.0F;
   }

   public float getContentHeight() {
      if (this.type == CategoryWindow.WindowType.MENU) {
         return 205.0F;
      }

      if (this.type == CategoryWindow.WindowType.CONFIGS) {
         int count = Respect.INSTANCE.getProfileManager().getAvailableConfigs().size();
         return 74.0F + count * 28.0F + 8.0F;
      }

      float total = 8.0F;

      for (ModuleRow row : this.moduleRows) {
         total += row.getHeight();
      }

      return total;
   }

   public void render(long vg, int mouseX, int mouseY, Color accentColor, String searchQuery) {
      float headerH = this.getHeaderHeight();
      float contentH = this.getContentHeight();
      float bodyH = Math.min(contentH, this.maxBodyHeight);
      float totalH = headerH + bodyH;
      Color shadowColor = new Color(0, 0, 0, 120);
      Color bodyBg = new Color(10, 8, 18, 230);
      Color headerBg = new Color(14, 11, 24, 245);
      NanoVGRenderer.drawRoundedRectWithShadow(this.x, this.y, this.width, totalH, 5.0F, bodyBg, shadowColor, 10.0F, 2.0F);
      NanoVGRenderer.drawRoundedRectVarying(this.x, this.y, this.width, headerH, 5.0F, 5.0F, 0.0F, 0.0F, headerBg);
      NanoVGRenderer.drawLine(
         this.x, this.y + headerH, this.x + this.width, this.y + headerH, 1.2F, accentColor != null ? accentColor : new Color(124, 58, 237)
      );
      float iconSize = 13.0F;
      float startX = this.x + 10.0F;
      float iconY = this.y + (headerH - iconSize) / 2.0F + 0.5F;
      float titleY = this.y + (headerH - 11.0F) / 2.0F - 0.5F;
      int iconId = getOrLoadIcon(this.iconPath);
      if (iconId != -1) {
         NanoVGImage.drawImage(iconId, startX, iconY, iconSize, iconSize, accentColor);
         startX += iconSize + 6.0F;
      }

      String displayTitle = this.title.toUpperCase();
      NanoVGRenderer.drawText(displayTitle, startX, titleY, 11.5F, Color.WHITE, true);
      int itemCount = 0;
      if (this.type == CategoryWindow.WindowType.CATEGORY) {
         itemCount = this.moduleRows.size();
      } else if (this.type == CategoryWindow.WindowType.CONFIGS) {
         itemCount = Respect.INSTANCE.getProfileManager().getAvailableConfigs().size();
      }

      if (this.type != CategoryWindow.WindowType.MENU) {
         String countStr = String.valueOf(itemCount);
         float countW = NanoVGRenderer.getTextWidth(countStr, 10.0F);
         NanoVGRenderer.drawText(countStr, this.x + this.width - 10.0F - countW, titleY + 0.5F, 10.0F, new Color(113, 113, 122), false);
      }

      boolean isHovered = mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + totalH;
      Color outlineColor = isHovered ? new Color(75, 50, 115, 220) : new Color(45, 30, 70, 180);
      NanoVGRenderer.drawRoundedRectOutline(this.x, this.y, this.width, totalH, 5.0F, 1.0F, outlineColor);
      float bodyY = this.y + headerH;
      NanoVG.nvgSave(vg);
      NanoVG.nvgIntersectScissor(vg, this.x, bodyY, this.width, bodyH);
      if (this.type == CategoryWindow.WindowType.CATEGORY) {
         this.renderCategoryContent(vg, this.x, bodyY - this.scrollOffset, this.width, mouseX, mouseY, accentColor, searchQuery);
      } else if (this.type == CategoryWindow.WindowType.MENU) {
         this.renderMenuContent(vg, this.x, bodyY, this.width, mouseX, mouseY, accentColor);
      } else if (this.type == CategoryWindow.WindowType.CONFIGS) {
         this.renderConfigsContent(vg, this.x, bodyY - this.scrollOffset, this.width, mouseX, mouseY, accentColor);
      }

      NanoVG.nvgRestore(vg);
      if (this.dragging) {
         this.x = mouseX - this.dragX;
         this.y = mouseY - this.dragY;
      }
   }

   private void renderCategoryContent(long vg, float contentX, float startY, float w, int mouseX, int mouseY, Color accentColor, String searchQuery) {
      float currentY = startY + 4.0F;
      String q = searchQuery != null ? searchQuery.toLowerCase().trim() : "";

      for (ModuleRow row : this.moduleRows) {
         if (q.isEmpty() || row.module.getName().toString().toLowerCase().contains(q)) {
            float rowH = row.getHeight();
            row.render(vg, contentX + 4.0F, currentY, w - 8.0F, mouseX, mouseY, accentColor);
            currentY += rowH;
         }
      }
   }

   private void renderMenuContent(long vg, float contentX, float startY, float w, int mouseX, int mouseY, Color accentColor) {
      float x = contentX + 10.0F;
      float y = startY + 8.0F;
      NanoVGRenderer.drawText("Menu Key:", x, y + 2.0F, 11.0F, new Color(190, 190, 205), false);
      String keyName = this.listeningMenuKey ? "[PRESS KEY]" : "Right Shift";
      float keyW = NanoVGRenderer.getTextWidth(keyName, 10.5F) + 14.0F;
      float keyX = x + w - 20.0F - keyW;
      boolean keyHover = mouseX >= keyX && mouseX <= keyX + keyW && mouseY >= y && mouseY <= y + 18.0F;
      NanoVGRenderer.drawRoundedRect(
         keyX, y, keyW, 18.0F, 4.0F, this.listeningMenuKey ? accentColor : (keyHover ? new Color(35, 35, 45) : new Color(22, 22, 28))
      );
      NanoVGRenderer.drawText(keyName, keyX + 7.0F, y + 3.5F, 10.0F, Color.WHITE, this.listeningMenuKey);
      y += 26.0F;
      NanoVGRenderer.drawText("Descriptions:", x, y + 2.0F, 11.0F, new Color(190, 190, 205), false);
      String descMode = descriptionMode;
      float descW = NanoVGRenderer.getTextWidth(descMode, 10.5F) + 14.0F;
      float descX = x + w - 20.0F - descW;
      boolean descHover = mouseX >= descX && mouseX <= descX + descW && mouseY >= y && mouseY <= y + 18.0F;
      NanoVGRenderer.drawRoundedRect(descX, y, descW, 18.0F, 4.0F, descHover ? new Color(35, 35, 45) : new Color(22, 22, 28));
      NanoVGRenderer.drawText(descMode, descX + 7.0F, y + 3.5F, 10.0F, Color.WHITE, false);
      y += 26.0F;
      NanoVGRenderer.drawText("Menu Color", x, y, 11.0F, new Color(190, 190, 205), true);
      NanoVGRenderer.drawCircle(x + w - 28.0F, y + 6.0F, 5.5F, this.colorPicker.getColor());
      y += 16.0F;
      this.colorPicker.width = w - 20.0F;
      this.colorPicker.render(vg, x, y, mouseX, mouseY);
   }

   private void renderConfigsContent(long vg, float contentX, float startY, float w, int mouseX, int mouseY, Color accentColor) {
      float x = contentX + 10.0F;
      float y = startY + 8.0F;
      float boxW = w - 20.0F;
      float inputY = y;
      float inputH = 24.0F;
      boolean inputHover = mouseX >= x && mouseX <= x + boxW && mouseY >= inputY && mouseY <= inputY + inputH;
      Color inputBg = new Color(18, 18, 24);
      Color inputOutline = this.configInputFocused ? accentColor : (inputHover ? new Color(255, 255, 255, 35) : new Color(255, 255, 255, 20));
      NanoVGRenderer.drawRoundedRect(x, inputY, boxW, inputH, 4.0F, inputBg);
      NanoVGRenderer.drawRoundedRectOutline(x, inputY, boxW, inputH, 4.0F, 1.0F, inputOutline);
      String displayText = this.configInputText.isEmpty()
         ? (this.configInputFocused ? "|" : "Name...")
         : this.configInputText + (this.configInputFocused ? "|" : "");
      Color textColor = this.configInputText.isEmpty() && !this.configInputFocused ? new Color(120, 120, 140) : Color.WHITE;
      NanoVGRenderer.drawText(displayText, x + 8.0F, inputY + 6.5F, 10.5F, textColor, false);
      float btnY = inputY + 28.0F;
      float btnH = 24.0F;
      boolean btnHover = mouseX >= x && mouseX <= x + boxW && mouseY >= btnY && mouseY <= btnY + btnH;
      Color btnBg = btnHover ? new Color(38, 38, 48) : new Color(26, 26, 34);
      NanoVGRenderer.drawRoundedRect(x, btnY, boxW, btnH, 4.0F, btnBg);
      NanoVGRenderer.drawRoundedRectOutline(x, btnY, boxW, btnH, 4.0F, 1.0F, new Color(255, 255, 255, 20));
      String saveText = "Save New";
      float saveW = NanoVGRenderer.getTextWidth(saveText, 11.0F);
      NanoVGRenderer.drawText(saveText, x + (boxW - saveW) / 2.0F, btnY + 6.5F, 11.0F, Color.WHITE, true);
      float listY = btnY + 32.0F;

      for (String cfgName : Respect.INSTANCE.getProfileManager().getAvailableConfigs()) {
         float rowY = listY;
         float rowH = 26.0F;
         NanoVGRenderer.drawText(cfgName, x + 2.0F, rowY + 7.5F, 10.5F, Color.WHITE, false);
         float actionBtnW = 18.0F;
         float actionBtnH = 18.0F;
         float gap = 3.0F;
         float delX = x + boxW - actionBtnW;
         float delY = rowY + 4.0F;
         boolean delHover = mouseX >= delX && mouseX <= delX + actionBtnW && mouseY >= delY && mouseY <= delY + actionBtnH;
         NanoVGRenderer.drawRoundedRect(delX, delY, actionBtnW, actionBtnH, 4.0F, delHover ? new Color(220, 38, 38) : new Color(24, 20, 40));
         NanoVGRenderer.drawRoundedRectOutline(delX, delY, actionBtnW, actionBtnH, 4.0F, 1.0F, new Color(255, 255, 255, 20));
         Color delIconColor = delHover ? Color.WHITE : new Color(161, 161, 170);
         NanoVGRenderer.drawLine(delX + 5.5F, delY + 5.5F, delX + 12.5F, delY + 12.5F, 1.4F, delIconColor);
         NanoVGRenderer.drawLine(delX + 12.5F, delY + 5.5F, delX + 5.5F, delY + 12.5F, 1.4F, delIconColor);
         float saveX = delX - actionBtnW - gap;
         float saveY = rowY + 4.0F;
         boolean saveHover = mouseX >= saveX && mouseX <= saveX + actionBtnW && mouseY >= saveY && mouseY <= saveY + actionBtnH;
         NanoVGRenderer.drawRoundedRect(saveX, saveY, actionBtnW, actionBtnH, 4.0F, saveHover ? accentColor : new Color(24, 20, 40));
         NanoVGRenderer.drawRoundedRectOutline(saveX, saveY, actionBtnW, actionBtnH, 4.0F, 1.0F, new Color(255, 255, 255, 20));
         Color saveIconColor = saveHover ? Color.WHITE : new Color(161, 161, 170);
         NanoVGRenderer.drawRoundedRectOutline(saveX + 6.0F, saveY + 4.0F, 6.0F, 6.0F, 3.0F, 1.2F, saveIconColor);
         NanoVGRenderer.drawRoundedRect(saveX + 5.0F, saveY + 8.0F, 8.0F, 6.0F, 1.5F, saveIconColor);
         float loadX = saveX - actionBtnW - gap;
         float loadY = rowY + 4.0F;
         boolean loadHover = mouseX >= loadX && mouseX <= loadX + actionBtnW && mouseY >= loadY && mouseY <= loadY + actionBtnH;
         NanoVGRenderer.drawRoundedRect(loadX, loadY, actionBtnW, actionBtnH, 4.0F, loadHover ? accentColor : new Color(24, 20, 40));
         NanoVGRenderer.drawRoundedRectOutline(loadX, loadY, actionBtnW, actionBtnH, 4.0F, 1.0F, new Color(255, 255, 255, 20));
         Color loadIconColor = loadHover ? Color.WHITE : new Color(161, 161, 170);
         NanoVGRenderer.drawLine(loadX + 4.5F, loadY + 9.5F, loadX + 7.5F, loadY + 12.5F, 1.5F, loadIconColor);
         NanoVGRenderer.drawLine(loadX + 7.5F, loadY + 12.5F, loadX + 13.0F, loadY + 5.5F, 1.5F, loadIconColor);
         listY += rowH + 2.0F;
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button, String searchQuery) {
      float headerH = this.getHeaderHeight();
      float contentH = this.getContentHeight();
      float bodyH = Math.min(contentH, this.maxBodyHeight);
      if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + headerH && button == 0) {
         this.dragging = true;
         this.dragX = (float)mouseX - this.x;
         this.dragY = (float)mouseY - this.y;
         return true;
      }

      float bodyY = this.y + headerH;
      if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= bodyY && mouseY <= bodyY + bodyH) {
         if (this.type == CategoryWindow.WindowType.CATEGORY) {
            float currentY = bodyY - this.scrollOffset + 4.0F;
            String q = searchQuery != null ? searchQuery.toLowerCase().trim() : "";

            for (ModuleRow row : this.moduleRows) {
               if (q.isEmpty() || row.module.getName().toString().toLowerCase().contains(q)) {
                  float rowH = row.getHeight();
                  if (mouseY >= currentY && mouseY <= currentY + rowH) {
                     return row.mouseClicked(mouseX, mouseY, button, this.x + 4.0F, currentY, this.width - 8.0F);
                  }

                  currentY += rowH;
               }
            }
         } else {
            if (this.type == CategoryWindow.WindowType.MENU) {
               float menuX = this.x + 10.0F;
               float keyY = bodyY + 8.0F;
               float keyW = NanoVGRenderer.getTextWidth(this.listeningMenuKey ? "[PRESS KEY]" : "Right Shift", 10.5F) + 14.0F;
               if (button == 0
                  && mouseX >= menuX + this.width - 20.0F - keyW
                  && mouseX <= menuX + this.width - 20.0F
                  && mouseY >= keyY
                  && mouseY <= keyY + 18.0F) {
                  this.listeningMenuKey = !this.listeningMenuKey;
                  return true;
               }

               float descY = keyY + 26.0F;
               float descW = NanoVGRenderer.getTextWidth(descriptionMode, 10.5F) + 14.0F;
               if (button == 0
                  && mouseX >= menuX + this.width - 20.0F - descW
                  && mouseX <= menuX + this.width - 20.0F
                  && mouseY >= descY
                  && mouseY <= descY + 18.0F) {
                  if (descriptionMode.equals("Mouse")) {
                     descriptionMode = "Always";
                  } else if (descriptionMode.equals("Always")) {
                     descriptionMode = "Off";
                  } else {
                     descriptionMode = "Mouse";
                  }

                  return true;
               }

               float cpY = descY + 26.0F + 16.0F;
               return this.colorPicker.mouseClicked(mouseX, mouseY, button, menuX, cpY);
            }

            if (this.type == CategoryWindow.WindowType.CONFIGS) {
               float cfgX = this.x + 10.0F;
               float boxW = this.width - 20.0F;
               float inputY = bodyY - this.scrollOffset + 8.0F;
               float inputH = 24.0F;
               if (button == 0 && mouseX >= cfgX && mouseX <= cfgX + boxW && mouseY >= inputY && mouseY <= inputY + inputH) {
                  this.configInputFocused = true;
                  return true;
               }

               if (button == 0) {
                  this.configInputFocused = false;
               }

               float btnY = inputY + 28.0F;
               float btnH = 24.0F;
               if (button == 0 && mouseX >= cfgX && mouseX <= cfgX + boxW && mouseY >= btnY && mouseY <= btnY + btnH) {
                  String name = this.configInputText.trim();
                  if (name.isEmpty()) {
                     name = "config_" + (Respect.INSTANCE.getProfileManager().getAvailableConfigs().size() + 1);
                  }

                  Respect.INSTANCE.getProfileManager().saveProfile(name);
                  this.configInputText = "";
                  this.configInputFocused = false;
                  return true;
               }

               float listY = btnY + 32.0F;
               List<String> configs = Respect.INSTANCE.getProfileManager().getAvailableConfigs();
               float actionBtnW = 18.0F;
               float actionBtnH = 18.0F;
               float gap = 4.0F;

               for (String cfgName : configs) {
                  float delX = cfgX + boxW - actionBtnW;
                  float delY = listY + 4.0F;
                  float saveX = delX - actionBtnW - gap;
                  float saveY = listY + 4.0F;
                  float loadX = saveX - actionBtnW - gap;
                  float loadY = listY + 4.0F;
                  if (button == 0) {
                     if (mouseX >= delX && mouseX <= delX + actionBtnW && mouseY >= delY && mouseY <= delY + actionBtnH) {
                        Respect.INSTANCE.getProfileManager().deleteProfile(cfgName);
                        return true;
                     }

                     if (mouseX >= saveX && mouseX <= saveX + actionBtnW && mouseY >= saveY && mouseY <= saveY + actionBtnH) {
                        Respect.INSTANCE.getProfileManager().saveProfile(cfgName);
                        return true;
                     }

                     if (mouseX >= loadX && mouseX <= loadX + actionBtnW && mouseY >= loadY && mouseY <= loadY + actionBtnH) {
                        Respect.INSTANCE.getProfileManager().loadProfile(cfgName);
                        return true;
                     }
                  }

                  listY += 28.0F;
               }
            }
         }
      }

      return false;
   }

   public void mouseReleased(int button) {
      if (button == 0) {
         this.dragging = false;
      }

      for (ModuleRow row : this.moduleRows) {
         row.mouseReleased(button);
      }

      this.colorPicker.mouseReleased(button);
   }

   public void mouseScrolled(double mouseX, double mouseY, double amount) {
      float headerH = this.getHeaderHeight();
      float bodyH = Math.min(this.getContentHeight(), this.maxBodyHeight);
      if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y + headerH && mouseY <= this.y + headerH + bodyH) {
         float maxScroll = Math.max(0.0F, this.getContentHeight() - this.maxBodyHeight);
         this.scrollOffset = Math.max(0.0F, Math.min(maxScroll, this.scrollOffset - (float)amount * 18.0F));
      }
   }

   public boolean keyPressed(int keyCode) {
      if (this.listeningMenuKey) {
         this.listeningMenuKey = false;
         return true;
      }

      if (this.configInputFocused) {
         if (keyCode == 259 && !this.configInputText.isEmpty()) {
            this.configInputText = this.configInputText.substring(0, this.configInputText.length() - 1);
            return true;
         }

         if (keyCode == 257 || keyCode == 335) {
            String name = this.configInputText.trim();
            if (name.isEmpty()) {
               name = "config_" + (Respect.INSTANCE.getProfileManager().getAvailableConfigs().size() + 1);
            }

            Respect.INSTANCE.getProfileManager().saveProfile(name);
            this.configInputText = "";
            this.configInputFocused = false;
            return true;
         }
      }

      for (ModuleRow row : this.moduleRows) {
         if (row.keyPressed(keyCode)) {
            return true;
         }
      }

      return false;
   }

   public boolean charTyped(char chr) {
      if (this.configInputFocused && chr >= ' ' && chr != 127 && this.configInputText.length() < 16) {
         this.configInputText = this.configInputText + chr;
         return true;
      } else {
         return false;
      }
   }

   public enum WindowType {
      CATEGORY,
      MENU,
      CONFIGS;
   }
}
