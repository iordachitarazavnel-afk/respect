package kat.respect.event.events;

import java.util.ArrayList;
import kat.respect.event.Event;
import kat.respect.event.Listener;

public interface TickListener extends Listener {
   void onTick();

   class TickEvent extends Event<TickListener> {
      @Override
      public void fire(ArrayList<TickListener> listeners) {
         listeners.forEach(TickListener::onTick);
      }

      @Override
      public Class<TickListener> getListenerType() {
         return TickListener.class;
      }
   }
}
