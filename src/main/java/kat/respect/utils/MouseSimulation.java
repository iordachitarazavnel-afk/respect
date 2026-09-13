package kat.respect.utils;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kat.respect.Respect;
import kat.respect.event.EventManager;
import kat.respect.event.events.ButtonListener;

public final class MouseSimulation {
   public static HashMap<Integer, Boolean> mouseButtons = new HashMap<>();
   public static ExecutorService clickExecutor = Executors.newFixedThreadPool(100);

   public static boolean isMouseButtonPressed(int keyCode) {
      Boolean key = mouseButtons.get(keyCode);
      return key != null ? key : false;
   }

   public static void mousePress(int keyCode) {
      mouseButtons.put(keyCode, true);
      EventManager.fire(new ButtonListener.ButtonEvent(keyCode, Respect.mc.method_22683().method_4490(), 1));
   }

   public static void mouseRelease(int keyCode) {
      mouseButtons.put(keyCode, false);
      EventManager.fire(new ButtonListener.ButtonEvent(keyCode, Respect.mc.method_22683().method_4490(), 0));
   }

   public static void mouseClick(int keyCode, int millis) {
      clickExecutor.submit(() -> {
         try {
            mousePress(keyCode);
            Thread.sleep(millis);
            mouseRelease(keyCode);
         } catch (InterruptedException var3) {
         }
      });
   }

   public static void mouseClick(int keyCode) {
      mouseClick(keyCode, 35);
   }
}
