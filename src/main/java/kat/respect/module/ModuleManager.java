package kat.respect.module;

import java.util.ArrayList;
import java.util.List;
import kat.respect.Respect;
import kat.respect.event.events.ButtonListener;
import kat.respect.module.modules.client.ClickGUI;
import kat.respect.module.modules.client.Friends;
import kat.respect.module.modules.client.SelfDestruct;
import kat.respect.module.modules.combat.AimAssist;
import kat.respect.module.modules.combat.AnchorMacro;
import kat.respect.module.modules.combat.AutoCrystal;
import kat.respect.module.modules.combat.AutoDoubleHand;
import kat.respect.module.modules.combat.AutoHitCrystal;
import kat.respect.module.modules.combat.AutoInventoryTotem;
import kat.respect.module.modules.combat.AutoJumpReset;
import kat.respect.module.modules.combat.AutoPot;
import kat.respect.module.modules.combat.AutoPotRefill;
import kat.respect.module.modules.combat.AutoWTap;
import kat.respect.module.modules.combat.CrystalOptimizer;
import kat.respect.module.modules.combat.DoubleAnchor;
import kat.respect.module.modules.combat.HoverTotem;
import kat.respect.module.modules.combat.NoMissDelay;
import kat.respect.module.modules.combat.SafeAnchor;
import kat.respect.module.modules.combat.ShieldDisabler;
import kat.respect.module.modules.combat.TotemOffhand;
import kat.respect.module.modules.combat.TriggerBot;
import kat.respect.module.modules.misc.AutoClicker;
import kat.respect.module.modules.misc.AutoXP;
import kat.respect.module.modules.misc.FakeLag;
import kat.respect.module.modules.misc.Freecam;
import kat.respect.module.modules.misc.KeyPearl;
import kat.respect.module.modules.misc.NoBreakDelay;
import kat.respect.module.modules.misc.NoJumpDelay;
import kat.respect.module.modules.misc.PackSpoof;
import kat.respect.module.modules.misc.PingSpoof;
import kat.respect.module.modules.misc.Prevent;
import kat.respect.module.modules.misc.Sprint;
import kat.respect.module.modules.render.HUD;
import kat.respect.module.modules.render.NoBounce;
import kat.respect.module.modules.render.PlayerESP;
import kat.respect.module.modules.render.StorageEsp;
import kat.respect.module.modules.render.TargetHud;
import kat.respect.module.setting.KeybindSetting;

public final class ModuleManager implements ButtonListener {
   private final List<Module> modules = new ArrayList<>();

   public ModuleManager() {
      this.addModules();
      this.addKeybinds();
   }

   public void addModules() {
      this.add(new AimAssist());
      this.add(new AnchorMacro());
      this.add(new AutoCrystal());
      this.add(new AutoDoubleHand());
      this.add(new AutoHitCrystal());
      this.add(new AutoInventoryTotem());
      this.add(new TriggerBot());
      this.add(new AutoPot());
      this.add(new AutoPotRefill());
      this.add(new AutoWTap());
      this.add(new CrystalOptimizer());
      this.add(new DoubleAnchor());
      this.add(new HoverTotem());
      this.add(new NoMissDelay());
      this.add(new ShieldDisabler());
      this.add(new TotemOffhand());
      this.add(new AutoJumpReset());
      this.add(new SafeAnchor());
      this.add(new Prevent());
      this.add(new AutoXP());
      this.add(new NoJumpDelay());
      this.add(new PingSpoof());
      this.add(new FakeLag());
      this.add(new AutoClicker());
      this.add(new KeyPearl());
      this.add(new NoBreakDelay());
      this.add(new Freecam());
      this.add(new PackSpoof());
      this.add(new Sprint());
      this.add(new HUD());
      this.add(new NoBounce());
      this.add(new PlayerESP());
      this.add(new StorageEsp());
      this.add(new TargetHud());
      this.add(new ClickGUI());
      this.add(new Friends());
      this.add(new SelfDestruct());
   }

   public List<Module> getEnabledModules() {
      return this.modules.stream().filter(Module::isEnabled).toList();
   }

   public List<Module> getModules() {
      return this.modules;
   }

   public void addKeybinds() {
      Respect.INSTANCE.getEventManager().add(ButtonListener.class, this);

      for (Module module : this.modules) {
         module.addSetting(new KeybindSetting("Keybind", module.getKey(), true).setDescription("Key to enabled the module"));
      }
   }

   public List<Module> getModulesInCategory(Category category) {
      return this.modules.stream().filter(module -> module.getCategory() == category).toList();
   }

   public <T extends Module> T getModule(Class<T> moduleClass) {
      return (T)this.modules.stream().filter(moduleClass::isInstance).findFirst().orElse(null);
   }

   public void add(Module module) {
      this.modules.add(module);
   }

   @Override
   public void onButtonPress(ButtonListener.ButtonEvent event) {
      if (!SelfDestruct.destruct) {
         this.modules.forEach(module -> {
            if (module.getKey() == event.button && event.action == 1) {
               module.toggle();
            }
         });
      }
   }
}
