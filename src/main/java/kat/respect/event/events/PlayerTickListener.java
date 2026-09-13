package kat.respect.event.events;

import java.util.ArrayList;
import kat.respect.event.Event;
import kat.respect.event.Listener;

public interface PlayerTickListener extends Listener {
   void onPlayerTick();

   class PlayerTickEvent extends Event<PlayerTickListener> {
      @Override
      public void fire(ArrayList<PlayerTickListener> listeners) {
         listeners.forEach(PlayerTickListener::onPlayerTick);
      }

      @Override
      public Class<PlayerTickListener> getListenerType() {
         return PlayerTickListener.class;
      }
   }
}
