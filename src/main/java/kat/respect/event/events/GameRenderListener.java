package kat.respect.event.events;

import java.util.ArrayList;
import kat.respect.event.Event;
import kat.respect.event.Listener;
import net.minecraft.class_4587;

public interface GameRenderListener extends Listener {
   void onGameRender(GameRenderListener.GameRenderEvent var1);

   class GameRenderEvent extends Event<GameRenderListener> {
      public class_4587 matrices;
      public float delta;

      public GameRenderEvent(class_4587 matrices, float delta) {
         this.matrices = matrices;
         this.delta = delta;
      }

      @Override
      public void fire(ArrayList<GameRenderListener> listeners) {
         listeners.forEach(e -> e.onGameRender(this));
      }

      @Override
      public Class<GameRenderListener> getListenerType() {
         return GameRenderListener.class;
      }
   }
}
