package kat.respect;

import java.io.IOException;
import net.fabricmc.api.ModInitializer;

public final class Main implements ModInitializer {
   public void onInitialize() {
      try {
         new Respect();
      } catch (InterruptedException | IOException var2) {
      }
   }
}
